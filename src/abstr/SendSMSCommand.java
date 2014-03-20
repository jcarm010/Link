package abstr;

import abstr.exceptions.FailedToSendSMSException;
import java.util.List;
import java.util.Map;
/**
 * Provides functionality to send SMS.
 * @author Henry
 */
public interface SendSMSCommand {
    /**
     * Takes a message as msg and sends it to the specified number in toNumber.
     * @param msg A message to be sent.
     * @param toNumber  A number to send the message to.
     * @throws FailedToSendSMSException When the message could not be sent 
     */
    public void sendSMS(SMS msg,PhoneNumber toNumber) throws FailedToSendSMSException;
    /**
     * Takes a list of phone numbers toNumbers and a list of messages and sends every message
     * in messages to every number in toNumbers.
     * @param messages
     * @param toNumbers
     * @return 
     */
    public Map<PhoneNumber,List<SMS>> sendSMS(List<SMS> messages, List<PhoneNumber> toNumbers);
}
