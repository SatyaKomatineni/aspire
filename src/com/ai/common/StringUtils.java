/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.ai.application.utils.AppObjects;

public class StringUtils
{
   public static String encode(final String inS, char escChar, char fromChar, char toChar )
   {
      // return empty strings as they are
      if (inS == null)
      {
         return inS;
      }
      if (inS.length() == 0)
      {
         return inS;
      }
      StringBuffer thisBuffer = new StringBuffer();
      for(int i=0;i<inS.length();i++)
      {
         char thisChar = inS.charAt(i);
         if (thisChar == fromChar)
         {
            thisBuffer.append(escChar);
            thisBuffer.append(toChar);
         }
         else if (thisChar == escChar)
         {
            thisBuffer.append(escChar);
            thisBuffer.append(escChar);
         }
         else
         {
            // neither escape nor from char
            thisBuffer.append(thisChar);
         }
      }// end of for
      return thisBuffer.toString();
   }// end of func

   public static String decode(final String inS, char escChar, String fromCharString, String toCharString )
   {
      // return empty strings as they are
      if (inS == null)
      {
         return inS;
      }
      if (inS.length() == 0)
      {
         return inS;
      }
      // return if the from char not found
      int firstOccurance = inS.indexOf(escChar);
      if (firstOccurance == -1)
      {
         // if there is no escChar let it go
         // nothing to decode
         return inS;
      }
      StringBuffer thisBuffer = new StringBuffer(inS.substring(0,firstOccurance));
      for(int i=firstOccurance;i<inS.length();i++)
      {
         char thisChar = inS.charAt(i);
         if (thisChar == escChar)
         {
            // write the next char
            char nextChar = inS.charAt(i+1);
            char translatedChar = translateCharacter(nextChar,toCharString,fromCharString);
            thisBuffer.append(translatedChar);
            i++;
            // not to have a next char may be an error.
         }
         else
         {
            // this is not an escape char
            thisBuffer.append(thisChar);
         }
      }// end of for
      return thisBuffer.toString();
   }// end of func
   
   public static char translateCharacter(char inChar, String fromString, String toString)
   {
   	  int i = fromString.indexOf(inChar);
   	  if (i == -1)
   	  {
   	  	return inChar;
   	  }
   	  //Available in the in string
   	  return toString.charAt(i);
   }

   public static String decode(final String inS, char escChar, char fromChar, char toChar )
   {
      // return empty strings as they are
      if (inS == null)
      {
         return inS;
      }
      if (inS.length() == 0)
      {
         return inS;
      }
      // return if the from char not found
      int firstOccurance = inS.indexOf(escChar);
      if (firstOccurance == -1)
      {
         // if there is no escChar let it go
         // nothing to decode
         return inS;
      }
      StringBuffer thisBuffer = new StringBuffer(inS.substring(0,firstOccurance));
      for(int i=firstOccurance;i<inS.length();i++)
      {
         char thisChar = inS.charAt(i);
         if (thisChar == escChar)
         {
            // write the next char
            char nextChar = inS.charAt(i+1);
            if (nextChar == toChar)
            {
               thisBuffer.append(fromChar);
            }
            else
            {
               thisBuffer.append(nextChar);
            }
            i++;
            // not to have a next char may be an error.
         }
         else
         {
            // this is not an escape char
            thisBuffer.append(thisChar);
         }
      }// end of for
      return thisBuffer.toString();
   }// end of func
   
   public static Vector splitAtFirst(String inStr, int separator)
   {
      Vector v = new Vector();
      int index = inStr.indexOf(separator);
      if (index == -1)
      {
         v.addElement(inStr);
         return v;
      }
      v.addElement(inStr.substring(0,index));
      v.addElement(inStr.substring(index+1));
      return v;
   }

   /**
    * Returns the literal values inside of double quotes
    * returns null if it is not a literal value (doesn't start with a quote)
    * returns empty space if the string is ""
    * @param val
    * @return
    */
   public static String getLiteralValue(String val)
   {
      //trim so that we can allow for empty spaces
      String newVal = val.trim();

      //if the first char after trimming is a " then we have a literal
      if (newVal.charAt(0) != '\"')
      {
         //first char is not a literal char
         return null;
      }

      //first char is a ". See if this is an empty string
      if (newVal.charAt(1) == '\"')
      {
         //second char is also a "
         return "";
      }

      //second char is not a "
      return newVal.substring(1,newVal.length()-1);
   }
   
   //*******************************************************************
   //escapeDoubleQuotes(String key, IDictionary args)
   //*******************************************************************
   public static String escapeDoubleQuotesS(String value)
   {
       if (value == null)
       {
         return "";
       }
       return StringUtils.encode(value,'\\','"','"');
   }//eof-function
   
   //*******************************************************************
   //urlEncode(String key, IDictionary args)
   //*******************************************************************
   public static String urlEncodeS(String value)
   {
       try
       {
 	      if (value == null)
 	      {
 	        return "";
 	      }
 	      return URLEncoder.encode(value,"UTF-8");
       }
       catch(UnsupportedEncodingException x)
       {
           AppObjects.error("StringUtils:urlEncodeS","Trying to url encode a string. UTF 8 conversion is not supported.");
           return "";
       }
   }//eof-function
   
