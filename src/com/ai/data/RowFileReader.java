/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;

public class RowFileReader implements ICreator {

        public RowFileReader() {
        }
        public Object executeRequest(String requestName, Object args)
                throws RequestExecutionException
        {
                java.io.BufferedReader fileReader = null;
                try 
                {       String filename;
                        try
                        {
                                filename = AppObjects.getIConfig().getValue(requestName + ".filename");
                        }
                        catch(ConfigException x)
                        {
                                AppObjects.log(x);
                                AppObjects.log("Error", "File name not in config file");
                                throw new RequestExecutionException (
                                        RequestExecutionException.LOADED_CLASS_CAN_NOT_EXECUTE_REQUEST );
                        }
                        String translatedFilename = com.ai.common.FileUtils.translateFileName(filename);
                        fileReader = new BufferedReader( new FileReader(translatedFilename) );

                        String line;
                        Vector lineVector = new Vector();
                        line = fileReader.readLine();
                        Vector metaFields = Tokenizer.tokenize(line,"|");
                        while( (line = fileReader.readLine()) != null)
                        {
                                lineVector.addElement(line);
                        }
                        return new VectorDataCollection( metaFields,lineVector );
                }
                catch (java.io.IOException x)
                {
                        AppObjects.log(x);
                }
                catch (com.ai.data.InvalidVectorDataCollection x)
                {
                        AppObjects.log(x);
                }
                finally
                {
                  if (fileReader != null)
                  {
                     try { fileReader.close();}
                     catch(java.io.IOException x)
                     {
                        AppObjects.log(x);
                     }
                  }
                }
                return null;
        }
} 