/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.tools;


//import com.ai.xml.AITransform;
//import java.io.*;
//import com.ai.application.defaultpkg.ApplicationHolder;
//import com.ai.htmlgen.FormUtils;
//import com.ai.application.utils.AppObjects;
//import com.ai.servletutils.ServletUtils;
//import java.util.Hashtable;
//import java.util.Vector;
//import java.sql.Connection;
//import com.ai.db.IConnectionManager;
//import com.ai.application.interfaces.ConfigException;
//import com.ai.application.interfaces.RequestExecutionException;
//import com.ai.db.DBException;
//import com.ai.common.FileUtils;
//import com.ai.common.ITranslator;
//import com.ai.htmlgen.IFormHandler;
//import com.ai.htmlgen.IControlHandler1;

public class GenPageDefinition extends com.ai.common.ACommandLineApplication
{
   // Base class level obligations
   public GenPageDefinition(String[] args)
   {
      super(args);
   }
   protected int internalStart(String[] args)
   {
      // Real code for starting the application goes here
      // Initialize the app with the properties file
      return 0;
   }
  
   protected boolean  verifyArguments(String[] args)
   {
      if (args.length < 2)  return false;
      return true;
   }
   public static void main(String[] args)
   {
      // we need two arguments
      GenPageDefinition pd = new GenPageDefinition(args);
      pd.start();
   }
   protected String getHelpString()
   {
      String className = this.getClass().getName();
      String arg1 = "template filename";
      String line1 = "Command line:\t\t\t " 
         + className 
         + " [/h|/?] " 
         + arg1;
      return super.getHelpString() + "\n" + line1;
   }

   //**************************************************************************
   //* Real export application level logic
   //**************************************************************************
   //
   // 1. Design a dummy class derived from IFormHandler
   // 2. Write code to invoke the IAITransform with the following inputs
   //    . Input template file
   //    . Dummy form handler
   // 3. The dummy form handler should spit out on the screen the contents of the properties file.
}
