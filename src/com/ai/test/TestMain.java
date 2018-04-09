/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.test;
import java.sql.*;
import java.util.*;

import com.ai.data.*;
import com.ai.application.utils.*;
import com.ai.application.defaultpkg.*;
import com.ai.application.interfaces.*;
import com.ai.db.*;

public class TestMain {

        public TestMain() {
        }

        public static void main(String[] args) 
        {
            TestMain thisObj = new TestMain();
            thisObj.mainTest(args);
        }
        
        public void mainTest(String[] args)
        {
                ApplicationHolder.initApplication("g:\\cb\\com\\ai\\application\\test\\TestAppConfig.properties",args);
        AppObjects.log("Begin testing");
                TestMain testMain = new TestMain();
//                testMain.printDatabase("MyDatabase");
//                testMain.printDBDefinition("MyDatabase");
//            testMain.execCommand("shipoview_access_form.ShipmentTableHandler.query_request");
            testMain.execCommand("shipoview_form.OrdersOverviewHandler.query_request");
            
//            testMain.execCommand("");
         AppObjects.log("EndTestng");            
        }
        private void printDBDefinition( final String dbDefinition )
        {
        try {
            DBDefinition dbDef = new DBDefinition(dbDefinition);
            System.out.println("The database definition for \"" + dbDefinition + "\" is\n" 
               + dbDef );
         }
         catch(com.ai.application.interfaces.ConfigException x)
         {
            AppObjects.log(x);
         }               
        }

        private void printDatabase( final String databaseAlias )
        {
                try 
                {
                        com.ai.application.interfaces.IConfig cfg = AppObjects.getIConfig();
                        String database = 
                        cfg.getValue("Database.alias." + databaseAlias );
                        System.out.println("Details of database alias " 
                                           + databaseAlias                
                                           + " are as follows " );
                        System.out.println("Database name \t\t" + database);

                        System.out.println("Database driver \t\t"
                                + cfg.getValue("Database." + database + ".jdbc_driver" ));

                        System.out.println("Database connection string \t\t"
                                + cfg.getValue("Database." + database + ".connection_string" ));

                        System.out.println("Database user id \t\t"
                                + cfg.getValue("Database." + database + ".userid" ));

                        System.out.println("Database password \t\t"
                                + cfg.getValue("Database." + database + ".password" ));
                }
                catch( com.ai.application.interfaces.ConfigException x)
                {
                        AppObjects.log(x);
                }
                
        }
        private void execCommand(String command )
        {
         try
         {
          Object obj = AppObjects.getIFactory().getObject(command,null);
          IDataCollection coll = (IDataCollection)(obj);
          IIterator itr = coll.getIIterator();

          // walk through the meta data
          
          IMetaData metaData = coll.getIMetaData();
          IIterator metaItr = metaData.getIterator();
          
          AppObjects.log("Walking through the meta data");
          for (metaItr.moveToFirst();!metaItr.isAtTheEnd();metaItr.moveToNext())
          {
            String colName = (String)metaItr.getCurrentElement();
            AppObjects.log("Column name:" + colName );
            AppObjects.log("Column index: " + metaData.getIndex(colName));
          }
          AppObjects.log("End walking through the metadata");
          
          AppObjects.log("Walking through the rows retrieved");
          for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
          {
            Object row = itr.getCurrentElement();
            AppObjects.log(row.toString());
          }
          AppObjects.log("End Walking through the rows retrieved");
          
/*          IConfig cfg = AppObjects.getIConfig();
          String dbAlias = cfg.getValue("request." + command + ".db" );
          Connection sharedCon = ConnectionManager.getInstance().getConnection(dbAlias);
          
*/         }
         catch(Exception x)
         {
            AppObjects.log(x);
         }
         
          
        }
        DBDefinition getDBDefinition(String dbAliasName )
         throws com.ai.application.interfaces.ConfigException
        {
            return  new DBDefinition(dbAliasName);
        }
} 
