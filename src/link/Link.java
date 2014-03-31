package link;

import abstr.PhoneNumber;
import abstr.SMS;
import abstr.SendSMSCommand;
import abstr.exceptions.FailedToSendSMSException;
import impl.SMSFactory;
import java.util.List;

/**
 * Main class to run Link
 *
 * @author javier
 */
public class Link {
    public static void main(String[] args) {      
        Request req = new Request();
        SendSMSCommand comand = SMSFactory.getSMSCommand();
        List<Request.RequestData> lst = req.getPendingRequests();
        lst.stream().forEach((d) -> {
            try {
                comand.sendSMS(new SMS(d.txt), new PhoneNumber(d.to));
                d.flag = Request.FLAG.SENT;
            } catch (FailedToSendSMSException err) {
                err.printStackTrace(System.err);
                d.flag = Request.FLAG.FAILED;
            }
        });
        req.sendResults(lst);
    }
}
