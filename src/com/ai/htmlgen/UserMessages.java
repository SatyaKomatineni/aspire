package com.ai.htmlgen;

import com.ai.application.utils.*;

public class UserMessages
{
   public static String NO_DATA_FOUND_KEY="NO_DATA_FOUND";
   public static String NO_DATA_FOUND_KEY_DEFAULT_VALUE="No data found";

   public static String getMessage(String messageKey, String defaultValue)
   {
      return AppObjects.getValue("aspire.messages." + messageKey,defaultValue);
   }
}