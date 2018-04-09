package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;
import java.io.*;
import java.util.Map;
/**
 * Takes an Aspire URL name and exports the contents of that URL output to the specified filename
 *
 * Additional property file arguments
 * 1. expression=<name of the aspire url>
 * 2. if
 * 3. else
 *
 * Output
 * 1.resultName: Absolute filename to which the contents are exported
 *
 */

public class IfPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
        throws RequestExecutionException
    {
        PrintWriter w = null;
        String aspireFilename = null;
        try
        {
        //mandatory args
            String expression = AppObjects.getValue(requestName + ".expression");
            String ifRequestName = AppObjects.getValue(requestName + ".if",null);
            String elseRequestName = AppObjects.getValue(requestName + ".else",null);

            boolean r
            = ExpressionEvaluationUtils.evaluateBooleanExpressionUsingDictionary(expression,new MapDictionary(inArgs));
            if (r == true)
            {
                if (ifRequestName == null)
                {
                	AppObjects.info(this,"No if mentioned. Skipping if execution");
                    return new Boolean(true);
                }
               return AppObjects.getObject(ifRequestName,inArgs);
            }
            else
            {
               if (elseRequestName == null)
               {
               	  AppObjects.info(this,"No else mentioned. Skipping if execution");
                  return new Boolean(true);
               }
               return AppObjects.getObject(elseRequestName,inArgs);
            }
        }
        catch(ConfigException x)
        {
            throw new RequestExecutionException("Error: ConfigException. See the embedded exception for details", x);
        }
        catch(CommonException x)
        {
            throw new RequestExecutionException("Error: Expression evaluation error. See the embedded exception for details", x);
        }
    }
}

