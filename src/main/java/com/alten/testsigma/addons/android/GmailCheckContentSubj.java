package com.alten.testsigma.addons.android;

import com.testsigma.sdk.AndroidAction;
import com.testsigma.sdk.ApplicationType;
import com.testsigma.sdk.Result;
import com.testsigma.sdk.annotation.Action;
import com.testsigma.sdk.annotation.TestData;
import lombok.Data;
import org.openqa.selenium.NoSuchElementException;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
@Action(actionText = "Verify that the Message subject and content testdata on Gmail with EmailID and Password",
        applicationType = ApplicationType.ANDROID,
        useCustomScreenshot = false)

public class GmailCheckContentSubj extends AndroidAction {

    @TestData(reference = "testdata")
    public com.testsigma.sdk.TestData targetPhrase;

    @TestData(reference = "EmailID")
    public com.testsigma.sdk.TestData username;

    @TestData(reference = "Password")
    public com.testsigma.sdk.TestData password;

    @TestData(reference = "subject")
    public com.testsigma.sdk.TestData subject;


    @Override
    public Result execute() throws NoSuchElementException {
        //Your Awesome code starts here
        Result result = null;
        final String host = "imap.gmail.com";
        boolean phraseExists = false;
        boolean sub = false;
        int unreadMessages = 0, count = 0;

        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.setProperty("mail.imaps.auth", "true");
        properties.setProperty("mail.imap.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties);

        List<String> partList = null;
        if (this.subject.getValue().toString().contains("|")) {
            String[] parts = this.subject.getValue().toString().split("\\|");
            partList = new ArrayList<>();
            for (String part : parts) {
                partList.add(part);
            }
        }else {
            partList = new ArrayList<>();
            partList.add(this.subject.getValue().toString());
        }

        List<String> partListContent = null;
        if (this.targetPhrase.getValue().toString().contains("|")) {
            String[] parti = this.targetPhrase.getValue().toString().split("\\|");
            partListContent = new ArrayList<>();
            for (String parte : parti) {
                partListContent.add(parte);
            }
        }else {
            partListContent = new ArrayList<>();
            partListContent.add(this.targetPhrase.getValue().toString());
        }
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

            for (int k = messages.length - 1; k >= 0; k--) {
                count++;
                Message message = messages[messages.length - count];
                Flags flags = message.getFlags();
                if (!flags.contains(Flags.Flag.SEEN) && unreadMessages != 1) {
                    unreadMessages++;
                    System.out.println("messages.length---" + messages.length);
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("From: " + message.getFrom()[0]);
                    String subj = message.getSubject();
                    for (String part : partList) {
                        if (subj.contains(part)) {
                            System.out.println(part);
                            sub = true;
                        } else {
                            System.out.println("Subject DOES NOT match");
                            setErrorMessage("Subject DOES NOT match");
                            sub = false;
                            break;
                        }
                    }
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
                                        for(String parte : partListContent){
                                            if (htmlContent.contains(parte)) {
                                                message.setFlag(Flags.Flag.SEEN, true);
                                                setSuccessMessage("Subject match and Phrase found !");
                                                phraseExists = true;
                                            }else{
                                                setErrorMessage("Subject match but Phrase NOT found !");
                                                phraseExists = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (content instanceof String) {
                        for(String parte : partListContent) {
                            if (((String) content).contains(parte)) {
                                setSuccessMessage("Subject match and Phrase found !");
                                phraseExists = true;
                            } else {
                                setErrorMessage("Subject match but Phrase NOT found !");
                                phraseExists = false;
                                break;
                            }
                        }

                    }
                }
                message.setFlag(Flags.Flag.SEEN, true);
            }

            if (unreadMessages == 0) {
                System.out.println("0 NEW email found in INBOX ");
                setErrorMessage("0 NEW email found in INBOX ");
                result = Result.FAILED;
            } else {
                System.out.println(unreadMessages + " NEW email found unread in INBOX ");
                logger.info(unreadMessages + " NEW email found unread in INBOX ");
            }

            folder.close(false);
            store.close();

            if (sub==true && phraseExists == true) {
                setSuccessMessage("Subject and phrase matched");
                result= Result.SUCCESS;
            }else if(sub==true && phraseExists == false) {
                setErrorMessage("Subject match but phrase not found");
                result = Result.FAILED;
            }else if(sub==false && phraseExists == true) {
                setErrorMessage("Subject doesn't match but phrase found");
                result = Result.FAILED;
            }else {
                setErrorMessage("Subject and phrase don't found");
                result = Result.FAILED;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR Exception: " + e);
            setErrorMessage("ERROR Exception: " + e);
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