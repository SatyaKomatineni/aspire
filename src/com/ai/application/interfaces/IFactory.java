/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/interfaces/IFactory.java

package com.ai.application.interfaces;

public interface IFactory {
    
    /**
       @roseuid 3694E3960290
     */
    public Object getObject(String identifier, Object args)
      throws RequestExecutionException;
}
