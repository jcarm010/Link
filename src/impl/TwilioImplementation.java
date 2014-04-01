package impl;

import abstr.PhoneNumber;
import abstr.SMS;
import abstr.SendSMSCommand;
import abstr.exceptions.FailedToSendSMSException;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.SmsFactory;
import com.twilio.sdk.resource.instance.Sms;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * Creates a Twilio implementation for SMS.
 * @author Henry
 */
public class TwilioImplementation implements SendSMSCommand {

    private static String ACCOUNT_SID = "ACe49b6b48575e9bc0e723d91faa603f69";
    private static String AUTH_TOKEN = "18e17a6957f91330416999355a2d3037";
    private static final Queue<String> fromNumbers = new LinkedList<>();
    private final TwilioRestClient client;

    /**
     * Constructs a TwilioImplementation for sending SMSs
     */
    public TwilioImplementation() {
        client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
        loadConfiguration();
    }

    @Override
    public final Map<PhoneNumber, List<SMS>> sendSMS(List<SMS> messages, List<PhoneNumber> toNumber) {
        Map<PhoneNumber, List<SMS>> failed = new HashMap<>();
        messages.stream().forEach((mes) -> {
            toNumber.stream().forEach((toNum) -> {
                try {
                    sendSMS(mes, toNum);
                } catch (FailedToSendSMSException ex) {
                    if (failed.containsKey(toNum)) {
                        failed.get(toNum).add(mes);
                    } else {
                        LinkedList<SMS> m = new LinkedList<>();
                        m.add(mes);
                        failed.put(toNum, m);
                    }
                }
            });
        });
        return failed;
    }

    @Override
    public final void sendSMS(SMS msg, PhoneNumber toNumber) throws FailedToSendSMSException {
        if(fromNumbers.isEmpty()) throw new RuntimeException("There are no numbers to send from.");
        String fNum = fromNumbers.poll();
        Map<String, String> params = new HashMap<>();
        params.put("Body", msg.getMessage());
        params.put("To", toNumber.getPhoneNumber());
        params.put("From", fNum);
        SmsFactory messageFactory = client.getAccount().getSmsFactory();
        try {
            Sms sms = messageFactory.create(params);
        } catch (TwilioRestException ex) { 
            fromNumbers.offer(fNum);
            throw new FailedToSendSMSException("Failed to send to " + toNumber.getPhoneNumber(), ex);
        } finally {
            fromNumbers.offer(fNum);
        }
    }
    /**
     * From numbers for testing purposes.
     */
    public enum TestingFrom {
        INVALID_PHONE("15005550001"),
        PHONE_NOT_OWNED("15005550007"),
        QUEUE_IS_FULL("15005550008"),
        NO_ERROR("15005550006");
        private final String value;
        TestingFrom(String str) {
            this.value = str;
        }
        public String getValue(){
            return value;
        }
    }
    /**
     * To numbers for testing purposes.
     */
    public enum TestingTo {
        INVALID_PHONE("15005550001"),
        CANNOT_ROUTE("15005550002"),
        NO_INTERNATIONAL_PERMISSION("15005550003"),
        BLACK_LISTED("15005550004"),
        SMS_NOT_SUPPORTED("15005550009"),
        NO_ERROR("15005550006");
        private final String value;
        TestingTo(String str) {
            this.value = str;
        }
        public String getValue(){
            return value;
        }
    }
    /**
     * Loads configuration from twilio_config file.
     */
    private static void loadConfiguration(){
        File file = new File(getExecutablePath()+"twilio_config");
        if(!file.exists()) writeConfigurationFile();
        Scanner scanner = null;
        try{
            scanner = new Scanner(file);
        }catch(FileNotFoundException err){
            err.printStackTrace(System.err);
        }
        if(scanner!=null && scanner.hasNext()){
            while(scanner.hasNext()){
                String line = scanner.nextLine();
                Option opt = getOption(line);
                switch(opt.getLeft()){
                    case "NUMBER":fromNumbers.offer(opt.right);break;
                    case "ACCOUNT_SID":ACCOUNT_SID = opt.right;break;
                    case "AUTH_TOKEN":AUTH_TOKEN = opt.right;break;
                }
            }
            scanner.close();
        }else{
            if(scanner!=null)scanner.close();
            writeConfigurationFile();
        }
    }
    
    private static void writeConfigurationFile(){
        File file = new File(getExecutablePath()+"twilio_config");
        FileWriter writer = null;
        try {
             writer = new FileWriter(file);
             for(String numb : fromNumbers){
                writer.write("NUMBER=");writer.write(numb);writer.write("\n");
             }
             writer.write("ACCOUNT_SID=");writer.write(ACCOUNT_SID);writer.write("\n");
             writer.write("AUTH_TOKEN=");writer.write(AUTH_TOKEN);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }finally{
            try {if(writer!=null)writer.close();} catch (IOException ex) {}
        }
    }
    
    private static Option getOption(String line){
        int hashIndex = line.indexOf("#");
        int equalsIndex = line.indexOf("=");
        if(equalsIndex<=0) return null;
        String left = line.substring(0,equalsIndex);
        String right = line.substring(equalsIndex+1,(hashIndex>0?hashIndex:line.length()));
        return new Option(left,right);
    }
    
    public static String getExecutablePath(){
        String pa = System.getProperty("java.class.path");
        if(pa.contains("Link"+System.getProperty("file.separator")+"build")) pa = ".";
        File f = new File(pa);
        File dir = f.getAbsoluteFile().getParentFile();
        String path = dir.toString()+System.getProperty("file.separator");
        return path;
    }
    
    private static class Option{
        private final String left;
        private final String right;
        public Option(String left,String right){
            this.left = left;
            this.right = right;
        }
        public String getLeft(){
            return left;
        }
        public String getRight(){
            return right;
        }
    }
}
