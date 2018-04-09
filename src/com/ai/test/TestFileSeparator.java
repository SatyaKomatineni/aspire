/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import java.io.*;

public class TestFileSeparator {

  public TestFileSeparator() {
  }

  public static void main(String[] args) {
    TestFileSeparator testFileSeparator = new TestFileSeparator();
    testFileSeparator.invokedStandalone = true;
    testFileSeparator.test();
  }
  private boolean invokedStandalone = false;
  public void test()
  {
      System.out.println("Testing File object related calls ");
      File testFile = new File("d:/work/ai.log");
      if (testFile.exists())
      {
         System.out.println("d:/work/ai.log exists");
         System.out.println("Filepath separator : " + File.pathSeparator );
         System.out.println("separator : " + File.separator );
         System.out.println("getName(): " + testFile.getName());
         System.out.println("getPath(): " + testFile.getPath());
         System.out.println(testFile);     
      }
      System.out.println("End testing");
  }
} 