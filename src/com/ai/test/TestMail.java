/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import com.ai.common.*;
public class TestMail {

   public static void main(String[] args) 
   {
      try
      {
      MailUtils.sendAttachment("mail.csx.com"
               ,"w7788"
               ,null
               ,"satya_komatineni@csx.com"
               ,"satya_komatineni@csx.com"
               ,"test subject"
               ,"d:\\work\\test.html");
      }
      catch(Throwable t)
      {
         t.printStackTrace();
      }               
   }
} 
