/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db.sequences;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import com.ai.data.*;
import com.ai.db.*;
import java.util.*;
import com.ai.common.*;

public class AccessSequenceGenerator implements ISequenceGenerator, ICreator
{
   private Hashtable sequenceNameVsValue = new Hashtable();
   
   public AccessSequenceGenerator() 
      throws FieldNameNotFoundException
   {
      loadSequences();
   }
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }      
   public String getNextSequenceFor(final String sequenceName)
      throws SequenceException
   {
      SequenceValue seqValue = (SequenceValue)sequenceNameVsValue.get(sequenceName);
      if (seqValue == null)
      {
         throw new SequenceException("NO such sequence");
      }
      synchronized(seqValue.m_lock)
      {
         seqValue.value++; // increment sequence
         updateSequence(sequenceName, seqValue);
         return Long.toString(seqValue.value);
      }
   }
   
   private void updateSequence(final String seqName, SequenceValue seqValue) throws SequenceException
   {
      // get a new outof band connection
      // update seq_table set seqvalue = 10 where seqName = 1
      // Have the connection auto commit enabled
      try
      {
         IFactory factory = AppObjects.getIFactory();
         Hashtable args=new Hashtable();
         args.put("seqname",seqName);
         args.put("seqvalue",Long.toString(seqValue.value));
         Object reply = factory.getObject("Aspire.update_sequence",args);
      }
      catch(RequestExecutionException x)
      {
         throw new SequenceException("Error: Could not update sequence" + seqName , x);
      }
   }
   private void loadSequences() throws com.ai.data.FieldNameNotFoundException
   {
      try
      {
         IFactory factory = AppObjects.getIFactory();
         IDataCollection col = (IDataCollection)factory.getObject("ASPIRE.SEQUENCE_LOAD",new Hashtable());
         IIterator itr = col.getIIterator();
         for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
         {                              
            IDataRow curRow = (IDataRow)itr.getCurrentElement();
            String seqName = curRow.getValue("seq_name");
            String valueStr = curRow.getValue("seq_value");
            sequenceNameVsValue.put(seqName,new SequenceValue(Long.parseLong(valueStr)));
         }
         col.closeCollection();
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("Error: Could not retrieve sequences");
         AppObjects.log(x);
      }
      catch(com.ai.data.DataException x)
      {
         AppObjects.log("Error: Could not retrieve sequences");
         AppObjects.log(x);
      }
      finally
      {
         AppObjects.info(this,"Loaded sequences are: %1s",sequenceNameVsValue.toString());
      }
   }
/********************************************************
 * Internal scope class  
 ********************************************************
 */
class SequenceValue                                     
{
   long value=0;
   final Integer m_lock = new Integer(0);
   SequenceValue(long inValue) {value=inValue;}
}   
} 