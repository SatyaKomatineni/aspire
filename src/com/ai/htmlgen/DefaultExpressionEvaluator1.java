package com.ai.htmlgen;
import com.ai.common.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class DefaultExpressionEvaluator1 implements IExpressionEvaluator, ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }

  public String evaluate(String expression, IDictionary args)
  {
     Vector v = com.ai.common.Tokenizer.tokenize(expression,"(,)");
     String functionName = (String)v.get(0);
     if (functionName.equals("substitute"))
     {
        return substituteForKey((String)v.get(1),args);
     }
     else if (functionName.equals("substituteFile"))
     {
        return substituteRelativeFile((String)v.get(1),args);
     }
     else
     {
      return "";
     }
  }

  public void evaluate(String expression, IDictionary args, PrintWriter out)
    throws IOException, TransformException
  {
     AppObjects.trace(this,"Processing expression:%1s",expression);
     Vector v = com.ai.common.Tokenizer.tokenize(expression.toLowerCase(),"(,)");
     String functionName = (String)v.get(0);
     if (functionName.equals("substitute"))
     {
        out.print(substituteForKey((String)v.get(1),args));
     }
     else if (functionName.equals("substitutefile"))
     {
        out.print(substituteRelativeFile((String)v.get(1),args));
     }
     else if (functionName.equals("substitutekey"))
     {
        out.print(substituteUsingAKey((String)v.get(1),args));
     }
     else if (functionName.equals("substitutekeyfile"))
     {
        out.print(substituteUsingAKeyFile((String)v.get(1),args));
     }
     else if (functionName.equals("escapedoublequotes"))
     {
        out.print(escapeDoubleQuotes((String)v.get(1),args));
     }
     else if (functionName.equals("urlencode"))
     {
        out.print(urlEncode((String)v.get(1),args));
     }
     else if (functionName.equals("htmlencode"))
     {
        out.print(htmlEncode((String)v.get(1),args));
     }
     else if (functionName.equals("getnextcontrolstring"))
     {
        out.print(this.getNextControlString((String)v.get(1),args));
     }
     else
     {
		AppObjects.info(this,"Function %1s not found. Using a delegate instead", functionName);
		this.delegateFunction(functionName,expression,args,out);
     }
  }

  //*******************************************************************
  //substituteUsingAKeyFile
  //The key is searched in the input arguments first
  //and then in the config file with the same name
  //*******************************************************************
  private String substituteUsingAKeyFile(String key, IDictionary args)
	throws TransformException
  {
     String filename = (String)args.get(key);
     if (filename == null)
     {
       throw new TransformException("Error:Could not find filename for key:" + key);
     }
     return substituteRelativeFile(filename,args);
  }


  //*******************************************************************
  //substituteUsingAKey
  //*******************************************************************
  private String substituteUsingAKey(String key, IDictionary args)
  {
     String realKey = (String)args.get(key);
     if (realKey == null)
     {
       AppObjects.error(this,"Could not find key for key:%1s",key);
       return "";
     }
      return substituteForKey(realKey,args);
  }

  //*******************************************************************
  //substitute(key)
  //*******************************************************************
  private String substituteForKey(String key, IDictionary args)
  {
     String value = (String)args.get(key);
     if (value == null)
     {
       AppObjects.error(this,"Could not find value for key:%1s",key);
       return "";
     }
     return SubstitutorUtils.generalSubstitute(value,args);
  }

  //*******************************************************************
  //substitute(relative_filename)
  //*******************************************************************
  private String substituteRelativeFile(String filename, IDictionary args)
  {
    try
    {
      String s = FileUtils.readFile(FileUtils.translateFileName(filename));
       return SubstitutorUtils.generalSubstitute(s,args);
    }
    catch(java.io.IOException x)
    {
      AppObjects.log("Error: Could not read file for substitution",x);
      return "";
    }
  }//eof-function

  //*******************************************************************
  //escapeDoubleQuotes(String key, IDictionary args)
  //*******************************************************************
  private String escapeDoubleQuotes(String key, IDictionary args)
  {
      String value = (String)args.get(key);
      if (value == null)
      {
        AppObjects.error(this,"Could not find value for key:%1s", key);
        return "";
      }
      return StringUtils.encode(value,'\\','"','"');
  }//eof-function
  
  //*******************************************************************
  //urlEncode(String key, IDictionary args)
  //*******************************************************************
  private String urlEncode(String key, IDictionary args)
  {
      try
      {
	      String value = (String)args.get(key);
	      if (value == null)
	      {
	        AppObjects.error(this,"Could not find value for key:%1s", key);
	        return "";
	      }
	      return URLEncoder.encode(value,"UTF-8");
      }
      catch(UnsupportedEncodingException x)
      {
          AppObjects.error(this,"Trying to url encode a string. UTF 8 conversion is not supported.");
          return "";
      }
  }//eof-function
  
  //*******************************************************************
  //htmlEncode(String key, IDictionary args)
  //*******************************************************************
  private String htmlEncode(String key, IDictionary args)
  {
      String value = (String)args.get(key);
      if (value == null)
      {
        AppObjects.error(this,"Error:Could not find value for key to htmlencode:" + key);
        return "";
      }
      return StringUtils.htmlEncode(value);
  }//eof-function
  
  //*******************************************************************
  //getNextControlString(String loopName, IDictionary args)
  //*******************************************************************
  public String getNextControlString(String loopName, IDictionary args)
  {
	String controlString = (String)args.get(loopName + "_controlstring");
	Vector v = Tokenizer.tokenize(controlString,",");
	String begin = (String)v.get(0);
	String span = (String)v.get(1);
	
	int iBegin = Integer.parseInt(begin);
	int iSpan = Integer.parseInt(span);
	
	int newBegin = iBegin + iSpan;
	String newControlString =  "" + newBegin + "," + iSpan;
	return loopName + "_controlstring=" + newControlString;
  }
  
  //*******************************************************************
  //Call an external functor
  //*******************************************************************
  private void delegateFunction(final String functionName
    ,final String caseSensitiveExpression
    ,IDictionary args
    ,PrintWriter out) throws TransformException, IOException
  {
   try
   {
    IExpressionEvaluator delegate =
    (IExpressionEvaluator)
    AppObjects.getIFactory().getObject("Aspire.ExpressionEvaluatorDelegate." + functionName,null);
    delegate.evaluate(caseSensitiveExpression,args,out);
   }
   catch(RequestExecutionException x)
   {
      throw new TransformException("Error:Invoking expression evaluator",x);
   }
  }//eof-function


}//eof-class