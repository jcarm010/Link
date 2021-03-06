package link;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * This class is to make requests for new messages to send.
 * @author javier
 */
public class Request {
    private static String pUsername = null;
    private static String pPassword = null;
    public Request(){
        setCredentials();
    }
    private static void saveCredentials(){
        File file = new File("ptconfig");
        try (FileWriter writer = new FileWriter(file)) {
            String line = "username="+pUsername+"\n";
            writer.write(line);
            line = "password="+pPassword+"\n";
            writer.write(line);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    private static void setCredentials(){
        if(pUsername==null || pPassword==null){
            File file = new File("ptconfig");
            try (Scanner scanner = new Scanner(file)) {
                while(scanner.hasNext()){
                    String line = scanner.nextLine();
                    KeyValuePair pair = parseLine(line);
                    if(pair!=null)switch(pair.key){
                        case "username":Request.pUsername = pair.value;break;
                        case "password":Request.pPassword = pair.value;break;
                    }
                }
                scanner.close();
            } catch (FileNotFoundException ex) {
                saveCredentials();
            }
        }
    }
    private static KeyValuePair parseLine(String line){
        if(line==null) return null;
        int index = line.indexOf("=");
        if(index<0) return null;
        KeyValuePair pair = new KeyValuePair();
        pair.key = line.substring(0,index);
        pair.value = line.substring(index+1);
        return pair;
    }
    private static class KeyValuePair{
        public String key;
        public String value;
    }
    public static enum FLAG{
        SENT,FAILED,PENDING
    }
    /**
     * Stores data about a specific request
     */
    public class RequestData {
        public String to;
        public String txt;
        public String id;
        public FLAG flag;
        public RequestData(){
            flag = FLAG.PENDING;
        }
        @Override
        public String toString() {
            return "to: " + to + " text: " + txt + " id: " + id;
        }
    }
    /**
     * Retrieves all the pending requests from the web server.
     * @return A list with the data for all the pending requests or an empty
     * list if there are no pending requests.
     */
    public final List<RequestData> getPendingRequests() {
        List<RequestData> lst = new LinkedList<>();
        URL xmlUrl;
        try {
            xmlUrl = new URL("https://panthertext.com/scripts/query_requests.php");
            HttpsURLConnection con = (HttpsURLConnection)xmlUrl.openConnection();
            con.setRequestMethod("POST");
            String urlParameters = "username="+pUsername+"&password="+pPassword;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            InputStream in = con.getInputStream();
            Document doc = parse(in);
            NodeList messages = doc.getElementsByTagName("message");
            for (int i = 0; i < messages.getLength(); i++) {
                Node msg = messages.item(i);
                NodeList data = msg.getChildNodes();
                RequestData obj = new RequestData();
                for (int j = 0; j < data.getLength(); j++) {
                    Node inf = data.item(j);
                    switch (inf.getNodeName()) {
                        case "to": obj.to = inf.getTextContent(); break;
                        case "text": obj.txt = inf.getTextContent(); break;
                        case "id": obj.id = inf.getTextContent(); break;
                    }
                }
                lst.add(obj);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        } 
        return lst;
    }

    /**
     * Helper method designed to help retrieve the data from a server request.
     * @param is An input stream from the web server's response
     * @return An XML Document.
     */
    private Document parse(InputStream is) {
        Document ret = null;
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            domFactory.setNamespaceAware(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            ret = builder.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return ret;
    }
    /**
     * Sends results about a list of messages to the web server.
     * @param lst The list of requests whose results need to be sent.
     * @return The response from the server as a String.
     */
    public final String sendResults(List<RequestData> lst) {
        String someXmlContent = buildXMLResultString(lst);
        return sendXMLResults(someXmlContent);
    }
    /**
     * Formats a request results as an XML String.
     * @param lst The list of results to be formatted as XML.
     * @return The XML String.
     */
    private String buildXMLResultString(List<RequestData> lst){
        String output=null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("report");
            doc.appendChild(rootElement);
            // report elements
            lst.stream().forEach( d -> {
                Element request = doc.createElement("request");
                rootElement.appendChild(request);
                Element id = doc.createElement("id");
                id.setTextContent(d.id);
                request.appendChild(id);
                Element flag = doc.createElement("flag");
                flag.setTextContent(d.flag.toString());
                request.appendChild(flag);
            });
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            output = writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace(System.err);
        } 
        return output;
    }
    /**
     * Sends the results formatted as an XML String.
     * @param xmlString The XML String.
     * @return The response from the server.
     */
    private String sendXMLResults(String xmlString){
        String urlText = "https://panthertext.com/scripts/check_flag.php";
        try {
            URL obj = new URL(urlText);
            HttpsURLConnection con = (HttpsURLConnection)obj.openConnection();
            con.setRequestMethod("POST");
            String urlParameters = "XML="+xmlString+"&username="+pUsername+"&password="+pPassword;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) 
                    response.append(inputLine);
            in.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}