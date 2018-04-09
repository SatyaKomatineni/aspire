/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.test;

public class TestSystemProperties
{

  public TestSystemProperties()
  {
  }
  public void test()
  {
      System.out.println("Comensurating test for system properties");
      System.out.println("property for eas_admin_file :" + System.getProperty("eas_admin_file"));
  }
  
} 