package com.ai.aspire.context;

import com.ai.common.*;
import com.ai.application.defaultpkg.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.util.*;

public class AspireContextRegistry implements ICreator {

   static public String NAME=
      com.ai.servlets.AspireConstants.SESSION_CONTEXT + "AspireContextRegistry";
      
   private Hashtable m_threadVsAspireContexts = new Hashtable();
   
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }
   
   public void add(AspireContext context)
   {
      m_threadVsAspireContexts.put(Thread.currentThread(),context);
   }
   public void remove(AspireContext context)
   {
      m_threadVsAspireContexts.remove(Thread.currentThread());
   }
   public AspireContext get()
   {
      return (AspireContext)m_threadVsAspireContexts.get(Thread.currentThread());
   }
   public int size()
   {
      return this.m_threadVsAspireContexts.size();
   }
   
   
} 
