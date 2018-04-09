/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;

public abstract class ACommandLineApplication {

  private String[] m_args;
  
  public ACommandLineApplication(String[] args) 
  {
      m_args = args;
  }
  public final int start()
  {
      if (baseVerifyArguments(m_args ) == true)
      {
         return internalStart(m_args);
      }
      else
      {
         System.out.println(getHelpString());
      }
      return 0;
  }
  protected abstract int      internalStart(String[] args);
  protected boolean  verifyArguments(String[] args)
  {
   return true;
  }
  
  protected String getHelpString()
  {
      return "/h, /? - help";
  }
  
  private boolean baseVerifyArguments(String[] args)
  {
      if (args.length > 1)
      {
        if (args[0].equals("/h") || args[0].equals("/?"))
        {
  //         printHelp();
           return false;
        }
      }        
      return verifyArguments(args);
  }
  private void printHelp()
  {
   System.out.println(getHelpString());
  }
} 
