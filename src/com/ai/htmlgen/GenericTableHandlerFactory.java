package com.ai.htmlgen;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.AppObjects;

public abstract class GenericTableHandlerFactory implements ICreator
{
    public static IControlHandler getControlHandler(String requestName
                ,String handlerName
                ,ihds parentHandler
                ,Hashtable arguments)
            throws RequestExecutionException
    {
        ArrayList totalArgs = new ArrayList();
        totalArgs.add(handlerName);
        totalArgs.add(parentHandler);
        totalArgs.add(arguments);
        return (IControlHandler)AppObjects.getObject(requestName + ".class_request",totalArgs);
    }
    protected abstract IControlHandler executeRequest(
            String requestName,
            List    inTotalArgs,
            String handlerName,
            ihds parentFormHandler,
            Hashtable args)
            throws RequestExecutionException;

    // from ICreator
    public Object executeRequest(String requestName, Object inArgs)
            throws RequestExecutionException
    {
            // I am going to have two arguments
            // form name , type string
            // parent form pointer
            //[optional argument strings]

            if (!(inArgs instanceof List))
            {
                throw new RequestExecutionException("Error: Wrong arguments for GenericTableHandler. List expected");
            }

            List args = (List)inArgs;
            String handlerName = (String)(args.get(0));
            ihds parentFormHandler = (ihds)(args.get(1));

            Hashtable urlArguments = null;
            if (args.size() > 2)
            {
              urlArguments = (Hashtable)args.get(2);
            }

            return executeRequest(requestName,(List)inArgs,handlerName,parentFormHandler,urlArguments);
    }//eofm
} // eofc