/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.test;
import com.ai.common.*;
import java.util.*;

public class TestArgSubstitutor
{

  public TestArgSubstitutor()
  {
  }
  public void runInterpreter()
  {
      AArgSubstitutor argSubstitutor = new CArgSubstitutor();
      Vector v = new Vector();
      v.addElement("this");
      v.addElement("super_string");

      System.out.println("Testing the vector substitution");
      System.out.println(argSubstitutor.substitute("How about {1} string {2} here;",v));

      System.out.println("Testing the hashtable substitution" );
      
      Hashtable hashtable = new Hashtable();
      hashtable.put("1","427");
      hashtable.put("2","556");
      System.out.println(
         argSubstitutor.substitute("How about {1} string {2} here;"
                                 ,hashtable));

      System.out.println("Testing SQLArgSubstitutor for no quote replacements");
      AArgSubstitutor sqlSubstitutor = new SQLArgSubstitutor();
      System.out.println(
         sqlSubstitutor.substitute("How about {1} string {2} here;"
                                 ,hashtable));
      
      System.out.println("Testing SQLArgSubstitutor for no quote replacements");
      System.out.println(
         sqlSubstitutor.substitute("How about {1} string {2.quote} here;"
                                 ,hashtable));

      System.out.println("");
      System.out.println("\nTesting SQLArgSubstitutor for no quote replacements");
      System.out.println(
         sqlSubstitutor.substitute("How about {1} string ({2.quote},{1.quote}) here;"
                                 ,hashtable));
                                       
  }
  public static void main(String[] args)
  {
    TestArgSubstitutor testArgSubstitutor = new TestArgSubstitutor();
    testArgSubstitutor.runInterpreter();
    testArgSubstitutor.invokedStandalone = true;
  }
  
  // local variables
  private boolean invokedStandalone = false;
} 