package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;

import java.io.*;
import java.util.Map;
/**
 * Based on ifPart
 * 
 * Evaluates an if expression and if successful
 * 	returns the "if" substitution result as the outcome
 * if fails
 * 	returns the "else" substitution result as the outcome
 * 
 * Additional property file arguments
 * 1. expression=<if expression>
 * 2. if
 * 3. else
 *
 * Output
 * 1.resultName: Return value.
 * The returned value is often inserted into the args by caller.
 * 
 * Notes
 * *********
 * This uses general arg subst by default
 * Use a derived class if you need a specialized secure
 * substitutor that can validate input.
 * 
 * Example
 * ********************
 * request.NUFH.GetFolderQualifier.classname=com.ai.parts.ConditionalSubstitutionPart
 * request.NUFH.GetFolderQualifier.expression=exists(folderid)
 * request.NUFH.GetFolderQualifier.if=\
 * f.folder_id = {folderid}
 * request.NUFH.GetFolderQualifier.else=\
 * f.folder_name = {folderName.quote}
 * request.NUFH.GetFolderQualifier.resultname=FolderQualifier
 * 
 * 12/4/15
 * *************
 * Try using ConditionalSQLSubstitutionPart
 * @see ConditionalSQLSubstitutionPart
 *
 */

public class ConditionalSubstitutionPart extends AFactoryPart
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
            String ifString = AppObjects.getValue(requestName + ".if",null);
            String elseString = AppObjects.getValue(requestName + ".else",null);

            boolean r
            = ExpressionEvaluationUtils.evaluateBooleanExpressionUsingDictionary(expression,new MapDictionary(inArgs));
            if (r == true)
            {
                if (ifString == null)
                {
                	AppObjects.warn(this,"No if mentioned. Skipping if execution");
                    return new IgnoreResult();
                }
               //Substitute using a suitable substitutor
               String newIfString = substitute(ifString,new MapDictionary(inArgs));
               return newIfString;
            }
            else
            {
               if (elseString == null)
               {
               	  AppObjects.info(this,"No else mentioned. Skipping if execution");
                  return new IgnoreResult();
               }
               //Substitute using a suitable substitutor
               String newElseString = substitute(elseString,new MapDictionary(inArgs));
               return newElseString;
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
    }//eof-function
    
    /**
     * Override this to specialize it
     * @param encodedString
     * @param arguments
     * @return
     */
    protected String substitute(String encodedString, IDictionary arguments)
    {
    	return SubstitutorUtils.generalSubstitute(encodedString,arguments);
    }
}//eof-class

