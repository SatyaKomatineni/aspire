/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/interfaces/IArgumentListener.java

package com.ai.application.interfaces;

import java.util.EventListener;


public interface IArgumentListener extends EventListener,IProxyListener {
    
    /**
       @roseuid 369B9C1A0204
     */
    public boolean verifyArguments(String[] args);
    
    /**
       @roseuid 369BAC9D007B
     */
    public String getName();
}
