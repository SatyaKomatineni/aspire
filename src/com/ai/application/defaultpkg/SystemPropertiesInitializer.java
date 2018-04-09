package com.ai.application.defaultpkg;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.util.*;
/**
 * Manifests
 * Aspire.SystemProperties=prop1,prop2,prop3
 *
 * Aspire.SystemProperties.prop1.key=
 * Aspire.SystemProperties.prop1.value=
 *
 *
 */
public class SystemPropertiesInitializer implements IApplicationInitializer, ICreator
{
   public static String PROPERTIES_STRING="Aspire.SystemProperties";
   
   public boolean initialize(IConfig cfg, ILog log, IFactory factory)
   {
      String properties = cfg.getValue(this.PROPERTIES_STRING,null);
      if (properties == null)
      {
         AppObjects.info(this,"No system properties identified by %1s",this.PROPERTIES_STRING);
         return true;
      }
      // properties available
      Vector v = com.ai.common.Tokenizer.tokenize(properties,",");
      for(Enumeration e=v.elements();e.hasMoreElements();)
      {
         String property = (String)e.nextElement();
         String key=cfg.getValue(this.PROPERTIES_STRING + "." + property + ".key","");
         String value=cfg.getValue(this.PROPERTIES_STRING + "." + property + ".value","");
         if (key.equals(""))
         {
            AppObjects.warn(this,"Key for Specified System property not found: %1s",property);
         }
         else
         {
            // key found, look for value
            if (value.equals(""))
            {
               AppObjects.warn(this,"Value for key %1s is empty",key);
            }
            else
            {
               // value found as well
               // set key and value in system
               AppObjects.info(this,"Placing %1s in System Properties as %2s", key,value);
               System.setProperty(key,value);
            }
         }//else
      }// for each property
      return true;
   }
    public Object executeRequest(String requestName, Object args) 
    {
      return this;
    }
} 
