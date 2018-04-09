/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.application.defaultpkg;

public final class BaseSupport {
    
    BaseSupport() {
    }
    public static void log( String msgType,  String msg) 
    {
      System.out.println(msgType + ":-->" + msg );
    }
    
    public static void log(String msg) 
    {
      System.out.println(msg);
    }
    
    public static void log( java.lang.Throwable exception) 
    {
      System.out.println(exception.getMessage());
      exception.printStackTrace();
    }
}
