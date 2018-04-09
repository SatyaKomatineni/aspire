/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;

public class UnexpectedTypeException extends Exception 
{
   public UnexpectedTypeException(String unexpectedTypeName) 
   {
      super("type <" + unexpectedTypeName + "> Not expected at this time");
   }
   public UnexpectedTypeException(Object obj) 
   {
      super("type <" + obj.getClass().getName() + "> Not expected at this time");
   }
}                         