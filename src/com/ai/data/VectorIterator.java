/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.data;

public class VectorIterator implements IIterator
{
        int curIndex = -1;
        java.util.Vector  m_v;
        
        public VectorIterator( java.util.Vector v) 
        {
                m_v = v;
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
                return m_v.elementAt(curIndex);
        }
        
} 