/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/interfaces/ILog.java

package com.ai.application.interfaces;

public interface ILog {
    
    /**
       @roseuid 369507DE0105
     */
    public void log(String message, String msgType);
    public void log(Throwable t);
    public void log(String cause, Throwable t);
}
