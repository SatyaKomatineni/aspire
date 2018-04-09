package com.ai.common;

/**
 * <p>Title: Your Product Name</p>
 * <p>Description: Your description</p>
 * <p>Copyright: Copyright (c) 1999</p>
 * <p>Company: Your Company</p>
 * @author Your Name
 * @version
 */
import java.util.*;
import com.ai.application.utils.*;

public class ExpressionEvaluationUtils
{
	public static String es = "";

   public static boolean evaluateBooleanExpressionUsingDictionary(String expression, IDictionary args)
         throws CommonException
   {
      Vector v = com.ai.common.Tokenizer.tokenize(expression,"(,)");
      String functionName = (String)v.get(0);
      
      //Deal with exists separately
      if (functionName.equalsIgnoreCase("exists"))
      {
      	String argument = (String)v.get(1);
      	String value = (String)args.get(argument);
      	if (value == null)
      		return false;
      	else
      		return true;
      }
      //Function name other than exists
      Vector paramVector = new Vector();
      paramVector.add(functionName.toLowerCase());
      for(int i=1;i<v.size();i++)
      {
         String value = getValue((String)v.elementAt(i),args);
         if (value != null)
         	paramVector.add(value);
         else
         {
         	//value does not exist add an empty string as its value
         	paramVector.add(es);
         }
      }

      //evaluate the function using common be evaluator
      Boolean bResult = (Boolean)AppObjects.getIFactory().getObject("Aspire.BooleanFunction." + functionName
                     ,paramVector);

      return bResult.booleanValue();

   }//eof-function
   private static String getValue(String key, IDictionary args)
   {
      String litValue = StringUtils.getLiteralValue(key);
      if (litValue == null)
      {
         //it is not a literal
         return (String)args.get(key);
      }
      else
      {
         //it is a literal
         return litValue;
      }
   }//eof-function
}//eof-class