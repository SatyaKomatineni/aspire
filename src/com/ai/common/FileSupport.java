package com.ai.common;

import com.ai.application.utils.*;
import java.io.*;
/**
 * Can only be called after initialization is complete
 */
public class FileSupport
{
   private static boolean bDifferentFileSeparators = false;
   private static char cSpecifiedFileSeparatorChar = '\\';
   static
   {
      String specifiedFileSeparator = AppObjects.getIConfig().getValue("directories.file_separator","\\");
      cSpecifiedFileSeparatorChar = specifiedFileSeparator.charAt(0);
      if (specifiedFileSeparator.equals(File.separator) == false)
      {
         AppObjects.warn("ProtocolResource","Different file separators between development and deployment environments");
         bDifferentFileSeparators = true;
     }
   }

   static char getSpecifiedFileSeparator()
   {
      return cSpecifiedFileSeparatorChar;
   }

   static boolean isFileSeparatorDifferent()
   {
      return (cSpecifiedFileSeparatorChar == File.pathSeparatorChar);
   }

   static String translateResourceForFileSeparators(String resourcename)
   {
      if (!(isFileSeparatorDifferent()))
      {
         //not diffrent : same
         return resourcename;
      }
      //different
      return resourcename.replace(getSpecifiedFileSeparator(),File.pathSeparatorChar);

   }//eof-method
}//eof-class