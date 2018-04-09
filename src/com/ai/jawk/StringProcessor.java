/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.jawk;

import java.util.StringTokenizer;
//import java.util.StringTokenizer;
import java.util.*;
import com.ai.common.*;

public class StringProcessor
{
    static public String subst(String target, String src, String dest )
    {
        StringBuffer str = new StringBuffer();
        StringTokenizer st = new StringTokenizer( target, src, true);
        while( st.hasMoreTokens() )
        {
            String curToken = st.nextToken();
            if (curToken.equals( src ))
            {
                    str.append(dest);
            }
            else
            { 
                str.append(curToken);
            }
        }
        return new String(str);
        
    }
    public static String capitalizeFirstChar( String word )
    {
            String firstLetter = word.substring(0,1);
            String rest = word.substring(1);
            return new String (firstLetter.toUpperCase ()+ rest );
    }
    public static String convertUnderscoresToCapitals( final String varName )
    {
        StringTokenizer st = new StringTokenizer( varName, "_" );
        if (st.countTokens() <= 1)
        {
            return varName;
        }
        StringBuffer newVarName = new StringBuffer(st.nextToken());

        while(st.hasMoreTokens())
        {
            newVarName.append(capitalizeFirstChar(st.nextToken()));
        }

        return new String( newVarName);
    }
   static public String substitute(String inString, Hashtable stringArguments )
   {
      AArgSubstitutor subst = new CArgSubstitutor();
      return subst.substitute(inString, stringArguments);
   }
    
}