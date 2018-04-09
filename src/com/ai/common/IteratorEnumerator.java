package com.ai.common;
import java.util.*;

public class IteratorEnumerator implements Enumeration {

   private Iterator m_itr;
   
   public IteratorEnumerator(Iterator itr) 
   {
      m_itr = itr;
   }
   public boolean hasMoreElements()
   {
      return m_itr.hasNext();
   }
   public Object nextElement() throws NoSuchElementException
   {
      return m_itr.next();
   }
} 