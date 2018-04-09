package com.ai.htmlgen;
import com.ai.data.*;
import com.ai.common.*;
import java.util.*;
public interface IUpdatableFormHandler extends IFormHandler1 
{
   // adding individual keys
   void addKey(String key, String value) throws DataException;
   void addDictionary(IDictionary dict) throws DataException;
   void addMap(Map map) throws DataException;

   // adding loops
   void addControlHandler(String controlHandler, IControlHandler loop) throws DataException;
} 
