package abstr;

/**
 * Represents an SMS
 * @author javier
 */
public class SMS {
    private final String msg;
    /**
     * Instantiates an SMS object.
     * @param msg The message
     */
    public SMS(String msg){
        this.msg = msg;
    }
    /**
     * Gets the message within an SMS
     * @return The message
     */
    public String getMessage(){
        return msg;
    }
    @Override
    public String toString(){
        return msg;
    }
}
