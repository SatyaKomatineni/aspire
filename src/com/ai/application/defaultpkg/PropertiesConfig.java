package com.ai.application.defaultpkg;

import com.ai.application.interfaces.AConfig;
import com.ai.application.interfaces.ConfigException;
import java.util.Properties;

public class PropertiesConfig extends AConfig
{
 private Properties m_p = null;
 public PropertiesConfig(Properties p)
 {
   m_p = p;
 }
 public String getValue(String key)
     throws ConfigException
 {
   String value =  m_p.getProperty(key);
   if (value != null) return value;
   throw new ConfigException("Key " + key + " not found.");
 }

 public String getValueFromSource(String source, String key)
     throws ConfigException
 {
   throw new ConfigException("Not implemented","key","msg");
 }
}
