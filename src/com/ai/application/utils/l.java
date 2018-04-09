package com.ai.application.utils;
import com.ai.application.interfaces.*;

public class l
{
   public static ILog m_log = AppObjects.getILog();
   public static boolean bt()
   {
      if (m_log instanceof ILog1)
      {
         return ((ILog1)m_log).isItNecessaryToLog(1);
      }
      else
      {
         return true;
      }
   }
}