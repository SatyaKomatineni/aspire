/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.xml;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import com.ai.htmlgen.*;

public class TransformUtils 
{
   public static Object getTransformObject(String prefix)
   {

      try
      {         
         // See if there is a special transform for this object
         Object pageLevelTransform = AppObjects.getIFactory().getObject(prefix + ".transform", null);
         return pageLevelTransform;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("pd: Page level transformation not available");
         AppObjects.log("pd: Continuing with Application level transformation");
      }         
      try 
      {
         //See if you can locate a transformation object
         Object obj = AppObjects.getIFactory().getObject(IAITransform.GET_TRANSFORM_OBJECT,null);
         return obj;
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("pd: Could not obtain the transform from the config file");
         AppObjects.log("pd: Using the default HtmlParser as the transformation object");
//         AppObjects.log(x);
         return new HtmlParser();
      }         
   }                                
} 
