package com.ai.db;
import com.ai.data.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import com.ai.application.utils.*;

/**
 * Used to iterate through an Resultset collection
 *
 * Typically used by DBRSCollectionss
 *
 * Returns IDataRow objects
 */
public class RSDataRowIterator implements IDataRowIterator 
{

   private ResultSet m_rs = null;
   private ResultSetMetaData m_rsMetaData = null;
   private IMetaData m_metaData = null;
   private int m_rownum = 0;
   
   // Has it been moved
   private boolean bMovedToFirst = false;

   // Are there any more entries
   private boolean m_bAtTheEnd = false;

   // Parent collection reference 
   private IDataCollection m_parentCol = null;

   // Date formatter for converting dates
   private SimpleDateFormat m_dateFormat = new SimpleDateFormat("d-MMM-yy");
   
   public RSDataRowIterator(ResultSet rs, IMetaData metaData, IDataCollection col) throws DataException
   {
      try
      {
         m_parentCol = col;
         m_rs = rs;
         m_rsMetaData = m_rs.getMetaData();
         m_metaData = metaData;
         m_rownum = 0;
      }
      catch(SQLException x)
      {
         throw new DataException("SQLException while constructing RSDataRowIterator",x);
      }
   }
   /**
    * Point the result set to the begining of the set
    */
   public void moveToFirst()
            throws DataException
   {

         // If already moved to first return
         if (bMovedToFirst)  return;        

         // Going to move to first now, let me remember it                
         bMovedToFirst = true;
         try   
         {  // Try moving and if succeeds return
            if( m_rs.next() == true) return;

            // if fails mark it the end
            m_bAtTheEnd = true;
         }
         catch(java.sql.SQLException x)
         {
            throw new com.ai.data.DataException("Error: Can not move to first",x);
         }
   }
                     
   /**
    * Move to next row
    */
   public void moveToNext()
            throws DataException
   {
      AppObjects.trace(this,"RSDataRowIterator/gotoNextRow Go to next row called");
   
      if (m_bAtTheEnd == true)
      {
         //Already at the end, just return
         AppObjects.warn(this,"Warn: moveToNext received when the collection is at the end");
         return;
      }
      try 
      { 
         // move to next and if successfull return
         m_rownum++;
         if( m_rs.next() == true) return;

         /// false
         m_bAtTheEnd = true;
         return;
      }
      catch(java.sql.SQLException x)
      {
          throw new com.ai.data.DataException("Error: Can not move to next",x);
      }
   }                  
        /**
         * Is the iterator at the end
         */
        public boolean isAtTheEnd()
                  throws DataException
        {    
            // The idea is to enable the iterator to reply to this 
            // when the iterator is obtained.
            if (bMovedToFirst)
            {
               // If the collection is already moved to first
               return m_bAtTheEnd;
            }              
            // Collection has not been attempted to be moved to first
            moveToFirst();
            return m_bAtTheEnd;
        }               
        
        public IDataRow getCurrentDataRow()
                  throws DataException
      {
         if (m_bAtTheEnd) 
            throw new DataException ("Error: No more rows");

         // not at the end
            return new RSDataRow(m_rs,m_rsMetaData,m_metaData,m_dateFormat, m_rownum);            
      }                  
        /**
         * getCurrentElement
         */
        public Object getCurrentElement()
                  throws DataException
        {   
            return getCurrentDataRow();
        }
      
                  
        /**
         * formatDate
         */
        private String formatDate(java.util.Date date )
        {
            if (date == null) return "none";
            return m_dateFormat.format(date);
        }
}  