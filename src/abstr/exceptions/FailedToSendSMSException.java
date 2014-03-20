package abstr.exceptions;

/**
 * Exception to specify that a SMS could not be sent.
 * @author javier
 */
public class FailedToSendSMSException extends Exception{
    /**
     * Constructs an exception signaling that a SMS could not be sent.
     */
    public FailedToSendSMSException(){
        super();
    }
    /**
     * Constructs an exception signaling that a SMS could not be sent with 
     * a custom message.
     * @param str A message
     */
    public FailedToSendSMSException(String str){
        super(str);
    }
    /**
     * Constructs an exception signaling that a SMS could not be sent with 
     * a custom message and the cause for the exception.
     * @param str A message
     * @param cause The cause for this exception
     */
    public FailedToSendSMSException(String str,Throwable cause){
        super(str,cause);
    }
}
