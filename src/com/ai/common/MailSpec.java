package com.ai.common;

public class MailSpec {

   // ************************
   // private data attributes
   // *************************
   private String	m_smtpMailHost;
   private String	m_user;
   private String	m_password;
   private String	m_from;
   private String	m_to;
   private String	m_subject;
   private String	m_attachmentFilename;
   private String	m_bodyText;
   private String	m_bodyMimeType;

   public MailSpec(){}
   public MailSpec (
            final	String	smtpMailHost,
            final	String	user,
            final	String	password,
            final	String	from,
            final	String	to,
            final	String	subject,
            final	String	attachmentFilename,
            final	String	bodyText,
            final	String	bodyMimeType
         )
      {
         // ************************
         // Assignments with in a constructor
         // *************************
         m_smtpMailHost = smtpMailHost;
         m_user = user;
         m_password = password;
         m_from = from;
         m_to = to;
         m_subject = subject;
         m_attachmentFilename = attachmentFilename;
         m_bodyText = bodyText;
         m_bodyMimeType = bodyMimeType;
      }// end of constructor

      
   // ************************
   // get methods
   // *************************
   public String	getSmtpMailHost(){ return m_smtpMailHost;}
   public String	getUser(){ return m_user;}
   public String	getPassword(){ return m_password;}
   public String	getFrom(){ return m_from;}
   public String	getTo(){ return m_to;}
   public String	getSubject(){ return m_subject;}
   public String	getAttachmentFilename(){ return m_attachmentFilename;}
   public String	getBodyText(){ return m_bodyText;}
   public String	getBodyMimeType(){ return m_bodyMimeType;}
   
   // ************************
   // set methods
   // *************************
   public void 	setSmtpMailHost			(final String	smtpMailHost){ m_smtpMailHost = smtpMailHost;}
   public void 	setUser			(final String	user){ m_user = user;}
   public void 	setPassword			(final String	password){ m_password = password;}
   public void 	setFrom			(final String	from){ m_from = from;}
   public void 	setTo			(final String	to){ m_to = to;}
   public void 	setSubject			(final String	subject){ m_subject = subject;}
   public void 	setAttachmentFilename			(final String	attachmentFilename){ m_attachmentFilename = attachmentFilename;}
   public void 	setBodyText			(final String	bodyText){ m_bodyText = bodyText;}
   public void 	setBodyMimeType			(final String	bodyMimeType){ m_bodyMimeType = bodyMimeType;}

   public String toString()
   {
      return m_smtpMailHost
            + "," + m_user
            + "," + m_password
            + "," + m_from
            + "," + m_to
            + "," + m_subject
            + "," + m_attachmentFilename
            + "," + m_bodyText
            + "," + m_bodyMimeType;
   }
} 
