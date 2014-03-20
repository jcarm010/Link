package impl;

import abstr.SendSMSCommand;

/**
 * Creates implementation independent SMS related objects.
 * @author javier
 */
public class SMSFactory {//panthertext//Messages14!
    /**
     * Generates a SendSMSCommand.
     * @return A SendSMSCommand
     */
    public static SendSMSCommand getSMSCommand(){
        return new TwilioImplementation();
    }
}
