package com.ai.data;

import com.ai.application.interfaces.*;
import com.ai.data.*;

import java.util.*;


/**
 * To implement this interface do the following:
 * Extend from ADataCollectionProducer and implement this one method
 *
 * Two options
 * case 1) Implementer wants to be a singleton
 *         Additionally implement ITask
 *
 * case2) Implementer wants to implement bean semantics (multiple copies)
 *       Additionally implement ISingleThreaded
 *
 * You can be either an ITask or ISingleThreaded but not both
 * By default you are treated as an ITask (singleton)
 *
 */
public interface IDataCollectionProducer
{  
   public IDataCollection execute(String taskName, Map arguments) 
      throws RequestExecutionException;
} 