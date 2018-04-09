/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.htmlgen.test;
import com.ai.htmlgen.*;
import com.ai.application.interfaces.*;
import java.util.Vector;
import com.ai.data.IIterator;
import java.util.Hashtable;

public abstract class TestFormHandler extends CFormHandlerWithControlHandlers
                        implements ICreator
{
        int m_allowedTurns = 10;
        int m_curTurn = 1;
        String m_formName;
   public IIterator getKeys()
   {
      return  null;
   }
/*       public IControlHandler getControlHandler(final String handlerName )
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
   public String getValue(final String key)
   {
        return "Replacement  by Form";
   }
   public String getFormName()
   {
        return m_formName;
   }
   public Hashtable getUrlArguments()
   {
      return null;
   }
   public Object executeRequest(String requestName, Object args )
   {
        m_formName = (String)(((Vector)args).elementAt(0));
        return this;
   }
   public String getValue(final String key, int turn)
   {
        m_curTurn = turn;
        return "Replace by Control " + turn;
   }
   public boolean getContinueFlag()
   {
        if (m_curTurn <= m_allowedTurns)
        {
                return true;
        }
                return false;
   }
}

