/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/interfaces/IAppShutDownListener.java

package com.ai.application.interfaces;

import java.util.EventListener;


public interface IAppShutDownListener extends EventListener, IProxyListener{
    
    /**
       @roseuid 369A599103D2
     */
    public boolean canYouShutDown();
    
    /**
       @roseuid 369A5A1F0051
     */
    public boolean shutDown();
}
