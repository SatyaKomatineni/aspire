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
import java.util.Enumeration;

public abstract class AFormHandlerWithRequestControlHandlers extends CFormHandlerWithControlHandlers
{
        public IControlHandler getControlHandler(final String handlerName )
                throws ControlHandlerException
        {
                try 
                {
                        Object obj = m_controlHandlers.get(handlerName);
                        if ( obj == null)
                        {
                                Vector args = new Vector();

                                // Pass the name full handler name
                                String fullHandlerName = getFormName() + "." + handlerName;
                                args.addElement(fullHandlerName );
                                
                                // Pass the object itself
                                args.addElement(this);

                                // Pass the url arguments to be substituted
                                // This would be an optional argument
                                Hashtable urlArgs = getUrlArguments();
                                if (urlArgs != null )
                                {
                                 args.addElement(urlArgs);
                                }
                                AppObjects.trace(this,"Arguments in to the table handler are: %1s",urlArgs );
                                AppObjects.trace(this,"Locating control handler for : %1s.class_request",fullHandlerName );
                                obj = AppObjects.getIFactory().getObject(
                                        fullHandlerName + ".class_request"
                                        ,args  ); 
                                m_controlHandlers.put(handlerName,obj);                        
                                IControlHandler thisHandler = (IControlHandler)obj;
                                String handlerNameArg = "aspire.loops." + handlerName.toLowerCase();
                                if (thisHandler.isDataAvailable())
                                {
                                    m_args.put(handlerNameArg,"true");
                                }
                                else
                                {
                                    m_args.put(handlerNameArg,"false");
                                }
                                AppObjects.info(this,"placing %1s in args", handlerNameArg);
                        }
                        return (IControlHandler)obj;
                }
                catch (RequestExecutionException x)
                {
                        throw new ControlHandlerException(ControlHandlerException.CONTROL_HANDLER_NOT_FOUND,x);
                }                        
        }
} 
