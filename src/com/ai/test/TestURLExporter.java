/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import com.ai.servletutils.*;

public class TestURLExporter {

   public static void main(String[] args) {
      TestURLExporter testURLExporter = new TestURLExporter();
      testURLExporter.test();
   }
   private void test()
   {
      try
      {
      ServletUtils.exportURLToAFile("http://www.csx.com","d:\\work\\test.html");
      System.out.println("test complete");
      }
      catch(Throwable t)
      {
         t.printStackTrace();
      }
   }
}    
