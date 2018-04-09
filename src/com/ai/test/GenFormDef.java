/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import java.util.Properties;
import java.io.*;
import java.io.BufferedReader;
import java.util.*;

public class GenFormDef {

  Properties formProps = new Properties();
  
  static final String URL_NAME                     = "URL_NAME";
  static final String TEMPLATE_FILE                = "TEMPLATE_FILE";
  static final String MAIN_DATA_REQUEST            = "MAIN_DATA_REQUEST";
  static final String NUMBER_OF_LOOP_HANDLERS      = "NUMBER_OF_LOOP_HANDLERS";
  static final String LOOP_HANDLER                 = "LOOP_HANDLER";

  private BufferedReader m_in = new BufferedReader( new InputStreamReader(System.in));
  
  public GenFormDef() 
  {
   formProps.put(this.TEMPLATE_FILE              ,this.TEMPLATE_FILE          );
   formProps.put(this.URL_NAME                   ,this.URL_NAME               );
   formProps.put(this.MAIN_DATA_REQUEST          ,this.MAIN_DATA_REQUEST      );
   formProps.put(this.NUMBER_OF_LOOP_HANDLERS    ,this.NUMBER_OF_LOOP_HANDLERS);
   formProps.put(this.LOOP_HANDLER               ,this.LOOP_HANDLER           );
  }

  public static void main(String[] args) {
    GenFormDef genFormDef = new GenFormDef();
    genFormDef.generate();
  }
  void generate()
  {
   try
   {
      for(Enumeration e=formProps.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         System.out.println("Please specify :" + key );
         System.out.flush();
         String line = m_in.readLine();
         formProps.put(key,line);
      }
      System.out.println("Here are your specified values ");
      for(Enumeration e=formProps.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         System.out.println( key + ":" + formProps.get(key) );
      }
      System.out.println("Type any key to exit the program ");
      m_in.readLine();
    }
    catch(java.io.IOException x)
    {
      x.printStackTrace();
    }  
      
  }
} 
