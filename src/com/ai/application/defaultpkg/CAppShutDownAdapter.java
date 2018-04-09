/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/default/CAppShutDownAdapter.java

package com.ai.application.defaultpkg;

import com.ai.application.interfaces.IAppShutDownListener;


public abstract class CAppShutDownAdapter implements IAppShutDownListener {
    
    CAppShutDownAdapter() {
    }
    /**
       @roseuid 369A5E0A0324
     */
    public boolean canYouShutDown() {
      return true;
    }
    
    /**
       @roseuid 369A5E0A0374
     */
    public boolean shutDown() {
      return true;
    }
}
