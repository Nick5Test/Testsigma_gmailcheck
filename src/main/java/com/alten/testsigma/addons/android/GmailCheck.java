package com.alten.testsigma.addons.android;

import com.testsigma.sdk.AndroidAction;
import com.testsigma.sdk.ApplicationType;
import com.testsigma.sdk.Result;
import com.testsigma.sdk.annotation.Action;
import com.testsigma.sdk.annotation.TestData;
import com.testsigma.sdk.annotation.Element;
import io.appium.java_client.android.AndroidDriver;
import lombok.Data;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Data
@Action(actionText = "Verify that the Message content testdata on Gmail with EmailID and Password",
        applicationType = ApplicationType.ANDROID,
        useCustomScreenshot = false)

public class GmailCheck extends AndroidAction {

    @TestData(reference = "testdata")
    public com.testsigma.sdk.TestData targetPhrase;

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
            //creazione folder object
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            //Message lastMessage = messages[messages.length - 1];
            //markAllAsRead(messages);
            for(Message message : messages){
                Flags flags = message.getFlags();
                if(!flags.contains(Flags.Flag.SEEN)) {
                    unreadMessages++;
                    System.out.println("messages.length---" + messages.length);
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + message.getFrom()[0]);
                    Object content = message.getContent();
                    if (content instanceof MimeMultipart) {
                        MimeMultipart multipart = (MimeMultipart) content;
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            if (bodyPart.isMimeType("multipart/related")) {
                                // Handle related multipart
                                MimeMultipart relatedMultipart = (MimeMultipart) bodyPart.getContent();
                                for (int j = 0; j < relatedMultipart.getCount(); j++) {
                                    BodyPart relatedBodyPart = relatedMultipart.getBodyPart(j);
                                    if (relatedBodyPart.isMimeType("text/html")) {
                                        // Print HTML content
                                        String htmlContent = (String) relatedBodyPart.getContent();
                                        if(htmlContent.contains(this.targetPhrase.getValue().toString())){
                                            message.setFlag(Flags.Flag.SEEN, true);
                                            System.out.println("Phrase found !");
                                            setSuccessMessage("Phrase found !");
                                            result = com.testsigma.sdk.Result.SUCCESS;
                                            phraseExists = true;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (content instanceof String) {
                        if(((String) content).contains(this.targetPhrase.getValue().toString())){
                            message.setFlag(Flags.Flag.SEEN, true);
                            System.out.println("Phrase found !");
                            setSuccessMessage("Phrase found !");
                            result = com.testsigma.sdk.Result.SUCCESS;
                            phraseExists = true;
                        }
                    }
                    if(phraseExists == false){
                        System.out.println("Phrase NOT found !");
                        setErrorMessage("Phrase NOT found !");
                        result = Result.FAILED;
                    }
                }
            }
            if(unreadMessages == 0){
                System.out.println("0 NEW email found in INBOX :( ");
                setErrorMessage("0 NEW email found in INBOX :( ");
                result = Result.FAILED;
            }else{
                System.out.println(unreadMessages+ " NEW email found unread in INBOX ");
                logger.info(unreadMessages+ " NEW email found unread in INBOX ");
            }
            folder.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR Exception: "+e);
            setErrorMessage("ERROR Exception: "+e);
            result = Result.FAILED;

        }
        return result;
    }
    public static void markAllAsRead (Message[] messages) throws MessagingException {
        for (Message message : messages){
            message.setFlag(Flags.Flag.SEEN, true);
        }
    }
}