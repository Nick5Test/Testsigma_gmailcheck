package com.alten.testsigma.addons.android;

import com.sun.mail.imap.protocol.FLAGS;
import com.testsigma.sdk.AndroidAction;
import com.testsigma.sdk.ApplicationType;
import com.testsigma.sdk.Result;
import com.testsigma.sdk.annotation.Action;
import com.testsigma.sdk.annotation.TestData;
import lombok.Data;
import org.openqa.selenium.NoSuchElementException;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Data
@Action(actionText = "Verify that the Message content testdata on Gmail with EmailID and Password",
        applicationType = ApplicationType.ANDROID,
        useCustomScreenshot = false)

public class GmailAttachment extends AndroidAction {

    @TestData(reference = "testdata")
    public com.testsigma.sdk.TestData targetPhrase;

    @TestData(reference = "EmailID")
    public com.testsigma.sdk.TestData username;

    @TestData(reference = "Password")
    public com.testsigma.sdk.TestData password;


    @Override
    public Result execute() throws NoSuchElementException {
        //Your Awesome code starts here
        Result result = null;
        final String host = "imap.gmail.com";
        boolean phraseExists = false;
        int unreadMessages = 0;

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
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}