/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.utils;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;
import com.ai.common.*;

public class MailRequestExecutor implements ICreator { 

      /**
       * Invoked by the request executor servlet
       * args is a hashtable
       */
        public Object executeRequest(String requestName, Object args)
                throws RequestExecutionException
        {
           Hashtable params = null;
           if (args instanceof Vector)
           {
               Vector vParams = (Vector)args;
               params = (Hashtable)vParams.elementAt(0);
           }
           else if (args instanceof Hashtable)
           {
               params = (Hashtable)args;
           }
           else
           {
             throw new RequestExecutionException("Error: Wrong type of arguments. Hashtable expected");
           }

           try 
           { 
              MailSpec ms = getMailSpecFromConfig(requestName);
              AppObjects.info(this,"MailSpec after reading from config %1s", ms);
              updateMailSpecFromInputArgs(ms,params);
              AppObjects.info(this,"MailSpec after reading from input arguments ", ms);

              //get body url
              String bodyURL = getBodyURL(requestName);
              if (bodyURL != null)
              {
                 String bodyContent = TransformUtils.transform(bodyURL,params);
                 ms.setBodyText(bodyContent);
              }
           
              MailUtils.sendMail(ms); 
           }
           catch(javax.mail.MessagingException x)
           {
             throw new RequestExecutionException("Error: Could not send the mail message",x);
           }
           catch(TransformException x)
           {
             throw new RequestExecutionException("Error: Could not send the mail message",x);
           }
                     
           return new com.ai.application.interfaces.RequestExecutorResponse(true);
        }

        private MailSpec getMailSpecFromConfig(String requestName)
        {
           IConfig cfg = AppObjects.getIConfig();
           MailSpec ms = new MailSpec();
           ms.setSmtpMailHost (cfg.getValue(requestName + ".smtpMailHost"  ,cfg.getValue("Aspire.mail.smtpMailHost",null)));
           ms.setUser         (cfg.getValue(requestName + ".user"          ,cfg.getValue("Aspire.mail.user",null)));
           ms.setPassword     (cfg.getValue(requestName + ".password"      ,cfg.getValue("Aspire.mail.password",null)));
           ms.setFrom         (cfg.getValue(requestName + ".from"          ,cfg.getValue("Aspire.mail.from",null)));
           ms.setTo           (cfg.getValue(requestName + ".to",null));
           ms.setSubject      (cfg.getValue(requestName + ".subject",null));
           ms.setAttachmentFilename(cfg.getValue(requestName + ".attachmentFilename",null));
           ms.setBodyText     (cfg.getValue(requestName + ".bodyText",null));
           ms.setBodyMimeType (cfg.getValue(requestName + ".bodyMimeType","text/plain"));
           return ms;
   /*
           setsmtpMailHost
           user
           password
           from
           to
           subject
           attachmentFilename
           bodyText
           bodyMimeType
   */        
        }

        private String getBodyURL(String requestName)
        {
            return AppObjects.getIConfig().getValue(requestName + ".bodyURL",null);
        }
        
        private void updateMailSpecFromInputArgs(MailSpec ms, Hashtable args)
        {
            String smtpmailhost       = (String)args.get("smtpmailhost"   );
            String user               = (String)args.get("user"           );
            String password           = (String)args.get("password"       );
            String from               = (String)args.get("from"           );
            String to                 = (String)args.get("to"             );
            String subject            = (String)args.get("subject"        );
            String attachmentfilename =  (String)args.get("attachmentfilename");
            String bodytext           =  (String)args.get("bodytext");
            String bodymimetype       =  (String)args.get("bodymimetype");


        
           if (smtpmailhost != null)       ms.setSmtpMailHost (smtpmailhost       );
           if (user         != null)       ms.setUser         (user               );
           if (password     != null)       ms.setPassword     (password           );
           if (from         != null)       ms.setFrom         (from               );
           if (to           != null)       ms.setTo           (to                 );
           if (subject      != null)       ms.setSubject      (subject            );
           if (attachmentfilename != null) ms.setAttachmentFilename(attachmentfilename );
           if (bodytext           != null) ms.setBodyText     (bodytext           );
           if (bodymimetype       != null) ms.setBodyMimeType (bodymimetype       );
        }
} 