/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.db.sequences;
import com.ai.common.CommonException;

public class SequenceException extends CommonException 
{
  public SequenceException(String msg, Throwable t )
  {
   super(msg);
   setChildException(t);
  }
  public SequenceException(String msg )
  {
   this(msg,null);
  }
} 


