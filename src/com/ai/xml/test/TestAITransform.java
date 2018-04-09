/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.xml.test;

import com.ai.xml.*;
import java.io.*;
import com.ai.application.defaultpkg.ApplicationHolder;
import com.ai.htmlgen.FormUtils;
import com.ai.application.utils.AppObjects;
import com.ai.servletutils.ServletUtils;
import java.util.Hashtable;
import java.util.Vector;
import java.sql.Connection;
import com.ai.db.IConnectionManager;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.db.DBException;
import com.ai.common.FileUtils;

public class TestAITransform {

  public static void main(String[] args)
  {
   //      testExport();
      oracleTest();
  }
  static public void oracleTest()
  {
   try
   {
      System.out.println("test begin");
      ApplicationHolder.initApplication(
                 "g:\\cb\\com\\ai\\xml\\test\\test.properties",null);
      Object obj = AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null);
      IConnectionManager cMan = (IConnectionManager)obj;
      Connection con  = cMan.getConnection("gabor_db");
      cMan.putConnection(con);
      System.out.println("test end");
    }
    catch(Throwable t)
    {
      t.printStackTrace();
    }      
  }                                  
  static public void testExport()
  {
   try
   {      
      ApplicationHolder.initApplication(
                 "g:\\cb\\com\\ai\\xml\\test\\test.properties",null);
      TestAITransform exp = new TestAITransform();
      exp.performExport("asn_export");
   }
   catch(Throwable t)
   {
      AppObjects.log(t);
   }      
  }
  static public void mainTest()
  {
        try {
                ApplicationHolder.initApplication(
                        "g:\\cb\\com\\ai\\xml\\test\\test.properties",null);
                PrintWriter outWriter = new PrintWriter(
                                                new BufferedWriter(
                                                        new FileWriter(
                                        "d:\\work\\out.html")),true);

                System.out.println("Test begins");
                AITransform.processHtmlPage( "g:\\cb\\com\\ai\\xml\\test\\ai_transform_test.htm"
                                        ,outWriter
                                        ,FormUtils.getFormHandlerFor("TestForm",null)
                                        );

                outWriter.close();
                System.out.println("Test completed");

        }
        catch(Exception x)
        {
                x.printStackTrace();
        }
  }

  public void performExport(String exportSpec)
  {
      boolean bRollBack = false;
      Connection con = null;
      try
      {
         String formName      = AppObjects.getIConfig().getValue(exportSpec + ".formHandlerName");
         String outFileName   = FileUtils.translateFileName(
                  AppObjects.getIConfig().getValue(exportSpec + ".outputFileName"));
         String templateFile  = FileUtils.translateFileName(
                  AppObjects.getIConfig().getValue(exportSpec + ".template"));
                  
         String dataSourceName  = AppObjects.getIConfig().getValue(exportSpec + ".db", null);
                  
         String argsString = AppObjects.getIConfig().getValue(exportSpec + ".args",null);
         Hashtable args = createArgs(argsString);
         args.put("export_filename",outFileName);

         // obtain a connection
         if (dataSourceName != null)
         {
            con = getConnection(dataSourceName);
            con.setAutoCommit(false);
            args.put("aspire.reserved.jdbc_connection",con);
         }            
         else
         {
            AppObjects.log("Datasource name not specified for export");
//            return;
         }
         // Get an output stream for export         
         PrintWriter outWriter = new PrintWriter(
                                       new BufferedWriter(
                                               new FileWriter(outFileName))
                                       ,true);

         AITransform.processHtmlPage( templateFile
                               ,outWriter
                               ,FormUtils.getFormHandlerFor(formName,args)
                               );
         // Commit the connection
         outWriter.flush();
         outWriter.close();
         if (con != null)
         { 
            AppObjects.log("db: Commiting the connection");  
            con.commit();
          }  
//         putConnection(con);                                        
      }
      catch (ConfigException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (IOException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (RequestExecutionException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (java.sql.SQLException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      catch (DBException x)
      {
         bRollBack = true;
         AppObjects.log(x);
      }
      finally                
      {
         if (con != null)
            rollbackAndReturnConnection(con);
      }
  }
  private void rollbackAndReturnConnection(Connection con)
  {
     if (con == null) return;
     try
     {
        con.rollback();
        putConnection(con);
      }
      catch(java.sql.SQLException x)
      {
         AppObjects.log(x);
         AppObjects.log("Could not rollback the connection");
      }        
      catch(RequestExecutionException x)
      {
         AppObjects.log(x);
         AppObjects.log("Could not return the connection");
      }        
      catch(DBException x)
      {
         AppObjects.log(x);
         AppObjects.log("Could not return the connection");
      }        
  }                  
  
  public Hashtable createArgs( String argsString )
  {
      Hashtable args = new Hashtable();
      if (argsString == null)
      {
         return args;
      }
      return args;
  }
  public Connection getConnection(String datasourceName)
   throws RequestExecutionException, DBException
  {
      IConnectionManager conMan = 
         (IConnectionManager)(AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null));
      return conMan.getConnection(datasourceName);
  }
  public void putConnection(Connection con)
   throws RequestExecutionException, DBException
  {
      IConnectionManager conMan = 
         (IConnectionManager)(AppObjects.getIFactory().getObject(IConnectionManager.GET_CONNECTION_MANAGER_REQUEST,null));
      conMan.putConnection(con);
  }
  
}

