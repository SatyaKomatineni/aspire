/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.jawk;
import com.ai.common.*;
import org.apache.regexp.*;

/**
 * Reads stdin and substitutes in a target command
 *
 * dir /b | java CmdSubst copy $1 c:\temp > cp.bat
 *
 * will copy each of the input files to temp directory
 */
public class CmdSubst {

   public static void main(String[] args) 
   {
     try
     {
       // Get source string
       // Get Target String
        if (args.length < 2)
        {
          System.err.println("You have to submit a src string and a target string");
          return;
        }
        com.ai.common.FileUtils.runFileProcessorWith("stdin",new CmdSubstline(args));
      }
      catch(Exception x)
      {
         x.printStackTrace();
      }        
   }
   static String getCommandString(String[] args)
   {
      StringBuffer b = new StringBuffer();
      for(int i=0;i<args.length;i++)
      {
         b.append(args[0] + " ");
      }
      return b.toString();
   }
} 
class CmdSubstline extends AFileProcessorListener
{
    private Program m_program = null;
    
    CmdSubstline(String args[]) throws RESyntaxException
    {
      m_program = new Program(args);
    }
    public void newLine( final String line )
    {
      // transform line\
      String newline = m_program.execute(new WordEvaluator(line));
      System.out.println( newline );
        
    }
}