   public static String htmlEncode(String inHtmlString)
   {
	   String fromCharString="<>&";
	   String[] toStringArray = { "&lt;", "&gt;", "&amp;" };
	   return encode(inHtmlString, fromCharString, toStringArray);
   }

	   public static String encode(final String inS
		   	, String fromCharString
		   	, String[] toCharStringArray) 
	{
	  // return empty strings as they are
	  if (inS == null)
	  {
	     return inS;
	  }
	  if (inS.length() == 0)
	  {
	     return inS;
	  }
	  StringBuffer thisBuffer = new StringBuffer();
	  for(int i=0;i<inS.length();i++)
	  {
	     char thisChar = inS.charAt(i);
	     //it is not an escape character
	     String translatedString = 
	    	 StringUtils.translateCharacter(thisChar
	    			 , fromCharString
	    			 , toCharStringArray);
	     if (translatedString == null)
	     {
	    	 //this character is not one of the 
	    	 //chars that needs to be translated
	    	 thisBuffer.append(thisChar);
	    	 continue;
	     }
	     //needs translation
	     thisBuffer.append(translatedString);
	  }// end of for
	  return thisBuffer.toString();
	}// end of func

   
	public static String translateCharacter(char inChar
			   , String fromString
			   , String[] toStringArray)
	{
		  int i = fromString.indexOf(inChar);
		  if (i == -1)
		  {
		  	return null;
		  }
		  //Available in the in string
		  return toStringArray[i];
	}//eof-fuction
	
	   public static boolean isEmpty(String s)
	   {
		   if (s == null) return true;
		   //s is not null
		   if (s.equals(""))
		   {
			   return true;
		   }
		   String trimmed = s.trim();
		   if (trimmed.equals(""))
		   {
			   return true;
		   }
		   return false;
	   }
	   
	   public static boolean ConvertStringToBoolean(String rs, boolean defaultValue)
	   {
	  	 if (StringUtils.isEmpty(rs) == true)
	  	 {
	  	 	return defaultValue;
	  	 }
	     if (rs.equalsIgnoreCase("false") 
	     		|| rs.equalsIgnoreCase("no") 
				|| rs.equalsIgnoreCase("n"))
	     {
	        return false;
	     }
	     return true;
	   }
	   
	   public static boolean isValid(String s)
	   {
		   return !isEmpty(s);
	   }
	   
	   public static boolean isNotANumber(String s)
	   {
		   return !isNumber(s);
	   }
	   
	   public static boolean isNumber(String s)
	   {
		   if (StringUtils.isEmpty(s))
		   {
			   //it is an empty string
			   //null or white space
			   return false; //it is not a number
		   }
		   //it is a valid string non empty
		   try
		   {
			   double d = Double.parseDouble(s);
		   }
		   catch(NumberFormatException x)
		   {
			   //Invalid number
			   AppObjects.log(x);
			   return false;
		   }
		   //it is not empty
		   //it can be converted to a double
		   //so it is a number
		   return true;
	   }
   
   /**
    * test main
    */
   public static void main(String[] args)
   {
      String ins1 = "satya?dddpp";
      String ins2 = "satya?";
      String ins3 = "satya";
      String ins4 = "satya???ppp";
      String ins5 = "pp";
      String ins6 = "\\servlets\\com.ai.servlets.PageDispatcherServlet?a=b&d=c";
      String ins7 = "someother stuff \\servlets\\com.ai.servlets.PageDispatcherServlet?a=b&d=c";
      String ins8 = "<%=ctx%>";

      String eins1 = encode(ins1,'\\','?','p');
      String eins2 = encode(ins2,'\\','?','p');
      String eins3 = encode(ins3,'\\','?','p');
      String eins4 = encode(ins4,'\\','?','p');
      String eins5 = encode(ins5,'\\','?','p');
      String eins6 = encode(ins6,'\\','?','p');
      String eins7 = encode(ins7,'\\','?','p');
      String eins8 = "\\l\\p=ctx\\p\\g";

      String deins1 = decode(eins1,'\\','?','p');
      String deins2 = decode(eins2,'\\','?','p');
      String deins3 = decode(eins3,'\\','?','p');
      String deins4 = decode(eins4,'\\','?','p');
      String deins5 = decode(eins5,'\\','?','p');
      String deins6 = decode(eins6,'\\','?','p');
      String deins7 = encode(eins7,'\\','?','p');
      String deins8 = decode(eins8,'\\',"<>%","lgp");
      
      
      System.out.println(ins1 + "===>>" + eins1 + "===>>" + deins1);
      System.out.println(ins2 + "===>>" + eins2 + "===>>" + deins2 );
      System.out.println(ins3 + "===>>" + eins3 + "===>>" + deins3 );
      System.out.println(ins4 + "===>>" + eins4 + "===>>" + deins4 );
      System.out.println(ins5 + "===>>" + eins5 + "===>>" + deins5);
      System.out.println(ins6 + "===>>" + eins6 + "===>>" + deins6);
      System.out.println(ins7 + "===>>" + eins7 + "===>>" + deins7);
      System.out.println(ins8 + "===>>" + eins8 + "===>>" + deins8);
      
      String html1 = new String("<p>Helloworld a &lt; b</p>");
      String eHtml1 = StringUtils.htmlEncode(html1);
      System.out.println(html1 + "===>>" + eHtml1);
      
   }
}
