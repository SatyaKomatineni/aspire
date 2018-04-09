/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.htmlgen;
import java.util.Hashtable;
import java.util.Vector;
import com.ai.application.utils.AppObjects;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.interfaces.ISingleThreaded;
import com.ai.common.IUpdatableMap;

import java.util.Enumeration;

public abstract class CFormHandlerWithControlHandlers extends ihdsFactory implements IFormHandler
                                          , ISingleThreaded
                                          ,IFormHandler1
										  ,IUpdatableMap
{

        Hashtable m_controlHandlers = new Hashtable();
        String m_formName = null;
        Hashtable m_args = null;

    	public void addKey(Object key, Object value)
    	{
    		m_args.put(key,value);
    	}
    	public void removeKey(Object key)
    	{
    		m_args.remove(key);
    	}
        
        protected IFormHandler executeRequest(
                String requestName,
                String formName,
                Hashtable args)
                 throws RequestExecutionException
        {
            try
            {
             init(formName,args);
             return this;
            }
            catch(ControlHandlerException x)
            {
                throw new RequestExecutionException("Error: ControlHandlerException",x);
            }
       }
        public void addControlHandler(final String handlerName, IControlHandler handler)
        {
            m_controlHandlers.put(handlerName,handler);
        }

   public Enumeration getControlHandlerNames()
   {
      return m_controlHandlers.keys();
   }

/*        public IControlHandler getControlHandler(final String handlerName )
                throws ControlHandlerException
        {
                Object obj = m_controlHandlers.get(handlerName);
                if (obj == null)
                {
                    throw new ControlHandlerException(ControlHandlerException.CONTROL_HANDLER_NOT_FOUND);
                }
                return (IControlHandler)obj;
        }
*/
        public String getFormName()
        {
         return m_formName;
        }
        public Hashtable getUrlArguments()
        {
         return m_args;
        }
       public void init( String formName, Hashtable urlArguments)
         throws ControlHandlerException
       {
         m_formName = formName;
         m_args = urlArguments;
         //This functionality is moved to the derived class
         //Otherwise this is resulting in a bug where the loop handlers are
         //being called before the main data request
         //preLoadControlHandlers();
       }
        public void formProcessingComplete()
        {
            // close all the loop handlers
            AppObjects.log("Closing collections on the loop handlers");
            for(Enumeration e=m_controlHandlers.elements();e.hasMoreElements();)
            {
               AppObjects.log("Closing the control handler");
               IControlHandler controlHandler = (IControlHandler)e.nextElement();
               controlHandler.formProcessingComplete();
            }
        }
   public boolean isDataAvailable()
   {
        for(Enumeration e=m_controlHandlers.elements();e.hasMoreElements();)
        {
           AppObjects.log("Closing the control handler");
           IControlHandler controlHandler = (IControlHandler)e.nextElement();
           if (controlHandler.isDataAvailable())
           {
              return true;
           }
        }
        return false;
    }
    public void preLoadControlHandlers()
    {
      String formName = getFormName();
      Hashtable args = getUrlArguments();
      String handlers = AppObjects.getIConfig().getValue(getFormName() + ".loopNames",null);
      if (handlers == null)
      {
          //lets try another place for loop names
          handlers = AppObjects.getIConfig().getValue("request." + getFormName() + ".loopNames",null);
      }
      AppObjects.trace(this,"Looking for loop names for (loopNames) :%1s",getFormName() );
      if (handlers == null)
      {
         return;
      }
      //otherwise
      Vector v = com.ai.common.Tokenizer.tokenize(handlers,",");
      for(Enumeration e=v.elements();e.hasMoreElements();)
      {
         String controlHandlerName = (String)e.nextElement();
         try
         {
            getControlHandler(controlHandlerName);
         }
         catch(ControlHandlerException x)
         {
            AppObjects.error(this,"Could not load control handler for :%1s", controlHandlerName);
            AppObjects.log(x);
         }
      }

    }
}








