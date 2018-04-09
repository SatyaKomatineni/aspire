package com.ai.common;
import java.util.*;
import com.ai.application.utils.*;

public class GeneralArgSubstitutor extends AdaptableArgSubstitutor
{
   public GeneralArgSubstitutor()
   {
   }
   /**
    * Given a compound key return a value
    */
   protected String translate( String compoundKey, IDictionary keyValMap)
   {

         if (compoundKey ==  null) return "";
               // if curToken
               CompoundField fieldParser = new CompoundField(compoundKey);
               if (fieldParser.getFieldType() == null)
               {
                  // This is not a compound key
                  String value = (String)keyValMap.get(fieldParser.getFieldName());
                  return defaultTranslation(value);
               }
               else
               {
               // compound key exists
                  String value = (String)keyValMap.get(fieldParser.getFieldName());
                  String fieldType = fieldParser.getFieldType();

                  ITranslator translator = AdaptableArgSubstitutor.getTranslator(fieldType);
                  if (translator != null)
                  {
                     if (translator instanceof ITranslator1)
                     {
                        return ((ITranslator1)translator).translateString(value,fieldType);
                     }
                     else
                     {
                        return translator.translateString(value);
                     }
                  }
                  else
                  {
                     // I have a null translaotr
                     AppObjects.log("Error: No translator found for " + fieldType);
                     return "";
                  }
               }

   }
   /* Override this method */
   protected String defaultTranslation(String value)
   {
      if (value == null) return "";
      return value;
   }
}
