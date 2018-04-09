package com.ai.application.interfaces;

/*
 * IInitializable
 * 
 * This interface interacts with a factory.
 * If implemented, a factory will call this method
 * after instantiating the class.
 * 
 * This is most applicable when the class is
 * multi-instance.
 * 
 * If it is a single instance then the execute method
 * might as well do the job of this function.
 * 
 * @see ICreator
 * @see ISingleThreaded
 */
public interface IInitializable
{
    public void initialize(String requestName);
}