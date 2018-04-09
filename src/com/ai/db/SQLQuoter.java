package com.ai.db;

import com.ai.common.ITranslator;
import com.ai.common.SQLArgSubstitutor;

public class SQLQuoter 
{
	private static ITranslator sqlQuoteTranslator = null;
	static
	{
		sqlQuoteTranslator = SQLArgSubstitutor.getQuoteTranslator();
		if (sqlQuoteTranslator == null)
		{
			throw new RuntimeException("Cannot get Quote Translator object");
		}
	}
	/*
	 * Take a string and quote the string
	 * depending on the database being used.
	 */
	public static String quote(String inS)
	{
		assert(sqlQuoteTranslator != null):"Cannot get Quote Translator object";
		return sqlQuoteTranslator.translateString(inS);
	}
}//eof-class
