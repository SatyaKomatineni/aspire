package com.ai.htmlgen;
import com.ai.application.interfaces.*;
import com.ai.data.*;

import java.util.*;


/**
 * To implement this interface do the following:
 * Extend from ADataCollectionTask and implement this one method
 * This task is most likely a singleton. 
 * The singleton semantics are implemented by the ADataCollectionTask
 * @see ITask
 */
public interface IDataCollectionTask extends ITask
{  
   public IDataCollection execute(String taskName, Map arguments) 
      throws RequestExecutionException;
} 
