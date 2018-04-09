/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;
import java.util.*;
import com.ai.application.utils.AppObjects;

public class VectorMetaData implements IMetaData
{
        Vector          m_v;
        Hashtable       attributesToIndex = new Hashtable();
        
        public VectorMetaData(Vector v)
        {
                m_v = v;
                for(int i=0;i<v.size();i++)
                {
                        attributesToIndex.put(((String)v.elementAt(i)).toLowerCase(),new Integer(i));
                }
        }
        public IIterator getIterator()
        {
                return new VectorIterator(m_v);
        }
        
        public int   getColumnCount()
        {
            return m_v.size();
        }
        
        public int getIndex(final String attributeName)
                throws FieldNameNotFoundException
        {
                try 
                {
                        AppObjects.log("vectorMetadata: Request for column : " + attributeName );
                        int rtnValue = ((Integer)(attributesToIndex.get(attributeName.toLowerCase()))).intValue();
                        AppObjects.log("vectorMetadata: Returned index for column : " + rtnValue );
                        return rtnValue;
                }
                catch(java.lang.NullPointerException x)
                {
                        throw new FieldNameNotFoundException();
                }
        }
}