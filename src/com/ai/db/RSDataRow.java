package com.ai.db;
import java.sql.*;
import com.ai.data.*;
import com.ai.application.utils.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RSDataRow implements IDataRow
{

private ResultSet m_rs = null;
private ResultSetMetaData m_rsMetaData = null;
private int m_rownum = 0;

//Passed in so that it is not constructed every time
private SimpleDateFormat m_dateFormatter = null;

//Passed in so that it is not constructed every time
private IMetaData m_metaData = null;

// hashtable
private List valueArray = null;
                                       
   public RSDataRow(ResultSet rs 
                    ,ResultSetMetaData rsMetaData
                    ,IMetaData metaData
                    ,SimpleDateFormat dateFormat
                    , int rownum) 
   {
      m_rs = rs;
      m_rownum = rownum;
      m_rsMetaData = rsMetaData;
      m_metaData = metaData;
      m_dateFormatter = dateFormat;
      valueArray = getValues();
   }

      
   /**
    * Return a string value for the specified index
    */
  public String getValue(int inIndex)
  {
      return (String)valueArray.get(inIndex);
  }

  private String getValueByIndex(int inIndex)
  {
      try 
      {      
         AppObjects.trace(this,"RSDataRow/getValue Request for column index %1s",inIndex);
         int colType = m_rsMetaData.getColumnType(inIndex + 1);
         String value = null;
         if (colType == java.sql.Types.CLOB )
         {
            int len = (int) m_rs.getClob(inIndex + 1).length();
            value = m_rs.getClob(inIndex + 1).getSubString(1, len);
         }
         else if (colType == java.sql.Types.BLOB)
         {
              int len = (int) m_rs.getClob(inIndex + 1).length();
              value = m_rs.getBlob(inIndex + 1).getBytes(1, len).toString();
         }
         else if (colType == java.sql.Types.DATE )
         {
            value = formatDate(m_rs.getDate(inIndex+1));
         }
         else if (colType == java.sql.Types.TIMESTAMP )
         {
            value = formatDate(m_rs.getDate(inIndex+1));
         }
         else
         {
            // it is of any type
            value = m_rs.getString(inIndex + 1);
         }
         
         if (value == null)
         {
            AppObjects.log("Trace:RSDataRow/getValue null value returned");
            return "";
         }
         if (value.equals("null"))
         {
            return "";
         }
         AppObjects.trace(this,"RSDataRow/getValue return value=%1s",value);
         return value;
      }
      catch(SQLException x)
      {
         AppObjects.log("Warn:RSDataRow/getValue Index not found in the result set", x);
         return "";
      }         
  }
   /**
    * Return a string value for the specified column name 
    * If the name not found return the default value
    */
  public String getValue(String columnName, String defaultValue)
  {
      try
      {
         return getValue(m_metaData.getIndex(columnName));
      }
      catch(FieldNameNotFoundException x)
      {
         AppObjects.log("Warn: Column name not recognized by the meta data");
         return defaultValue;
      }
  }
   /**
    * Return a string value for the specified column name 
    */
  public String getValue(String columnName) throws com.ai.data.FieldNameNotFoundException
  {
      AppObjects.info(this,"RSDataRow/getValue request for column %1s",columnName);
      int index = m_metaData.getIndex(columnName);
      if (index == -1)
      {
         throw new FieldNameNotFoundException();
      }
      // valid index
      return getValue(index);
  }
  
   /**
    * Return column names
    */ 
     public IIterator getColumnNamesIterator()
     {
         return m_metaData.getIterator();
     }
     /**
      * formatDate
      */
     private String formatDate(java.util.Date date )
     {
         if (date == null) return "";
         return m_dateFormatter.format(date);
     }

     /**
      * Convert to string
      */
     public String toString()
     {
               StringBuffer row = new StringBuffer();
               for(int i = 0;i<m_metaData.getColumnCount();i++)
               {
                  row.append("|" + getValue(i));
               }
               return row.toString();
     }

     // zero based index
     private List getValues()
     {
         ArrayList al = new ArrayList();
         for(int i = 0;i<m_metaData.getColumnCount();i++)
         {
            al.add(getValueByIndex(i));
         }
         return al;
     }
     /**
      * Methods borrowed from IDataRow1
      */
     
     public Object getValueAsObject(int inIndex)
     throws DataException
     {
         try 
         {      
            AppObjects.info(this,"GetValueasobject Request for column index %1s", inIndex);
            return m_rs.getObject(inIndex+1);
         }
         catch(SQLException x)
         {
        	 throw new DataException("Error:Problem retrieving values from resultset",x);
         }         
     }//eof-function
     /**
      * Return a string value for the specified column name 
      */
    public Object getValueAsObject(String columnName) 
    throws DataException
    {
    	try
    	{
	        AppObjects.info(this,"GetValueByObject request for column %1s",columnName);
	        int index = m_metaData.getIndex(columnName);
	        if (index == -1)
	        {
	           throw new DataException("Error:Requested column name " + columnName + " doesn't exist");
	        }
	        // valid index
	        return getValueAsObject(index);
    	}
    	catch(FieldNameNotFoundException x)
    	{
	           throw new DataException("Error:Requested column name " 
	        		   + columnName 
	        		   + " doesn't exist"
	        		   ,x);
    	}
    }
     
}//eof-class
