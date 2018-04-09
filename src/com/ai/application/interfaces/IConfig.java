/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

/*
 * Copyright (c) Active Intellect, Inc.
 * All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this
 * software and its documentation for NON-COMMERCIAL purposes
 * and without fee is hereby granted provided that this
 * copyright notice appears in all copies. Please refer to
 * the licenses restrictions for commercial use on the web site.
 * http://www.activeintellect.com/aspire
 *
 * Active  Intellect, Inc. MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. Active Intellect, Inc. SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.ai.application.interfaces;

/**
 * Retrieve configuration information.
 * Typically from properties files.
 * Could be from a database as well if implemented.
 */
public interface IConfig {
    
   /**
    * retrieve value given a key.
    * key could be hierarchical if separated by such separators as '.'
    * throws a ConfigException if key is not found.
    * Use the overloaded getValue to retrieve a key without exception.
    */
    public String getValue(String key)
        throws ConfigException;
        
    public String getValue(String key, String defaultValue);
                                     
    public String getValueFromSource(String source, String key)
        throws ConfigException;
}
