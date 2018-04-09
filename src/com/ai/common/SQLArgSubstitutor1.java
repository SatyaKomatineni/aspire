/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.Hashtable;

import com.ai.application.interfaces.IInitializable;
import com.ai.application.utils.AppObjects;
import com.ai.db.DBBaseJavaProcedure;
import com.ai.db.DBException;
import com.ai.db.SQLInjectionException;
import com.ai.servlets.AspireConstants;

/**
 * 11/15/15
 * *******************
 * it is here to check the validity of non string substitutions
 * The field values used for a column which is not of type string are good.
 * Avoids sql injection in non-string fields
 * 
 * 11/16/15
 * ********************
 * Based on SQLArgSubstitutor
 * See various sql server strings allowed in translations
 * 
 * Related Classes
 * ***********************
 * @see SQLArgSubstitutor
 * @see DBBaseJavaProcedure
 *
 * How to specify this class in the config file
 * ***********************************************
 * request.aspire.substitutions.sqlArgSubstitutor.className=\
 * com.ai.common.SQLArgSubstitutor
 * 
 * Originally
 * ***************
 * @version                    1.37, 26 Jun 1996
 */
public class SQLArgSubstitutor1 extends SQLArgSubstitutor implements IInitializable 
{
   public static void main(String[] args)
   {
      com.ai.application.defaultpkg.ApplicationHolder.initApplication(args[0],args);
      SQLArgSubstitutor s = new SQLArgSubstitutor1();
      Hashtable t = new Hashtable();
      t.put("foldername","Humanities current");      
      System.out.println(s.substitute("f.folder_name={folderName.quote}",t));
   }                                   
	
	private int nonStringFieldMaxLength = 32;
	
	@Override
	public void initialize(String requestName) {
		AppObjects.info(this, "Initializing");
		String paramName = requestName + AspireConstants.SUB_SQLARG_SUBSTITUTOR_MAX_INT_LENGTH;
		String sNonStringFieldMaxLength = 
				AppObjects.getValue(paramName, "32");
		
		try	{
			nonStringFieldMaxLength = Integer.parseInt(sNonStringFieldMaxLength);
		}
		catch(NumberFormatException x)
		{
			AppObjects.error(this, "Not a number for config param:%s. Returning default value of %d"
					, paramName
					, nonStringFieldMaxLength);
			
			//Rethrow it, to keep the integrity of the specification
			throw x;
		}
	}
	
   /**
    * Just override the behavior of non-string fields
    * 
    * Background
    * ************
    * some-statement {some-non-string-field-key}
    * No qualifier
    * 
    * Rules
    * *************
    * Possible values
    * 
    * 1. not_found: No value for key found in arguments
    * 2. empty: The value for key found is empty or white characters
    * 3. null: The value for key found is string "null"
    * 4. too long: The value for key found is too long
    * 5. not a number: must satisfy 123, 2.356 etc.
    * 
    * not_found case
    * *******************
    * if there is a key but the value is not there in the arguments
    * not sure what can be substituted. 
    * It is then considered a required field for which there is no value.
    * If the statement designer wants to put a null let that be done
    * through an explicit qualifier.
    * So all fields without a qualifier are then considered required.
    * If a value is not there throw exception
    * 
    * empty value case
    * *******************
    * Again can a required non-string field be empty?
    * I would say no.
    * throw exception
    * 
    * null value
    * ******************
    * you want to pass a null for an int field
    * should I allow it?
    * This means an injector could pass a null field
    * behavior is less predictable
    * If null needed let it come through an explicit qualifier
    * throw an exception
    * 
    * too long
    * **********************
    * This is a number field
    * shouldn't allow too long a string
    * 32 is a good number
    * throw an exception otherwise
    * 
    * not a number
    * **********************
    * Convert it to a long, float or double
    * if not one of those return exception
    *  
    */
   @Override
   protected void validateNonStringField(String nonStringFieldValue)
   throws DBException
   {
	   
	   if (StringUtils.isEmpty(nonStringFieldValue))
	   {
		   //it is an empty value
		   AppObjects.error(this, "Invalid SQL argument. Empty value");
		   throw new SQLInjectionException("Invalid SQL argument due to empty value passed in");
	   }
	   
	   //Non empty field. See if it is null
	   if (nonStringFieldValue.toLowerCase().equals("null"))
	   {
		   //it is null case
		   AppObjects.error(this, "Invalid SQL argument. String value of null passed: %s", nonStringFieldValue);
		   throw new SQLInjectionException("Invalid SQL argument due to null value passed in for non string field");
	   }
	   // it is not empty
	   // it is not null
	   // Check the length
	   if (nonStringFieldValue.length() > nonStringFieldMaxLength)
	   {
		   AppObjects.error(this, "Invalid SQL argument. Integer length exceeded (%s/%s). String is %s"
				   ,nonStringFieldValue.length()
				   ,nonStringFieldMaxLength
				   ,nonStringFieldValue );
		   throw new SQLInjectionException("Invalid SQL argument due to length. Input field:" + nonStringFieldValue);
	   }
	   // it is not empty
	   // it is not null
	   // Length is valid
	   // check to see if it is a number
	   if (StringUtils.isNotANumber(nonStringFieldValue))
	   {
		   //This is not a number
		   //This is not a valid argument as a result
		   AppObjects.error(this, "Invalid SQL argument. Not a number %s"
				   ,nonStringFieldValue );
		   throw new SQLInjectionException("Invalid SQL argument due to number format error. Input field:" + nonStringFieldValue);
	   }
	   // it is not empty
	   // it is not null
	   // Length is valid
	   // it is a number
	   // All good
	   return;
   }
   //Validate typed values
   protected void validateTypedStringField(String fieldType, String fieldValue)
   throws DBException
   {
	   if (StringUtils.isEmpty(fieldValue))
	   {
		   //Translated value is null. This should be a warn or error
		   AppObjects.warn(this, "Field value is empty. Field type is %s", fieldType);
	   }
   }
}//eof-class