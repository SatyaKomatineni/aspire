package com.ai.htmlgen;
import com.ai.common.*;
import java.util.*;
import com.ai.data.*;
import com.ai.application.utils.*;
/**
 * Reusable Default implementation
 */
/**
 * Adds an enumeration method for obtaining loop handlers for
 * this form handler.
 *
 * Enumeration would be empty if there are no form handlers.
 *
 * Base class interface: IFormHandler's interface
 ***************************************************
 *  public String getValue(final String key);
 *  public IIterator getKeys();
 *  public IControlHandler getControlHandler(final String name )
 *       throws ControlHandlerException;
 *  public void formProcessingComplete();        
 *  public boolean isDataAvailable();
 *
   public interface IFormHandler1 extends IFormHandler 
   {
      //
      //  return an Enumeration of loop handler objects
      public Enumeration getControlHandlerNames();
   }
*/
public class DUpdateFormHandler implements IUpdatableFormHandler 
{
   private HashMap m_keys = new HashMap();
   private IDictionary m_keySource;
   private HashMap m_loops = new HashMap();
   
   public DUpdateFormHandler()
   {  
      m_keySource = new MapDictionary(m_keys);
      m_keySource.addChild(new ConfigDictionary(AppObjects.getIConfig()));
   }
   
   public String getValue(final String key)
   {
      return (String)m_keySource.get(key.toLowerCase());
   }
   
   public IIterator getKeys()
   {
      ArrayList m_list = new ArrayList();
      m_keySource.getKeys(m_list);
      return new IteratorConverter(m_list.iterator());
   }
   /**
    * returns an object that is responsible for loop data.
    * throws an exception if the loop handler is not found
    */
   public IControlHandler getControlHandler(final String name )
        throws ControlHandlerException
   {
      IControlHandler loop = (IControlHandler)m_loops.get(name);
      if (loop != null) return loop;
      throw new ControlHandlerException("Warn: Control handler not found: " + name);
   }        
   /**
    * indicates that the data is no longer required by the page.
    * Internal resources could be closed.
    */
   public void formProcessingComplete()
   {
   }
   /**
    * returns false if there is no data in the form.
    */
   public boolean isDataAvailable()
   {
      return true;
   }
   //*********************************************
   // Methods from the IUpdatableFormHandler
   //*********************************************
   // adding individual keys
   public void addKey(String key, String value) throws DataException
   {
      m_keys.put(key,value);
   }
   public void addDictionary(IDictionary dict) throws DataException
   {
      m_keySource.addChild(dict);
   }
   public void addMap(Map map) throws DataException
   {
      m_keySource.addChild(new MapDictionary(map));
   }

   // adding loops
   public void addControlHandler(String controlHandlerName, IControlHandler loop) throws DataException
   {
      m_loops.put(controlHandlerName,loop);
   }
   
   //*********************************************
   // Methods from the IUpdatableFormHandler
   //*********************************************
   /**
    * return an Enumeration of loop handler objects
    */
   public Enumeration getControlHandlerNames()
   {
      return new IteratorEnumerator(m_loops.keySet().iterator());
   }
   
}
