/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.common;

public class TransformException extends CommonException
{
  public TransformException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public TransformException(String msg )
  {
   this(msg,null);
  }
}             