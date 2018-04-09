/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import java.io.BufferedReader;
import java.io.PrintWriter;

import com.ai.application.defaultpkg.ApplicationHolder;
import com.ai.application.utils.AppObjects;
import com.ai.common.TransformException;
import com.ai.common.Utils;
import com.ai.common.console.CLIConsole;
import com.ai.data.DataException;
import com.ai.data.FieldNameNotFoundException;
import com.ai.data.IDataCollection;
import com.ai.data.IIterator;
import com.ai.data.IMetaData;
import com.ai.generictransforms.DebugTextTransform;
import com.ai.htmlgen.ihds;
import com.ai.jawk.AFileProcessorListener;
import com.ai.jawk.FileProcessor;

public class TestCollection extends com.ai.common.ACommandLineApplication
{
   private PrintWriter m_log = null;
   private PrintWriter outWriter = null;

   // Base class level obligations
   public TestCollection(String[] args)
   {
      super(args);
   }
  protected int internalStart(String[] args)
  {
      try
      {
         String propertyFilename = null;
         if (args.length > 0)
         {
            propertyFilename = args[0];
         }
         else
         {
            propertyFilename = "g:\\cb\\com\\ai\\application\\test\\TestAppConfig.properties";
         }
         CLIConsole.base_info("Testing Collection from the properties file: \"" + propertyFilename + "\"" );
         ApplicationHolder.initApplication(propertyFilename,args);
         FileProcessor fp = new FileProcessor();
         fp.addFileProcessorListener(new DataRequestExecutorListener());
         fp.processFile("stdin");
         return 0;
      }
      catch(java.io.IOException x)
      {
         x.printStackTrace();
         return 1;
      }
  }

  protected boolean  verifyArguments(String[] args)
  {
//      if (args.length < 2)  return false;
      return true;
  }
  public static void main(String[] args)
  {
     // we need two arguments
     TestCollection app = new TestCollection(args);
     app.start();
  }

  protected String getHelpString()
  {
      String line1 = "Command line:\t\t\t java com.ai.test.TestCollection [/h|/?] <application property file>";
      String line3 = "\nProperty file: \tAbsolute path of the property filename.";
      return super.getHelpString() + "\n" + line1 + line3;
  }

  public static CommandRequest getCommandRequestFromInputLine(String line)
  {
	if (PartialCommandRequestSDO.isPartialCommandInPlace() == false)
	{
		return new CommandRequest(line);
	}
	//partial command in place
	PartialCommandRequestSDO pc = PartialCommandRequestSDO.getSingleInstance();
	return pc.createCommandRequest(line.trim());
  }
        public static void execCommand(String command )
        {
         //CommandRequest req = new CommandRequest(command);
         CommandRequest req = getCommandRequestFromInputLine(command);
         if (req.getCommand() == null)
         {
            CLIConsole.error("No command specified");
            return;
         }
         try
         {
          Object obj = AppObjects.getIFactory().getObject(req.getCommand()
                                                ,Utils.getAsHashtable(req.getArgs()));
          if (writeObjectToConsole(obj) == true)
          {
              return;
          }
          String queryType = AppObjects.getIConfig().getValue("request." + command + ".query_type","");
          if (!(obj instanceof IDataCollection))
          {
            AppObjects.log("Non collection.");
            CLIConsole.error("Non collection data");
            AppObjects.info("com.ai.test.TestCollection",obj.toString());
            return;
          }
          CLIConsole.info("Processed collection data can be found in the log file");
          writeDataCollection((IDataCollection)obj);
          
/*          IConfig cfg = AppObjects.getIConfig();
          String dbAlias = cfg.getValue("request." + command + ".db" );
          Connection sharedCon = ConnectionManager.getInstance().getConnection(dbAlias);

*/         }
         catch(Throwable x)
         {
            AppObjects.log(x);
         }
        }//eof method

        private static boolean writeObjectToConsole(Object o)
                throws TransformException
        {
            if (o instanceof ihds)
            {
                PrintWriter out = new PrintWriter(System.out);
                DebugTextTransform.staticTransform((ihds)o,out);

                out.flush();
                out.close();
                return true;  
            }
            else if (o instanceof String)
            {
            	CLIConsole.output((String)o);
            	return true;
            }
            else
            {
                return false;
            }
        }
        
        private static void writeDataCollection(IDataCollection coll)
        throws DataException, FieldNameNotFoundException
        {
        	try
			{
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
			}
        	finally
			{
        		coll.closeCollection();
			}

        }

} //eof class

class DataRequestExecutorListener extends AFileProcessorListener
{
   BufferedReader  m_reader;
    public void beginOfFile(BufferedReader reader )
    {
      m_reader = reader;
      CLIConsole.info("Testing starts" );
      CLIConsole.prompt("Please enter the name of a command to execute ");
    }
    public void newLine( final String line )
    {
       if (line.toLowerCase().equals("quit"))
       {
         CLIConsole.info("Application exiting ");
         System.exit(0);
       }
       CLIConsole.info("Executing " + line );
       long startTime = System.currentTimeMillis();
       try
       {
          TestCollection.execCommand(line);
       }
       catch(Throwable t)
       {
         t.printStackTrace();
       }
       long endTime = System.currentTimeMillis();
       CLIConsole.info("elapsed time : " + (endTime - startTime ) );
       CLIConsole.prompt("Please enter the name of a command to execute ");
    }
    public  void endOfFile()
    {
      CLIConsole.info("End of test ");
    }
}//eof-class
