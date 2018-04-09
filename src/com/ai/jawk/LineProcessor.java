/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.jawk;

import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class LineProcessor
{
    Vector m_words;
    public  LineProcessor(final String line, final String delimiter)
    {
        m_words = new Vector();
        StringTokenizer st = new StringTokenizer( line, delimiter,true );
        String prevToken = delimiter;
        while( st.hasMoreTokens() )
        {
            String curToken = st.nextToken();
            if (curToken.equals( delimiter ))
            {
                if (prevToken.equals( delimiter ))
                {
                    m_words.addElement(new String(""));
                }
            }
            else
            {
                m_words.addElement( curToken );
            }
            prevToken = curToken;
        }
    }
    public Enumeration    elements()
    {
        return m_words.elements();
    }
    public String    getWord( int index )
    {
        return (String)m_words.elementAt(index);
    }
    public int getNumberOfWords()
    {
        return m_words.size();
    }
}