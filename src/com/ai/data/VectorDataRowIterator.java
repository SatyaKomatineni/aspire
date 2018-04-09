/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;

public class VectorDataRowIterator implements IIterator
{
        int curIndex = -1;
        java.util.Vector  m_v;
        IMetaData m_metadata = null;
        
        public VectorDataRowIterator( java.util.Vector v, IMetaData metadata) 
        {
                m_v = v;
                m_metadata = metadata;
                moveToFirst();
        }
        public void moveToFirst()
        {
                curIndex = 0;
        }
        public void moveToNext()
        {
                curIndex++;
        }
        public boolean isAtTheEnd()
        {
                if (curIndex >= m_v.size())
                {
                        return true;
                }
                return false;
        }
        public Object getCurrentElement()
        {
                return new DataRow(m_metadata,(String)m_v.elementAt(curIndex),"|");
        }
        
} 