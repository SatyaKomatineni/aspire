package com.ai.application.defaultpkg;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

/**
 * @author Satya
 * Provide aliases for classnames
 */
public class AliasedFactory3 extends FilterEnabledFactory2 {

    /**
     * Creates an object with no consideration of synchronization.
     * The calling methods are expected to provide the synchronization.
     * Only called by the getCreator() for now
     */
    protected Object createCreator(String className)
      throws com.ai.application.interfaces.RequestExecutionException
    {
         try
         {
         	String realClassname = AppObjects.getValue("aspire.classalias." + className,className);
            AppObjects.info(this,"Creating the creator :" + realClassname);
            Class classObj = Class.forName(realClassname);
            Object creator = classObj.newInstance();
            return   creator;
         }
         catch(java.lang.ClassNotFoundException x)
         {
            throw new RequestExecutionException("Error: class not found",x);
         }
         catch (java.lang.InstantiationException x)
         {
            throw new RequestExecutionException("Error: class instantiation error",x);
         }
         catch(java.lang.IllegalAccessException x)
         {
            throw new RequestExecutionException("Error: Illeagal access",x);
         }
    }
}