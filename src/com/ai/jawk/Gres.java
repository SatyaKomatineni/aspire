/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.jawk;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import com.ai.common.StringUtils;

public class Gres {

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
        com.ai.common.FileUtils.runFileProcessorWithAFilename("stdin",new ReplaceLine(args[0],args[1]));
      }
      catch(Exception x)
      {
         x.printStackTrace();
      }        
   }
} 
class ReplaceLine extends AFileProcessorListener
{
    private String m_srcString = null;
    private String m_targetString = null;
    private RE m_srcExpr = null;
    
    ReplaceLine(String src, String target)
      throws RESyntaxException
    {
      m_srcString = src;
      m_targetString = StringUtils.decode(target,'\\',"<>%\"\'=","lgpdse");
      m_srcExpr = new RE(m_srcString);
    }
    public void newLine( final String line )
    {
      // transform line\
      String newline = m_srcExpr.subst(line,m_targetString);
      System.out.println( newline );
        
    }
}


