/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db.sequences;

public interface ISequenceGenerator 
{
   public static final String NAME="Aspire.SequenceGenerator";
   public String getNextSequenceFor(final String sequenceName)
      throws SequenceException;
} 
