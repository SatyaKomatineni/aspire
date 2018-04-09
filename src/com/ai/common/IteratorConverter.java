package com.ai.common;
import java.util.*;
import com.ai.data.*;

public class IteratorConverter implements IIterator 
{
   Iterator m_itr;
   Object m_curObject = null;
   boolean bEnd = false;
   
   public IteratorConverter(Iterator itr)
   {
      m_itr = itr;
   }
   public void moveToFirst()
            throws DataException
   {
      m_curObject = internalNext();
   }            
   public void moveToNext()
            throws DataException
   {
      m_curObject = internalNext();
   }            
   public boolean isAtTheEnd()
            throws DataException
   {
      return bEnd;
   }            
   public Object getCurrentElement()
            throws DataException
   {
      return m_curObject;
   }            
   private Object internalNext()
   {
      if (m_itr.hasNext())
      {
         // i have a next object
         return m_itr.next();
      }   
      else
      {
         // no i don't have any more objects
         bEnd = true;
         return null;
      }
   }
   
} 
