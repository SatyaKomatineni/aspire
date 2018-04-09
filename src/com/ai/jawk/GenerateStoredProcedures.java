/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.jawk;

import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStreamReader;


class CatFile extends AFileProcessorListener
{
    public void newLine( final String line )
    {
        System.out.println( line + "\n" );
        
    }
}

class GenerateSP extends AFileProcessorListener
{
//    static final int AIRPORT_CODE_INDEX 
    private LineProcessor m_lp;
    private int curLineNumber = 1;
    static   final  private String sep = "'";
    static final private String comma    = ",";
    
    static final int BAX_LOCATION_INDEX     = 0;
    static final int AIRPORT_CODE_INDEX     = 1;
    static final int CITY_CODE_INDEX        = 2;
    static final int AIPORT_NAME_INDEX      = 3;
    static final int GMT_OFFSET_INDEX       = 4;
    static final int WORLD_AREA_CODE_INDEX  = 5;
    static final int LAT_DEG_INDEX          = 6;
    static final int LAT_MIN_INDEX          = 7;
    static final int LON_DEG_INDEX          = 8;
    static final int LON_MIN_INDEX          = 9;
    
    private    String wordIndex[] = { 
        "bax location",
        "airport code",
        "city code",
        "airport name",
        "gmt offset in minutes",
        "world area code",
        "lat deg",
        "lat min",
        "lon deg",
        "lon min" };

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

    public void newLine( final String line )
    {
//        System.out.println("Processing line number " + curLineNumber++ );
        m_lp = new LineProcessor( line,"\t" );
        
        StringBuffer sp = new StringBuffer("sp_insertAirport ");
        sp.append( makeAppendable(m_lp.getWord(AIRPORT_CODE_INDEX) ));
        sp.append( comma + makeAppendable( m_lp.getWord( CITY_CODE_INDEX )));
        sp.append( comma + makeAppendable(  m_lp.getWord( AIPORT_NAME_INDEX )));
        sp.append( comma + m_lp.getWord( GMT_OFFSET_INDEX ) );
        sp.append( comma + makeAppendable( m_lp.getWord( WORLD_AREA_CODE_INDEX )));
        sp.append( comma + m_lp.getWord( LAT_DEG_INDEX ) );
        sp.append( comma + m_lp.getWord( LAT_MIN_INDEX ) );
        sp.append( comma + m_lp.getWord( LON_DEG_INDEX ) );
        sp.append( comma + m_lp.getWord( LON_MIN_INDEX ) );
        if (m_lp.getWord( BAX_LOCATION_INDEX ).equals("1"))
        {
            sp.append( comma + "1" );
        }
//        sp.append( comma + m_lp.getWord( BAX_LOCATION_INDEX ) );
        
//        System.out.println( "Number of words " + m_lp.getNumberOfWords() );
        System.out.println( sp );        
        System.out.println("go");
    }
}



public class GenerateStoredProcedures
{
    private FileProcessor m_fileProcessor;
    private IFileProcessorListener     m_catFile;
    BufferedReader  m_in;

    void run( String[] args )
    {
//        System.out.println( "Number of arguments " + args.length + "\n" );
        for(int i=0; i<args.length;i++)
        {
//            System.out.println( "Argument " + i + ": " + args[i] );
        }
        if (args.length < 1)
            return;
            
        m_in = createInputReader();
        m_fileProcessor = new FileProcessor();
//        System.out.println( "processing file " + args[0] );
        String prompt = "yes";
/*        
        try {
//            prompt = m_in.readLine();
        }
        catch(IOException e )
        {
            e.printStackTrace();
            return;
        }
*/        
        if (prompt.equals("yes"))
        {
           try 
           {
               m_catFile = new GenerateSP();
               m_fileProcessor.addFileProcessorListener( m_catFile );
               m_fileProcessor.processFile( args[0] );
           }
           catch(IOException e )
           {
                e.printStackTrace();
                return;
           }
        }
        else
        {
            return;
        }

    }
    static public void main( String[] args )
    {
        GenerateStoredProcedures gsp = new GenerateStoredProcedures();
        gsp.run( args );
        return;
    }
     BufferedReader    createInputReader()
     {
         return new BufferedReader(new InputStreamReader(System.in));
     }
}
