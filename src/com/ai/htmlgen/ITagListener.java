/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

/**
 * Used by an AITransform so that it can respond to tags
 * For each tag detected it will pass back a string to be substituted
 * 
 */
public interface ITagListener 
{
   String startPage();
   // Could pass where applicable current state of the state machine
   String tagDetected(ITag tag, int curState);
   String endPage();
} 
