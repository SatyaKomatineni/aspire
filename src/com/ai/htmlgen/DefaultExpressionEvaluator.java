package com.ai.htmlgen;
import com.ai.common.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.io.*;
import java.util.*;

public class DefaultExpressionEvaluator implements IExpressionEvaluator, ICreator
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
    throws IOException
  {
     AppObjects.trace(this,"Processing expression:%1s",expression);
     Vector v = com.ai.common.Tokenizer.tokenize(expression,"(,)");
     String functionName = (String)v.get(0);
     if (functionName.equals("substitute"))
     {
        out.print(substituteForKey((String)v.get(1),args));
     }
     else if (functionName.equals("substituteFile"))
     {
        out.print(substituteRelativeFile((String)v.get(1),args));
     }
     else
     {
      AppObjects.warn(this,"Unrecognized function:%1s", expression);
      return ;
     }
  }

  //substitute(key)
  private String substituteForKey(String key, IDictionary args)
  {
     String value = (String)args.get(key);
     if (value == null)
     {
       AppObjects.warn(this,"Could not find value for key:%1s", key);
       return "";
     }
     return SubstitutorUtils.generalSubstitute(value,args);
  }

  //substitute(relative_filename)
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
  }

}//eof-class