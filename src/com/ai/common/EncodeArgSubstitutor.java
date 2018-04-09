package com.ai.common;
import java.util.*;

/**
 * By default it encodes the url values
 * If you don't want encoding use as follows
 *
 * ...{key.empty}..
 *
 * The above construct will substitute the value as it is
 *
 * See GeneralArgSubstitutor for more documentation
 */
public class EncodeArgSubstitutor extends GeneralArgSubstitutor 
{
   protected String defaultTranslation(String value)
   {
      if (value == null) return "";
      return java.net.URLEncoder.encode(value);
   }
   public static void main(String args[])
   {
      Hashtable ht = new Hashtable();
      ht.put("test1","xyz");
      String ins = "{test1.empty}&a=b";
      EncodeArgSubstitutor sub = new EncodeArgSubstitutor();
      String outs = sub.substitute(ins,ht);
   }
} 
