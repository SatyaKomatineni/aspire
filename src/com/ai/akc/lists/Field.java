package com.ai.akc.lists;

import java.util.List;

import com.ai.common.StringUtils;
import com.ai.common.Tokenizer;
import com.ai.common.Utils;
import com.ai.db.SQLQuoter;

public class Field 
{
	public static final int VALIDATION_TYPE_STRING = 1;  
	public static final int VALIDATION_TYPE_NUMBER = 2;
	public static final String DEFAULT_STORAGE_TABLENAME = "t_list_populace";
	
	//Acts as a key for this field
	public String name;
	
	// string or a number
	public int validationType;
	
	//A given field may reside in a different table
	//storage spec in other words
	public String tablename;
	public String columnname;
	
	public boolean deletedFlag;

	/**
	 * Construct from a compositeFieldSpec
	 * No commas allowed,but as delimiters
	 * ex: name,[table.]fieldname,[string]
	 */
	public Field(String compositeFieldSpec)
	{
		List<String> fieldSpecElementList
		= Tokenizer.tokenizeAsList(compositeFieldSpec, ",");
		Utils.massert(this,
				(fieldSpecElementList.size() >= 2)
				,"There must be at least name and its field map");
		name = fieldSpecElementList.get(0);
		String compositeFieldName = fieldSpecElementList.get(1);
		setCompositeField(compositeFieldName);
		if (fieldSpecElementList.size() > 2)
		{
			String lValidationType = fieldSpecElementList.get(2);
			validationType = convertStringToValidationType(lValidationType);
		}
	}
	/*
	 * [tablename.]fieldname
	 */
	private void setCompositeField(String cfield)
	{
		if (cfield.indexOf('.') < 0)
		{
			tablename=Field.DEFAULT_STORAGE_TABLENAME;
			columnname = cfield;
			return;
		}
		//tablename is present
		List<String> f1f2 = Tokenizer.tokenizeAsList(cfield,",");
		tablename=f1f2.get(0);
		columnname = f1f2.get(1);
		return;
	}
	
	private int convertStringToValidationType(String vtype)
	{
		if (StringUtils.isEmpty(vtype))
		{
			return Field.VALIDATION_TYPE_NUMBER;
		}
		return Field.VALIDATION_TYPE_STRING;
	}
	/*
	 * Take a value for this field.
	 * If it is a string quote the string so that it
	 * can be used in an sql statement.
	 */
	public String translate(String value)
	{
		if (validationType != Field.VALIDATION_TYPE_STRING)
		{
			//not a string
			return value;
		}
		//not a number
		return SQLQuoter.quote(value);
	}
}
