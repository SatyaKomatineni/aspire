/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

// public class ITagListener 
// {
//    // Could pass where applicable current state of the state machine
//    String tagDetected(ITag tag, String curState);
// } 

public class EmptyTagListener implements ITagListener 
{

   public EmptyTagListener() {
   }
   public String tagDetected(ITag tag, int curState)
   {
      return "";
   }
   public String startPage()
   {
      return "";
   }
   public String endPage()
   {
      return "";
   }
} 