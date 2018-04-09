package com.ai.common;

/**
 * Introduced in place of FieldParser
 * Solely used by substitution framework with in aspire
 * Use this instead fo FieldParser where applicable.
 */
class CompoundField
{
   String m_fieldName = null;
   String m_fieldType = null;
   
   CompoundField(String compoundField)
   {
      int index = compoundField.lastIndexOf(".");
      if (index == -1)
      {
         m_fieldName = compoundField;
      }
      else
      {
//         AppObjects.log("SQLArgSubstitutor: " + compoundField + " quote at " + index);
         m_fieldName = compoundField.substring(0,index);
         m_fieldType = compoundField.substring(index + 1);
//         AppObjects.log("SQLArSystem.out.println(m_fieldName + " : " + m_fieldType );
      }
   }                                  
   String getFieldName()
   {
      return m_fieldName;
   }
   String getFieldType()
   {
      return m_fieldType;
   }
}
