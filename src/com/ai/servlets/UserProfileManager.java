/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import java.util.*;

public class UserProfileManager 
{
   private static UserProfileManager m_instance = new UserProfileManager();
   private Hashtable m_userProfiles = new Hashtable();
   
   private static UserProfileManager getInstance()
   {
      return m_instance;
   }
   private synchronized UserProfile getUserProfile(String userName, boolean bCreate)
   {
      UserProfile profile = (UserProfile)m_userProfiles.get(userName);
      if (profile == null)
      {
         System.out.println("Creating user profile for user " + userName );
         profile = new UserProfile(userName);
         m_userProfiles.put(userName,profile);
      }
      return profile;
   }
} 
