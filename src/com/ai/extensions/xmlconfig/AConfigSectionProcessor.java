
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;

import com.ai.application.interfaces.*;

public abstract class AConfigSectionProcessor implements ICreator, IConfigSectionProcessor
{
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
    {
      return this;
    }
} 
