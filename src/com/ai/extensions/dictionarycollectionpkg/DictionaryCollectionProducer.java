package com.ai.extensions.dictionarycollectionpkg;

import com.ai.data.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import com.ai.common.*;

/**
 * Factory task
 * Returns an IDataCollection from a dictionary
 *
 * property file arguments
 * ***********************
 *
 * request.url1.maindataRequest.className=com.ai.extensions.dictionarycollectionpkg.DictionaryCollectionProducer
 * request.url1.maindataRequest.dictionaryproducerRequestname=xmlFile1Request
 *
 * request.xmlFile1Request.className=com.ai.extensions.dictionarycollectionpkg.XmlNodeDictionary
 * request.xmlFile1Request.filename=filename
 * request.xmlFile1Request.xpathParentNodeList=expr1|expr2|expr3
 *
 *
 * request.<requestname>.dictionaryproducerRequestname=x
 *
 * <request name="x" className="implementingx" arg1=""/>
 *
 */
public class DictionaryCollectionProducer extends ADataCollectionProducer {

   public IDataCollection execute(String taskName, Map arguments)
      throws RequestExecutionException
   {
      try
      {
         String dictRequestName =
            AppObjects.getValue(taskName + ".dictionaryProducerRequestName");

         return new DictionaryCollection(
               (IDictionary)AppObjects.getObject(dictRequestName,arguments));
      }
      catch(ConfigException x)
      {
         throw new RequestExecutionException("Error: Dictionary producer not found",x);
      }
   }
}