package com.ai.akc.lists;

import java.util.HashMap;
import java.util.Map;

import com.ai.common.StringUtils;
import com.ai.common.Utils;
import com.ai.db.SQLQuoter;

/*
 * 4/10/2012
 * Goal
 * ****
 * Given a set of attributes and a tablename
 * provide sql code segments to issue a final
 * insert statement.
 * 
 * The column names should be storage column names.
 * This means the caller has already translated the 
 * business variable column name to a fixed
 * storage column name in a specific table.
 * 
 * Also do escapes of all column values if they are strings.
 * 
 * Final expected insert statment
 * *********************************
 * insert into {tablename}
 * ({columnNames},additionalc1,c2)
 * values 
 * ({columnValues),c1-value,c2value.quote);
 */
public class InsertStatement 
{
	public String tablename;
	
	//Map<StorageColumnName, StorageColumnValue>
	private Map<String,String> attributeValues
	= new HashMap<String,String>();
	
	public InsertStatement(String inTablename)
	{
		tablename = inTablename;
	}
	
	/**
	 * let this method de-claw value before it stores it
	 * @key: storage column name
	 * @value: value as passed into the post 
	 * @fieldValidationType: will indicate if it needs to be quoted or not
	 */
	public void addAttribute(String key, String value, int fieldValidationType)
	{
		String newValue;
		if (fieldValidationType == Field.VALIDATION_TYPE_STRING)
		{
			newValue = quote(value);
		}
		else
		{
			newValue = value;
		}
		attributeValues.put(key,newValue);
	}
	/*
	 * Quote the string and return the quoted string
	 */
	private String quote(String inS)
	{
		return SQLQuoter.quote(inS);
	}
	/*
	 * A comma separated list of storage column names
	 * Ex: c1, c2, c3
	 */
	public String getInsertColumnNames()
	{
		validateState();
		boolean firstone = true;
		StringBuffer insertColumnNamesBuffer = new StringBuffer();
		for(String storageColName: attributeValues.keySet())
		{
			if (firstone)
			{
				insertColumnNamesBuffer.append(storageColName);
				firstone = false;
			}
			else
			{
				insertColumnNamesBuffer.append(",");
				insertColumnNamesBuffer.append(storageColName);
			}
		}
		return insertColumnNamesBuffer.toString();
	}
	private void validateState()
	{
		Utils.massert(this
				,(StringUtils.isEmpty(tablename) == false)
				,"The tablename for this insert statement cannot be empty");
		Utils.massert(this
				,(attributeValues.isEmpty() == false)
				,"There should be some columns to create an insert for.");
	}
	
	/*
	 * A comma separated list of strings.
	 * each string is quoted.
	 * each string is separated by a comma.
	 * if the column is an integer it will not be quoted.
	 * 
	 * Column values are expected to be vetted for quotes already.
	 * 
	 * Here is an example
	 * ******************
	 * Ex: c1-int-value, 'c2-string-value', 'c3-string-value"'quoted stuff"'somemore'
	 * 
	 */
	public String getInsertColumnValues()
	{
		validateState();
		boolean firstone = true;
		StringBuffer insertColumnValuesBuffer = new StringBuffer();
		for(String storageColName: attributeValues.keySet())
		{
			//Get the value for the key
			//These should be in the same order as the column names
			String columnValue = attributeValues.get(storageColName);
			if (firstone)
			{
				insertColumnValuesBuffer.append(columnValue);
				firstone = false;
			}
			else
			{
				insertColumnValuesBuffer.append(",");
				insertColumnValuesBuffer.append(columnValue);
			}
		}
		return insertColumnValuesBuffer.toString();
	}
}
