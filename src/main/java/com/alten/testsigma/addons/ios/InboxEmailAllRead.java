package com.alten.testsigma.addons.ios;
import com.testsigma.sdk.AndroidAction;
import com.testsigma.sdk.ApplicationType;
import com.testsigma.sdk.Result;
import com.testsigma.sdk.annotation.Action;
import com.testsigma.sdk.annotation.TestData;
import lombok.Data;
import org.openqa.selenium.NoSuchElementException;
import javax.mail.*;
import java.util.Properties;

@Data
@Action(actionText = "Verify that the Message content testdata on Gmail with EmailID and Password",
        applicationType = ApplicationType.IOS,
        useCustomScreenshot = false)
public class InboxEmailAllRead extends AndroidAction {

    @TestData(reference = "EmailID")
    public com.testsigma.sdk.TestData username;

    @TestData(reference = "Password")
    public com.testsigma.sdk.TestData password;


    @Override
    public com.testsigma.sdk.Result execute() throws NoSuchElementException {
        //Your Awesome code starts here
        com.testsigma.sdk.Result result = null;
        final String host = "imap.gmail.com";
        boolean phraseExists = false;

        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.setProperty("mail.imaps.auth", "true");
        properties.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties);

        try {
            //connessione server imap
            Store store = session.getStore("imap");
            store.connect(host, this.username.getValue().toString(), this.password.getValue().toString());
            //creazione folder object
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            for (Message message : messages){
                message.setFlag(Flags.Flag.SEEN, true);
            }
            System.out.println("All the emails in INBOX are now flagged as read");
            setErrorMessage("All the emails in INBOX are now flagged as read");
            result = Result.SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR Exception: "+e);
            setErrorMessage("ERROR Exception: "+e);
            result = Result.FAILED;

        }
        return result;
    }
}