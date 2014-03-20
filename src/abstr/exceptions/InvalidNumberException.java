package abstr.exceptions;

/**
 * Exeption to specify that a phone number is invalid.
 * @author javier
 */
public class InvalidNumberException extends RuntimeException{
    
    /**
     * Constructs a checked exception specifying that a phone number is invalid.
     */
    public InvalidNumberException(){
        super();
    }
    
    /**
     * Constructs a checked exception specifying that a phone number is invalid
     * and a custom message.
     * @param msg A message
     */
    public InvalidNumberException(String msg){
        super(msg);
    }
}
