/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.common;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class Tokenizer {

        public Tokenizer() {
        }
        static public  void tokenizeInto( String inString, String separators, Vector v)
        {
                StringTokenizer tokenizer = new StringTokenizer(inString, separators );
                while(tokenizer.hasMoreTokens())
                {
                        v.addElement(tokenizer.nextToken());
                }
        }
        static public  void tokenizeInto( String inString, String separators, List l)
        {
                StringTokenizer tokenizer = new StringTokenizer(inString, separators );
                while(tokenizer.hasMoreTokens())
                {
                        l.add(tokenizer.nextToken());
                }
        }
        static public  Vector tokenize( String inString, String separators)
        {
                Vector v = new Vector();
                StringTokenizer tokenizer = new StringTokenizer(inString, separators );
                while(tokenizer.hasMoreTokens())
                {
                        v.addElement(tokenizer.nextToken());
                }
                return v;
        }
        static public  List<String> tokenizeAsList( String inString, String separators)
        {
                List<String> v = new ArrayList<String>();
                StringTokenizer tokenizer = new StringTokenizer(inString, separators );
                while(tokenizer.hasMoreTokens())
                {
                        v.add(tokenizer.nextToken());
                }
                return v;
        }
        /**
         * Provides for empty tokens
         */
        static public  Vector tokenize( String inString, String separators, String emptyObject)
        {
                final int TOK = 1;
                final int SEP = 2;
                int expected = TOK;
                int tokenType=0;
                String value =null;

                Vector v = new Vector();
                StringTokenizer tokenizer = new StringTokenizer(inString, separators,true );
                while(tokenizer.hasMoreTokens())
                {
                  String token = tokenizer.nextToken();
                  tokenType = (separators.indexOf(token) == -1) ? TOK : SEP;
                  switch (tokenType)
                  {
                     case TOK:
                        switch (expected)
                        {
                           case TOK:
                              v.addElement(token);
                              break;
                           case SEP:
                              break;
                        }
                        expected = SEP;
                        break;
                     case SEP:
                        switch (expected)
                        {
                           case TOK:
                              v.addElement(emptyObject);
                              break;
                           case SEP:
                              break;
                        }
                        expected=TOK;
                        break;
                  } // end of switch
                }
                if (expected == TOK)
                  v.addElement(emptyObject);
                return v;
        }
        /**
         * Provides a faster way to find out if a string is a member of a string set
         */
        static public  Hashtable tokenizeAsAHashtable( String inString, String separators)
        {
                Hashtable v = new Hashtable();
                StringTokenizer tokenizer = new StringTokenizer(inString, separators );
                while(tokenizer.hasMoreTokens())
                {
                        String thisTok = tokenizer.nextToken();
                        v.put(thisTok,thisTok);
                }
                return v;
        }
        /**
         * Make a hashtable out of arg1=a,arg2=b
         */
        static public Hashtable tokenizeArgsInto(final String inString, Hashtable ht)
        {
            return tokenizeArgsInto(inString,ht,false);
        }

        static public Hashtable tokenizeArgsInto(final String inString, Hashtable ht, boolean lowerCase)
        {
            if (inString == null)
            {
               // No arguments to tokenize
               return ht;
            }
            Vector v = tokenize(inString,",");
            for(Enumeration e=v.elements();e.hasMoreElements();)
            {
               String pair = (String)e.nextElement();
               Vector pairV = tokenize(pair,"=");
               if (lowerCase)
               {
                  ht.put(((String)pairV.elementAt(0)).toLowerCase(),pairV.elementAt(1));
               }
               else
               {
                  ht.put(pairV.elementAt(0),pairV.elementAt(1));
               }
            }
            return ht;
        }
        static public List<String> tokenizeWithEscapeChar(String inString, char sepChar)
        {
        	String expression = "(?<!\\\\)" + sepChar;
        	String[] col = inString.split(expression);
        	List<String> stringList = Arrays.asList(col);
        	return stringList;
        }
        
        static public List<String> tokenizeWithEscapeCharDecoded(String inString, char sepChar)
        {
        	ArrayList<String> ol = new ArrayList<String>();
        	String expression = "(?<!\\\\)" + sepChar;
        	String[] col = inString.split(expression);
        	List<String> stringList = Arrays.asList(col);
        	for(String s : stringList)
        	{
        		String ns = StringUtils.decode(s, '\\', sepChar, sepChar);
        		ol.add(ns);
        	}
        	return ol;
        }
        static public void main(String args[])
        {
        	test2();
        	
        }
        static private void test1()
        {
            System.out.println(Tokenizer.tokenize("a,,b",",","null"));
            System.out.println(Tokenizer.tokenize("a,,b,",",","null"));
            System.out.println(Tokenizer.tokenize("a,,,b",",","null"));
            System.out.println(Tokenizer.tokenize(",a,,b",",","null"));
            String test = "0010001635,4383,000000000045001523,7777,7,87,107,1,1,17,77,1,,97,7,7,1,2,1,2,1,7";
            System.out.println(Tokenizer.tokenize(test,",","null"));
        }
        static private void test2()
        {
            String test1 = "15,a < b";
            String test2 = "15,45";
            String test3 = "15,40\\,67\\,87 \\\\\\,blah";
            System.out.println(test3);
            List<String> l = Tokenizer.tokenizeWithEscapeChar(test3,',');
            System.out.println(l.size());
            System.out.println(l);
            String s2 = l.get(1);
            System.out.println("Second string:" + s2);
            String s2d = StringUtils.decode(s2, '\\', ',', ',');
            System.out.println("Second string decoded:" + s2d);
        }
}