package com.ai.data;
import java.util.*;
import java.util.Enumeration;

public class EnumerationIterator implements IIterator {

    private Enumeration m_e;
    private Object m_curObject;
    private boolean m_bAtTheEnd = false;

    public EnumerationIterator(Enumeration e) {
        m_e = e;
    }
    public void moveToFirst()
              throws DataException
    {
        if (m_e.hasMoreElements())
        {
            m_curObject = m_e.nextElement();
            m_bAtTheEnd = false;
        }
        else
        {
            //no more elements
            m_bAtTheEnd = true;
        }
    }
    public void moveToNext()
              throws DataException
    {
        if (m_e.hasMoreElements())
        {
            m_curObject = m_e.nextElement();
            m_bAtTheEnd = false;
        }
        else
        {
            //no more elements
            m_bAtTheEnd = true;
        }
    }
    public boolean isAtTheEnd()
              throws DataException
    {
            return m_bAtTheEnd;
    }
    public Object getCurrentElement()
                  throws DataException
    {
        return m_curObject;
    }

}