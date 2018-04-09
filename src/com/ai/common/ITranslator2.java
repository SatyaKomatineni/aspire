/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;                  

/**
 * At this point these translators are being used by Substitutors
 * Clients should give preference to this interface
 */
public interface ITranslator2 extends ITranslator1
{
  //Constant to be used when other fields are not passed in
  //
  public static final String UNKNOWN = "unknown"; 
	/**
	 * @param fieldName : Used for reference and debugging
	 * @param fieldValue : This is the value to be translated
	 * @param fieldType : The type of field 
	 * @return translated value
	 */
   public String translateString(final String fieldName,
		   final String fieldValueToBeTranslated, 
		   final String fieldType);
}  