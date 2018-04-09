/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/interfaces/IApplication.java

package com.ai.application.interfaces;

public interface IApplication {
    
    /**
       @roseuid 3694E2F50162
     */
    public IConfig getIConfig();
    
    /**
       @roseuid 369506220319
     */
    public IFactory getIAppFactory();
    
    /**
       @roseuid 369508200326
     */
    public ILog getILog();
    /**
       @roseuid 369B99CD03D4
     */
    public void addArgumentListener(IArgumentListener listener);
    
    /**
       @roseuid 369B9A2401B2
     */
    public void removeArgumentListener(IArgumentListener listener);
    public void addAppShutDownListener(IAppShutDownListener listener);
    
    /**
       @roseuid 369B9A2401B2
     */
    public void removeAppShutDownListener(IAppShutDownListener listener);
    /**
       @roseuid 369B98F40205
     */
    public boolean startup(String[] args);
    
    /**
       @roseuid 369B9BE100CB
     */
    public boolean shutDown();
}
