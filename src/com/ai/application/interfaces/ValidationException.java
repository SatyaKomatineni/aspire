package com.ai.application.interfaces;

import com.ai.common.CommonException;

public class ValidationException extends CommonException 
{
  public ValidationException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public ValidationException(String msg )
  {
   this(msg,null);
  }
} 