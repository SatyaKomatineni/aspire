package com.ai.common;

//Represents a field which looks like
//fieldname.fieldtype
//fieldtype could be empty in which case fieldtype is null
//fieldname should not be empty but can be in which case it is null
//In the future raise an exception! or at least write an error log entry
public class FieldParser
{
 String m_fieldName = null;
 String m_fieldType = null;
 
 FieldParser(String compoundField)
 {
    int index = compoundField.lastIndexOf(".");
    if (index == -1)
    {
       m_fieldName = compoundField;
    }
    else
    {
//       AppObjects.log("SQLArgSubstitutor: " + compoundField + " quote at " + index);
       m_fieldName = compoundField.substring(0,index);
       m_fieldType = compoundField.substring(index + 1);
//       AppObjects.log("SQLArSystem.out.println(m_fieldName + " : " + m_fieldType );
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
}//eof-class
