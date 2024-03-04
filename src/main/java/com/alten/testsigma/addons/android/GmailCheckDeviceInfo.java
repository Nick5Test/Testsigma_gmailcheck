package com.alten.testsigma.addons.android;

import com.alten.testsigma.addons.android.test.TestAndroidAction_Samsung;
import com.testsigma.sdk.AndroidAction;
import com.testsigma.sdk.ApplicationType;
import com.testsigma.sdk.Result;
import com.testsigma.sdk.annotation.Action;
import com.testsigma.sdk.annotation.TestData;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import lombok.Data;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Data
@Action(actionText = "Verify that the Message content testdata and device info on Gmail with EmailID and Password",
        applicationType = ApplicationType.ANDROID,
        useCustomScreenshot = false)

public class GmailCheckDeviceInfo extends AndroidAction {

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
        AndroidDriver androidDriver = (AndroidDriver) this.driver;
        String piattaforma = capitalizeFirstLetter(androidDriver.getCapabilities().getCapability("platformName").toString());
        String marca = capitalizeFirstLetter(androidDriver.getCapabilities().getCapability("deviceManufacturer").toString());
        String modello = (androidDriver.getCapabilities().getCapability("deviceModel").toString().toUpperCase());
        String devInfo = piattaforma + " " +marca + " " + modello;
        System.out.println(piattaforma + " " +marca + " " + modello);
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
                                        if(htmlContent.contains(this.targetPhrase.getValue().toString()) && htmlContent.contains(devInfo)){
                                            message.setFlag(Flags.Flag.SEEN, true);
                                            System.out.println("Phrase found and platform info match !");
                                            setSuccessMessage("Phrase found and platform info match !");
                                            result = Result.SUCCESS;
                                            phraseExists = true;
                                        }
                                    }
                                }
                            }
                        }
                    } else if (content instanceof String) {
                        if(((String) content).contains(this.targetPhrase.getValue().toString()) && ((String) content).contains(devInfo)){
                            message.setFlag(Flags.Flag.SEEN, true);
                            System.out.println("Phrase found and device info match !");
                            setSuccessMessage("Phrase found and device info match !");
                            result = Result.SUCCESS;
                            phraseExists = true;
                        }
                    }
                    if(phraseExists == false){
                        System.out.println("Phrase NOT found or device Info does NOT match !");
                        setErrorMessage("Phrase NOT found or device Info does NOT match !");
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

    public static String capitalizeFirstLetter(String input){
        if(input == null || input.isEmpty()){
            return input;
        }else {
            return input.substring(0,1).toUpperCase()+input.substring(1).toLowerCase();
        }
    }

    public static String minuscFirstLetter(String input){
        if(input == null || input.isEmpty()){
            return input;
        }else {
            return input.substring(0,1).toLowerCase()+input.substring(1).toUpperCase();
        }
    }
}