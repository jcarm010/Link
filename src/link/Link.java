package link;

import abstr.PhoneNumber;
import abstr.SMS;
import abstr.SendSMSCommand;
import abstr.exceptions.FailedToSendSMSException;
import impl.SMSFactory;
import java.util.List;

/**
 * Main class to run Link
 * @author javier
 */
public class Link {
    public static void main(String[] args) {
        Request req = new Request();
        SendSMSCommand comand = SMSFactory.getSMSCommand();
        List<Request.RequestData> lst = req.getXML();
        for (Request.RequestData d : lst) {   
            try{
                comand.sendSMS(new SMS(d.txt), new PhoneNumber(d.to));
            }catch(FailedToSendSMSException err){
                err.printStackTrace(System.err);
                //do something when fails to send
            }
        }
    }
}
