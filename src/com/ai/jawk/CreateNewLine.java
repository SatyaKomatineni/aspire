/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.jawk;

public class CreateNewLine {

   public static void main(String[] args) 
   {
     try
     {
        com.ai.common.FileUtils.runFileProcessorWith("stdin",new CatFile1());
      }
      catch(Exception x)
      {
         x.printStackTrace();
      }        
   }
} 
class CatFile1 extends AFileProcessorListener
{
    public void newLine( final String line )
    {
        System.out.println( line );
        
    }
}

