package link;

import abstr.PhoneNumber;
import abstr.SMS;
import abstr.SendSMSCommand;
import abstr.exceptions.FailedToSendSMSException;
import impl.SMSFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * Main class to run Link
 * @author javier
 */
public class Link {
    public static void main(String[] args) {
        setRedirects(args);
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
        String result = req.sendResults(lst);
        System.out.println("Server Response: "+result);
    }
    
    /**
     * Redirects System.out and System.err to specified files if indicated by
     * command line arguments.
     * @param raw The raw array or command line arguments.
     */
    private static void setRedirects(String[] raw){
        String stdout = null,stderr=null;
        for(int i = 0 ; i < raw.length;i++){
            String s = raw[i];
            switch(s){
                case "-e": if(++i<raw.length)stderr = raw[i];break;
                case "-s": if(++i<raw.length)stdout = raw[i];break;
            }
        }
        if(stdout!=null) try {
            File out = new File(stdout);
            if(!out.exists()) out.createNewFile();
            System.setOut(new PrintStream(new FileOutputStream(out, true)));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        if(stderr!=null) try {
            File err = new File(stderr);
            if(!err.exists()) err.createNewFile();
            System.setErr(new PrintStream(new FileOutputStream(err, true)));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
