/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;
import java.util.*;

public class TestHashtable {

  public TestHashtable()
  {
  }
 public  static void main(String args[])
  {
      TestHashtable o = new TestHashtable();
      o.test();
  } 
  
  public void test()
  {
      Hashtable table = new Hashtable();
      table.put("one","one");
      table.put("one","two");
      System.out.println(table);
  }
} 
