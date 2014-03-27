package link;

import abstr.PhoneNumber;
import abstr.SMS;
import abstr.SendSMSCommand;
import abstr.exceptions.FailedToSendSMSException;
import impl.SMSFactory;
import impl.TwilioImplementation;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Request {
    public class RequestData {

        public String to;
        public String txt;
        public String id;

        public String toString() {
            return "to: " + to + " text: " + txt + " id: " + id;
        }
    }

    public List<RequestData> getXML() {
        List<RequestData> lst = new LinkedList<>();
        URL xmlUrl;
        try {
            xmlUrl = new URL("http://panthertext.com/scripts/query_requests.php");
            InputStream in = xmlUrl.openStream();
            Document doc = parse(in);
            NodeList messages = doc.getElementsByTagName("message");
            if (messages != null) {
                for (int i = 0; i < messages.getLength(); i++) {
                    Node msg = messages.item(i);
                    NodeList data = msg.getChildNodes();
                    RequestData obj = new RequestData();
                    for (int j = 0; j < data.getLength(); j++) {
                        Node inf = data.item(j);
                        switch (inf.getNodeName()) {
                            case "to":
                                obj.to = inf.getTextContent();
                                break;
                            case "text":
                                obj.txt = inf.getTextContent();
                                break;
                            case "id":
                                obj.id = inf.getTextContent();
                                break;
                        }
                    }
                    lst.add(obj);
                }
            } else {
                //todo
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lst;
    }

    public Document parse(InputStream is) {
        Document ret = null;
        DocumentBuilderFactory domFactory;
        DocumentBuilder builder;

        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setValidating(false);
            domFactory.setNamespaceAware(false);
            builder = domFactory.newDocumentBuilder();

            ret = builder.parse(is);
        } catch (Exception ex) {
            System.err.println("unable to load XML: " + ex);
        }
        return ret;
    }
}
