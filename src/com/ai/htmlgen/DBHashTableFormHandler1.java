package com.ai.htmlgen;
import com.ai.common.IUpdatableMap;
import com.ai.data.*;
import java.util.*;
import com.ai.application.utils.*;

public class DBHashTableFormHandler1 
		extends DBHashTableFormHandler 
		implements ihds
{
    private boolean bAtTheEnd = false;
    public ihds getParent()
            throws DataException
    {
        return null;
    }
    public IIterator getChildNames()
            throws DataException
    {
        return new EnumerationIterator(this.getControlHandlerNames());
    }
    public ihds getChild(String childName)
            throws DataException
    {
        IControlHandler hd = null;
        try
        {
            hd = getControlHandler(childName);
        }
        catch(ControlHandlerException x)
        {
            throw new DataException("Error: Could not get a control handler",x);
        }

        //Got a control handler
        if (hd instanceof ihds)
        {
            return (ihds)hd;
        }
        throw new DataException("Error:Loop needs to support ihds. Upgrade your Table handlers to 6 or greater");
    }
    public String getAggregateValue(String keyname)
            throws DataException
    {
        return this.getValue(keyname);
    }
    public IMetaData getMetaData()
            throws DataException
    {
        Vector names = new Vector();
        IIterator keys = getKeys();
        for(keys.moveToFirst();!keys.isAtTheEnd();keys.moveToNext())
        {
            String key = (String)keys.getCurrentElement();
            names.add(key);
        }
        return new VectorMetaData(names);
    }

    /**********************************************************
     * Implementing loop forward iterator
     * ********************************************************
     */

    public void moveToFirst()
              throws DataException
    {
        bAtTheEnd = false;
    }

    public void moveToNext()
              throws DataException
    {
        bAtTheEnd = true;
    }

    public boolean isAtTheEnd()
                  throws DataException
    {
        return bAtTheEnd;
    }

    public void close() throws DataException
    {
        formProcessingComplete();
    }

    public boolean gotoNextRow()
    {
        try
        {
            if (bAtTheEnd == true) return false;
            moveToNext();
            return true;
        }
        catch(DataException x)
        {
            AppObjects.log("Error: DataException in DBHashtableFormHandler1",x);
            return false;
        }
    }//eof-function

}//eof-class