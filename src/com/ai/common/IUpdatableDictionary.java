package com.ai.common;

public interface IUpdatableDictionary extends IDictionary 
{        
   /**
    * sets the key to value 
    * adds the key if the key doesnt exist
    * returns the dictionary itself back for chaining
    */
   public IUpdatableDictionary set(Object key, Object value);
} 
