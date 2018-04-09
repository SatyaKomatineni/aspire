/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.htmlgen;
import com.ai.common.CommonException;

public class ControlHandlerException extends CommonException {
        static public final String CONTROL_HANDLER_NOT_FOUND = "Control Handler not found";
        public ControlHandlerException(String msg, Throwable t) 
        {
                super(msg);
                setChildException(t);
        }
        public ControlHandlerException(String msg)
        {
               this(msg,null);
        }
} 

