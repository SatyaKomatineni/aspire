/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.filters;
import java.util.*;
import com.ai.common.*;

public class FilterUtils 
{
   public static boolean convertToBoolean(Object reply)
      throws com.ai.common.UnexpectedTypeException
   {
         if (reply == null)
         {
            throw new UnexpectedTypeException("null");
         }
         
         if (reply instanceof Boolean)
         {
            return ((Boolean)reply).booleanValue();
         }
         if (reply instanceof Hashtable)
         {
            Hashtable h = (Hashtable)reply;
            if (h.isEmpty())
            {
               return false;
            }
            String typedReply = (String)h.get("result");
            if (typedReply.equals("false"))
            {
               return false;
            }
            if (typedReply.toLowerCase().equals("false"))
            {
               return false;
            }
            return true;
         } 
         if (reply instanceof String)
         {
            if (reply.equals("false") || reply.equals("no"))
            {
               return false;
            }
            String lowerCaseReply = ((String)reply).toLowerCase();
            if (lowerCaseReply.equals("false") || lowerCaseReply.equals("no"))
            {
               return false;
            }
            return true;
         }   
         throw new UnexpectedTypeException(reply);         
   }
   public static boolean convertToBoolean(Object reply, boolean defaultValue)
   {
      if (reply == null)
      {
      	return defaultValue;
      }
      
      if (reply instanceof Boolean)
      {
         return ((Boolean)reply).booleanValue();
      }
      if (reply instanceof String)
      {
      	return StringUtils.ConvertStringToBoolean((String)reply, defaultValue);
      }   
      if (reply instanceof Hashtable)
      {
         Hashtable h = (Hashtable)reply;
         if (h.isEmpty())
         {
            return false;
         }
         String typedReply = (String)h.get("result");
       	 return StringUtils.ConvertStringToBoolean(typedReply, defaultValue);
      } 
      throw new RuntimeException("Unknown Type in converting to boolean:" + reply.getClass().getName());         
   }
} 
