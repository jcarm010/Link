package link;

import abstr.PhoneNumber;
import abstr.SMS;
import abstr.SendSMSCommand;
import impl.SMSFactory;
import impl.TwilioImplementation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Main class to run Link
 * @author javier
 */
public class Link {
    public static void main(String[] args) throws Exception{
        
        SendSMSCommand test = SMSFactory.getSMSCommand();
        List<PhoneNumber> toNum = new ArrayList<>();
        List<SMS> msg = new ArrayList<>();
        
        toNum.add(new PhoneNumber(TwilioImplementation.TestingTo.NO_ERROR.getValue()));
        
        msg.add(new SMS("Hello, this is the first test"));
        msg.add(new SMS("Hello, this is the second test"));
        
        Map<PhoneNumber,List<SMS>> failed = test.sendSMS(msg,toNum);
        for(Entry<PhoneNumber,List<SMS>> entry : failed.entrySet()){
            System.out.println("Failed - "+entry.getKey()+": "+entry.getValue());
        }        
    }
}
