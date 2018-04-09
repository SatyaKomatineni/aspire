/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.common;
import java.io.PrintWriter;

public class CommonException extends Exception {

        int m_errorNumber;
        Throwable m_childException=null;
        
        public CommonException(String     msg
                               ,int       errorNumber )
        {
                super(msg);
                m_errorNumber = errorNumber;
        }
        public CommonException(String msg) 
        {
                this(msg,0);
        }
        final void setErrorNumber( int errorNumber)
        {
             m_errorNumber = errorNumber;
        }
        final int getErrorNumber()
        {
                return m_errorNumber;
        }
        final public Throwable getChildException()
        {
         return m_childException;
        }
        
        final public void setChildException(Throwable t)
        {
            m_childException = t;
        }
/*        public String toString()
        {
         return super.toString()
               + "\n Error number : " + m_errorNumber
               + "\n Child exceptions follow " 
               + "\n " + m_childException;
        }
*/
      public void printStackTrace(PrintWriter out)
      {
         super.printStackTrace(out);
         out.println(super.getMessage());
         if (m_childException != null)
         {
            m_childException.printStackTrace(out);
         }
      }
      public void printStackTrace()
      {
         super.printStackTrace();
         System.out.println(super.getMessage());
         if (m_childException != null)
         {
            m_childException.printStackTrace();
         }
      }
      public String getMessage()
      {
      
         String message = this.getClass().getName() + ":" + super.getMessage();
         if (m_childException != null)
         {
            String childMessage = m_childException.getMessage();
            return childMessage + "\n" + message;
         }
         return message;
      }
      public Throwable getRootException()
      {
         if (m_childException == null)
         {
            return this;
         }
         // child exception is not null
         if (m_childException instanceof CommonException)
         {
            return ((CommonException)m_childException).getRootException();
         }
         // Child not a common exception
         return m_childException;
      }
      
      public String getRootCause()
      {
         Throwable t = getRootException();
         return t.getMessage();
      }
} 