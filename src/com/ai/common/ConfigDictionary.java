package com.ai.common;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

public class ConfigDictionary extends DDictionary
{
   public static ConfigDictionary self = new ConfigDictionary(AppObjects.getIConfig());
   private IConfig m_config;
   public ConfigDictionary(IConfig cfg)
   {
      m_config =cfg;
   }
   public Object internalGet(Object key)
   {
      if (key instanceof String)
         return m_config.getValue((String)key,null);
      else
         return null;
   }
}
