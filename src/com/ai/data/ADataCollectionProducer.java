package com.ai.data;

import com.ai.application.interfaces.*;
import java.util.*;

/**
 * Extend from this class if you want to implement IDataCollectionProducer
 * Additionally implement ITask if you want to be a singleton
 * Implement ISingleThreaded if you want to be a bean
 * By default you are an ITask
 */

public abstract class ADataCollectionProducer 
      implements IDataCollectionProducer
                 ,ICreator
{

   /**
    * interface from aspire and implemented by the client
    */
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
    {
      if (args instanceof Hashtable)
      {
         return execute(requestName, (Hashtable)args);
      }
      else if (args instanceof Map)
      {
         return execute(requestName, (Map)args);
      }
      else if (args instanceof Vector)
      {
         Vector vArgs = (Vector)args;
         return execute(requestName, (Map)vArgs.elementAt(0));
      }
      else
      {
         throw new RequestExecutionException("Error: Wrong type of arguments passed : " + args.getClass().getName());
      }
    }
} 