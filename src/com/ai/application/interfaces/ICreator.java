/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Source file: d:/cbgen/com/ai/application/interfaces/ICreator.java

package com.ai.application.interfaces;

/*
 * Signature used by a factory implementation to
 * create an instance of an object by calling the execute method.
 * 
 * The object doesn't have to implement ICreator to be invoked by a factory.
 * 
 * An object can be one of the following whether it is a
 * creator or not
 * 
 * 1. IInitializable
 * 2. ISingleThreaded (indicating multi instance)
 * 
 * By default an object created by a factory is a singleton.
 * See FilterEnabledFactor4 for full understanding.
 * 
 * @see ICreator
 * @see ISingleThreaded
 * @see IInitializable 
 * @see ITask
 * 
 */
public interface ICreator 
{
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException;
}
