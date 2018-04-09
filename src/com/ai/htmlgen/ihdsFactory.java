package com.ai.htmlgen;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;

public abstract class ihdsFactory implements ICreator
{
    //Typed method to create formhandler
    public static IFormHandler getFormHandler(String requestName
                ,Hashtable arguments)
            throws RequestExecutionException
    {
        return (IFormHandler)AppObjects.getObject(requestName,arguments);
    }

    //Typed method to create ihds
    public static ihds getIhds(String requestName
                ,Hashtable arguments)
            throws RequestExecutionException
    {
        return (ihds)AppObjects.getObject(requestName,arguments);
    }

    //Implemented by derived classes
    protected abstract IFormHandler executeRequest(
            String requestName,
            String formName,
            Hashtable args)
            throws RequestExecutionException;

    // from ICreator
    public Object executeRequest(String requestName, Object inArgs)
            throws RequestExecutionException
    {
        try
        {
            Hashtable urlArguments = null;
            String formName = null;

            if (inArgs instanceof Vector)
            {
                 Vector args = (Vector)inArgs;
                 formName = (String)(args.elementAt(0));

                 if (args.size() > 1)
                 {
                    urlArguments = (Hashtable)(args.elementAt(1));
                 }
            }
            else if (inArgs instanceof Hashtable)
            {
                urlArguments = (Hashtable)inArgs;
                Vector requestNameParts = com.ai.common.Tokenizer.tokenize(requestName,".");
                formName = (String)requestNameParts.get(1);
            }
            else
            {
                throw new com.ai.common.UnexpectedTypeException(inArgs);
            }

             return executeRequest(requestName,formName,urlArguments );
        }
        catch(UnexpectedTypeException x)
        {
           throw new RequestExecutionException("Error: Unexpected type", x);
      }
    }//eofm
} // eofc
