/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/test/TestIApp.java

package com.ai.application.test;

import java.lang.String;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.ai.application.utils.AppObjects;
import com.ai.application.interfaces.*;
import com.ai.application.defaultpkg.ApplicationHolder;

// Testing RowFileReader
import com.ai.data.*;

/** TestIApp : Main class that executes test cases for IApp component.
 * Tests accessing the configuration files through IConfig
 */
public class TestIApp {

   /** Constructor for TestIApp
    * @see TestIApp
    */
    TestIApp() {
    }
    /** static main method of execution
     * @parameter args array of argument string
     * @return none
     */
    public static void main( String args[])
    {
      try
      {
       ApplicationHolder.initApplication("g:\\cb\\com\\ai\\application\\test\\TestAppConfig.properties", args);
       TestIAppAssistant.testConfig();
//       TestIAppAssistant.testLog();
//         TestMain.test();
//        TestIAppAssistant.testFactory();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
    }// end of main
}

class TestMain
{
   static void test() throws java.io.IOException
   {
         BufferedReader console = new BufferedReader( new InputStreamReader(System.in));
         String line = null;
         while((line = console.readLine()) != null)
         {
            System.out.println(line);       
         }
   }

}
class TestIAppAssistant
{
   final static String SQL_QUERY_1 = "TestForm.tableHandler.query_request";
   static void testLog()
   {
      AppObjects.log("COnsider this message also");
      AppObjects.log("Info","How about this message");      
   }
   static void testConfig()
   {
        try 
        {
              String numOfProperties = AppObjects.getIConfig().getValue("numberOfProperties");
              AppObjects.log("Info",numOfProperties);
        }
        catch (ConfigException x)
        {
                AppObjects.log(AppObjects.LOG_CRITICAL,x);
        }
   }
   static void testFactory()
   {
        try
        {
              Object o = AppObjects.getIFactory().getObject(SQL_QUERY_1,null);
              IDataCollection dc = (IDataCollection)o;
              IIterator itr = dc.getIIterator();

              Object curRow;
              for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
              {
                curRow = itr.getCurrentElement();
                AppObjects.log("Info",curRow.toString());
              }
        }      
        catch (RequestExecutionException x)
        {
                AppObjects.log(AppObjects.LOG_CRITICAL,x);
        }
        catch(com.ai.data.DataException x)
        {
               AppObjects.log(AppObjects.LOG_CRITICAL,x);
        }
   }
}
