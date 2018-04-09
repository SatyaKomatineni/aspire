/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.jutils;

import java.io.*;
import com.ai.jawk.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Repeat a dos command with every line as an argument
 *
 * logic:
 */
public class GenerateClassForAttributes extends AFileProcessorListener
{
    private FileProcessor m_fileProcessor;
    private    String m_args[];

    private LineProcessor m_lp;
    static   final  private String sep = "'";
    static final private String comma    = ",";

        static final int TYPE       = 0;
        static final int ATTRIBUTE        = 1;

    Vector attributes = new Vector();
    Vector gets = new Vector();
    Vector sets = new Vector();
    Vector assignments = new Vector();
    Vector constructorLines = new Vector();

//        static final int ied_processed_time_stamp= 5

    public GenerateClassForAttributes(String inArgs[])
    {
        m_fileProcessor = new FileProcessor();
        m_fileProcessor.addFileProcessorListener( this );
        m_args = inArgs;
    }

    String    makeAppendable( String inString )
    {
        if (inString.endsWith("\"") )
        {
            return inString;
        }
        else
        {
            return sep + inString + sep;
        }

    }
    public int run()
    {
        try
        {
            m_fileProcessor.processFile("stdin");
        }
        catch(IOException x)
        {
            x.printStackTrace();
            return -1;
        }
        return 0;
    }
    public void newLine( final String line )
    {
        m_lp = new LineProcessor(line," ");
        if (m_lp.getNumberOfWords() < 2)
        {
            return;
        }
        String thisType = m_lp.getWord(TYPE);
        String thisAttr = StringProcessor.convertUnderscoresToCapitals(m_lp.getWord(ATTRIBUTE));

        attributes.addElement( createAttributeLine(thisType, thisAttr) );
        gets.addElement( createGetsLine(thisType, thisAttr ));
        sets.addElement( createPutsLine(thisType, thisAttr ));
        assignments.addElement(createAssignmentLine( thisType, thisAttr ));
        constructorLines.addElement(createConstructorLine(thisType,thisAttr));

    }
    String createAttributeLine( final String thisType, final String thisAttr )
    {
        StringBuffer line = new StringBuffer("private ");
        line.append(thisType + "\t" + "m_" + thisAttr + ";");
        return new String(line);
    }
    
    String createGetsLine( final String thisType, final String thisAttr )
    {
        StringBuffer line = new StringBuffer("public ");
        line.append(thisType + "\tget" + StringProcessor.capitalizeFirstChar(thisAttr) + "(){ return m_" + thisAttr + ";}");
        return new String(line);
    }
    
    String createPutsLine( final String thisType, final String thisAttr )
    {
        StringBuffer line = new StringBuffer("public ");
        String arguments = new String("final " + thisType + "\t" + thisAttr );
        line.append("void \tset" + StringProcessor.capitalizeFirstChar(thisAttr) + "\t\t\t(" + arguments + "){ m_" + thisAttr + " = " + thisAttr + ";}");
        return new String(line);
    }
    String createAssignmentLine( final String thisType, final String thisAttr )
    {
        StringBuffer line = new StringBuffer();
        line.append("\tm_" + thisAttr + " = " + thisAttr +";");
        return new String(line);
    }
    
    String createConstructorLine( final String thisType, final String thisAttr )
    {
        StringBuffer line = new StringBuffer("\t\t\tfinal\t");
        line.append(thisType + "\t" + thisAttr + ",");
        return new String(line);
    }
    public  void endOfFile()
    {
        //printConstructorLines();
        //printAttributeLines();
        //printGetLines();
        //printSetLines();
        
        System.out.println("// ************************");
        System.out.println("// private data attributes");
        System.out.println("// *************************");
        
        printLines(attributes);
        
        
        printConstructor();        
        System.out.println("// ************************");
        System.out.println("// Assignments with in a constructor");
        System.out.println("// *************************");
        
        printLines(assignments);
        System.out.println("\t}// end of constructor");
        
        System.out.println("// ************************");
        System.out.println("// get methods");
        System.out.println("// *************************");
        printLines(gets);
        
        System.out.println("// ************************");
        System.out.println("// set methods");
        System.out.println("// *************************");
        printLines(sets);
    }
    void printConstructor()
    {
         String line1 = new String("public constructor (");
         System.out.println(line1);
         printLines(constructorLines);
         System.out.println("\t\t)");
         System.out.println("\t{");
    }
    void printLines(Vector strings )
    {
        for(Enumeration e=strings.elements();e.hasMoreElements();)
        {
            System.out.println( (String)e.nextElement() );
        }
    }
    
    public static    String convertDateFormat( String inDate )
    {
        if (inDate.equals(""))
        {
            return "January 1 1973 4:30AM";
        }

//        DateFormat.ge
//        January 1, 1900, 4:33AM
        SimpleDateFormat m_df = new SimpleDateFormat("MMM d yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        int     year =     Integer.parseInt(inDate.substring(0,4));
        int     month = Integer.parseInt(inDate.substring(4,6));
        int     date = Integer.parseInt(inDate.substring(6,8));
        int     hour = Integer.parseInt(inDate.substring(8,10));
        int     min = Integer.parseInt(inDate.substring(10,12));
        cal.set(year,month,date,hour,min);
        String outputStr = m_df.format(cal.getTime());
        return outputStr;
    }


    static public void main( String[] args )
    {
        GenerateClassForAttributes gsp = new GenerateClassForAttributes(args);
        gsp.run();
//        String s =  GenerateEventSPs.convertDateFormat( "199705290900" );
//        System.out.println(s);
        return;
    }
}

