/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;                  

/**
 * At this point these translators are being used by Substitutors
 */
public interface ITranslator1 extends ITranslator
{
// from base class
//  public String translateString(final String inString);
   public String translateString(final String inString, final String translateKey);
}  