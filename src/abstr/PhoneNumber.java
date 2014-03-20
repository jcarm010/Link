package abstr;

import abstr.exceptions.InvalidNumberException;

/**
 * Represents a phone number.
 * @author javier
 */
public class PhoneNumber {
    private String phoneNumber;
    /**
     * Instantiates a PhoneNumber object.
     * @param number The phone number
     */
    public PhoneNumber(String number){
        if(number==null) throw new InvalidNumberException(number);
        this.phoneNumber = number;
    }
    /**
     * Gets the phone number in this object.
     * @return The phone number
     */
    public String getPhoneNumber(){
        return phoneNumber;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.phoneNumber != null ? this.phoneNumber.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object obj){
        if(!this.getClass().equals(obj.getClass()))
            return false;
        PhoneNumber p = (PhoneNumber)obj;
        return p.phoneNumber.equals(this.phoneNumber);
    }
    @Override
    public String toString(){
        return phoneNumber;
    }
}
