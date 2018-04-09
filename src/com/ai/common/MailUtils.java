/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import javax.mail.*;
import java.util.Properties;
import javax.mail.internet.*;
import javax.activation.*;
import com.ai.application.utils.*;

public class MailUtils 
{
   public static void sendAttachment(String smtpMailHost
                                    , String user
                                    , String password
                                    , String from
                                    , String to
                                    , String subject
                                    , String filename)
      throws javax.mail.internet.AddressException, javax.mail.MessagingException
   {
      // Get a session
      Properties props = new Properties();
      props.setProperty("mail.smtp.host",smtpMailHost);
      props.setProperty("mail.transport.protocol","smtp");
      props.setProperty("mail.user",user);
      
      SimpleMailAuthentication authenticator = null;
      if (password != null)
      {
         authenticator = new SimpleMailAuthentication(user,password); 
      }
      Session mailSession = Session.getDefaultInstance(props, authenticator);

      // Prepare the message
      MimeMessage message = new MimeMessage(mailSession);
      message.setFrom( new InternetAddress(from) );
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSubject(subject);

      // Add the attachment
      MimeBodyPart attachment = new MimeBodyPart();
      attachment.setDataHandler(new DataHandler(new FileDataSource(filename)));
      attachment.setFileName(filename);

      Multipart messageContent = new MimeMultipart();
      messageContent.addBodyPart(attachment);
      message.setContent(messageContent);
      mailSession.getTransport().send(message);
   }
   
   public static void sendMail(MailSpec ms)
      throws javax.mail.internet.AddressException, javax.mail.MessagingException
   {
      AppObjects.info("MailUtils.sendMail","Sending mail for spec %1s", ms);
      // Get a session
      Properties props = new Properties();
      props.setProperty("mail.smtp.host",ms.getSmtpMailHost());
      props.setProperty("mail.transport.protocol","smtp");
      props.setProperty("mail.user",ms.getUser());
      
      SimpleMailAuthentication authenticator = null;
      if (ms.getPassword() != null)
      {
         authenticator = new SimpleMailAuthentication(ms.getUser(),ms.getPassword()); 
      }
      Session mailSession = Session.getDefaultInstance(props, authenticator);

      // Prepare the message
      MimeMessage message = new MimeMessage(mailSession);
      message.setFrom( new InternetAddress(ms.getFrom()) );
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(ms.getTo()));
      message.setSubject(ms.getSubject());
      message.setContent(ms.getBodyText(),ms.getBodyMimeType()); // text/plain

      // Add the attachment if available
      if (ms.getAttachmentFilename() != null)
      { 
         MimeBodyPart attachment = new MimeBodyPart();
         attachment.setDataHandler(new DataHandler(new FileDataSource(ms.getAttachmentFilename())));
         attachment.setFileName(ms.getAttachmentFilename());
         
         Multipart messageContent = new MimeMultipart();
         messageContent.addBodyPart(attachment);
         message.setContent(messageContent);
      }
      
      mailSession.getTransport().send(message);
   }
} 

class SimpleMailAuthentication extends Authenticator
{
   private String m_user = null;
   private String m_password = null;
   SimpleMailAuthentication(String user, String password)
   {
      m_user = user;
      m_password = password;
   }
   protected PasswordAuthentication getPasswordAuthentication()
   {
      PasswordAuthentication pw = new PasswordAuthentication(m_user,m_password);
      return pw;
   }
}
