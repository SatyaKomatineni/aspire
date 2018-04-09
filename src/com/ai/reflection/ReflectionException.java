package com.ai.reflection;

import com.ai.common.CommonException;
public class ReflectionException extends CommonException 
{
  public ReflectionException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public ReflectionException(String msg )
  {
   this(msg,null);
  }
} 