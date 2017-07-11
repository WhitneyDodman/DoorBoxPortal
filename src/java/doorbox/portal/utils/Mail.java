/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.utils;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author torinw
 */
public class Mail {
    //public static String DEFAULT_MAIL_HOST = "localhost";
    //public static String DEFAULT_MAIL_HOST = "smtp.portal.thedoorbox.com";
    public static String DEFAULT_MAIL_HOST = "outlook.office365.com";
    public static String DO_NOT_REPLY_ADDR = "do-not-reply@thedoorbox.com";
    //public static String DO_NOT_REPLY_ADDR = "sales@thedoorbox.com";
    
    public static void main(String[] args) {
        String htmlBody =
            "Hello " + "Torin Walker" + "<br/>" +
            "<br/>" +
            "The DoorBox owner <b>" + "accountPerson" + "</b> has granted you a temporary code to access his/her DoorBox for a pick up or delivery.<br/>" +
            "<table>" +
            "<tr><td><h4>Temporary Code:</h4></td><td><h4><b>" + "1122334455667788" + "</b></h4></td></tr>" +
            "<tr><td><h4>For doorbox:</h4></td><td><h4><b>" + "doorbox1"  + "</b></h4></td></tr>" +
            "<tr><td><h4>Valid from:</h4></td><td><h4><b>" + "2016-08-30 00:00:00 EDT" + "</b></h4></td></tr>" +
            "<tr><td><h4>Valid to:</h4></td><td><h4><b>" + "2016-08-31 11:00:00 EDT"  + "</b></h4></td></tr>" +
            "</table>" +
            "<br/>" +
            "If you received this in error, please accept our apologies and disregard this email.";
        
        //sendEmail("do-not-reply@portal.thedoorbox.com", "do-not-reply@thedoorbox.com", "Test", htmlBody);
        sendEmail("torinwalker@gmail.com", "do-not-reply@thedoorbox.com", "Test", htmlBody);
    }
    
    public static void sendEmail(String recipient, String sender, String subject, String htmlBody) {
        String replyTo = sender != null ? sender : DO_NOT_REPLY_ADDR;
        Properties properties = System.getProperties();
        properties.setProperty("mail.transport.protocol.rfc822", "smtp");
        properties.setProperty("mail.auth", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.debug.auth", "true");
        properties.setProperty("mail.smtp.host", DEFAULT_MAIL_HOST);
        properties.setProperty("mail.smtp.port", "25");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.protocol.host", DEFAULT_MAIL_HOST);
        properties.setProperty("mail.protocol.port", "25");
        properties.setProperty("mail.smtp.timeout", "15000");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        
        //properties.setProperty("mail.imap.port", "993");
        
        // Get the default Session object.
        Session session = Session.getInstance(properties,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("sales@thedoorbox.com", "Bruins2011");
                }
            });
        
        try {
            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            mp.addBodyPart(htmlPart);
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(replyTo));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);

            message.setContent(mp);

            Transport.send(message);
            System.out.println("Sent email successfully to " + recipient);
            
        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
