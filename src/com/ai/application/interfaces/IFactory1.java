/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.interfaces;

//**********************************************************
//*public interface IFactory {
//*    public Object getObject(String identifier, Object args)
//*      throws RequestExecutionException;
//*}
//**********************************************************

public interface IFactory1 extends IFactory
{
    public Object getObjectWithDefault(String identifier, Object args, Object defaultObject);
} 
