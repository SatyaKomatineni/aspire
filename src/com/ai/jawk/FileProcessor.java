/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.jawk;

import java.util.Vector;
import java.util.Enumeration;
import java.io.*;

public class FileProcessor
{
    private Vector m_listeners = null;
    private String m_filename;

    public FileProcessor()
    {
        m_filename = null;
        m_listeners = new Vector();
    }
    public void processStream(InputStream stream) throws IOException
    {
          BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
          processReader(reader);
    }

    public void processReader(BufferedReader reader) throws IOException
    {
       try
       {
          callListenersWithBeginOfFile( reader );
          String newLine;
          while((newLine = reader.readLine()) != null)
          {
              callListeners( newLine );
          }
          callListenersWithEndOfFile();
        }
        finally
        {
           if (reader != null)
           {
              reader.close();
           }
        }
    }//eof-function

    public void processFile( final String filename ) throws IOException
    {
        Reader reader = null;
        BufferedReader bufReader = null;
        try
        {
           if(filename.equals("stdin"))
           {
               reader = new InputStreamReader(System.in);
           }
           else
           {
               reader = new FileReader(filename);
           }
           // open file
           bufReader = new BufferedReader( reader );
           callListenersWithBeginOfFile( bufReader );
           String newLine;
           while((newLine = bufReader.readLine()) != null)
           {
               callListeners( newLine );
           }
           callListenersWithEndOfFile();
         }
         finally
         {
            if (bufReader != null)
            {
               bufReader.close();
            }
         }
    }

    private void callListenersWithBeginOfFile(BufferedReader reader)
    {
         for (Enumeration e = m_listeners.elements() ; e.hasMoreElements() ;)
         {
            IFileProcessorListener fileListener = (IFileProcessorListener) e.nextElement();
            fileListener.beginOfFile(reader);
         }
    }
    private void callListenersWithEndOfFile()
    {
         for (Enumeration e = m_listeners.elements() ; e.hasMoreElements() ;)
         {
            IFileProcessorListener fileListener = (IFileProcessorListener) e.nextElement();
            fileListener.endOfFile();
         }
    }

    private void callListeners( final String inNewLine )
    {
         for (Enumeration e = m_listeners.elements() ; e.hasMoreElements() ;)
         {
            IFileProcessorListener fileListener = (IFileProcessorListener) (e.nextElement());
            fileListener.newLine( inNewLine );
         }
    }
    public void addFileProcessorListener(IFileProcessorListener listener)
    {
        m_listeners.addElement( listener );
    }
}
