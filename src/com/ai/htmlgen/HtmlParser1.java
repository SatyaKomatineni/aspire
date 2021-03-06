/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.htmlgen;
import java.io.*;
import com.ai.application.utils.AppObjects;

public class HtmlParser1 {

   public static void processHtmlPage( String htmlFilename
                                        ,PrintWriter writer
                                        , IFormHandler formHandler
                                        )
        throws java.io.IOException
      {
        HtmlParser1 parser = new HtmlParser1();
        parser.parseFile(htmlFilename, writer, formHandler);
   }

   static final int STATE_LOOP_BEGIN_ENCOUNTERED = 2;
   static final int STATE_INSIDE_LOOP = 1;
   static final  int STATE_OUTSIDE_LOOP = 3;

   // required for marking a stream to perform loops
   static final int FILE_IO_MARK_READ_AHEAD_LIMIT = 4096;

   static final String TAGS_RLF_TAG = "<!--RLF_TAG";
   static final int TAGS_RLF_TAG_LENGTH = TAGS_RLF_TAG.length();
   static final String TAGS_REPLACE_TAG = "REPLACE_BGN";
   static final String TAGS_REPLACE_END_TAG = "REPLACE_END";
   static final String TAGS_END_LOOP_TAG = "END_LOOP";
   static final String TAGS_BGN_LOOP_TAG = "BGN_LOOP";

   // object level variables
   IFormHandler         m_formHandler;
   int                  m_turn = 1;
   IControlHandler      m_curControlHandler;
   int                  m_parserState = STATE_OUTSIDE_LOOP;
   boolean              m_bMarkAtEveryNewLineRead = true;
   boolean              m_bReplacementInProgress = false;
   PrintWriter          m_printWriter;

  protected HtmlParser1() {
  }
  public void parseFile(String htmlFilename, PrintWriter writer, IFormHandler formHandler)
        throws java.io.FileNotFoundException
        ,java.io.IOException
  {
      System.out.println("Inside the parser");
  // begin with an outside loop state
      m_formHandler = formHandler;
      m_printWriter = writer;

      BufferedReader reader = new BufferedReader(new FileReader(htmlFilename));
      String line;
      boolean bPrintTheRestOfTheLine = true;
      boolean bIgnoreTags = false;
      while((line = reader.readLine()) != null)
      {
               int beginIndex = 0;
               int curTagIndex =0;
               RLFTag1 curRLFTag = null;
               // per line processing
               // multiple tags are allowed on this line
               // Including loops with in a line
               while((curRLFTag = nextTag(line,beginIndex)) != null)
               {
                        curTagIndex = curRLFTag.getStartIndex();

                        if(bIgnoreTags == true )
                        {
                            // SPECIAL CASE where the control handler fetched no data
                            // Only look for the end tag
                            if (!curRLFTag.getTagName().equals(this.TAGS_END_LOOP_TAG))
                            {
                                 beginIndex = curRLFTag.getTagEndIndex();
                                 continue;
                            }
                            else
                            {
                              // found the end tag
                              // let the following end tag process the flow
                            }
                        }
                        else
                        {
                             // write the stream so far up to the tag
                             // NORMAL CASE
                             writeToStream(line.substring(beginIndex,curTagIndex));
                        }                             

                        // ****************************************************
                        // Let each tag handle it's own responsibility
                        // based on a state machine
                        // ****************************************************
                                                                               
                        if (curRLFTag.getTagName().equals(TAGS_REPLACE_TAG))
                        {
                                // write replacement code here
                                // Do nothing for right now
                                if (m_parserState == STATE_INSIDE_LOOP)
                                {
                                        if (m_curControlHandler != null)
                                        {
                                                writeToStream(m_curControlHandler.getValue(
                                                        curRLFTag.getDefaultAttributeValue()
                                                        ,m_turn));
                                        }
                                        else
                                        {
                                                writeToStream("Control handler not found");
                                        }
                                }
                                else
                                {
                                        writeToStream(m_formHandler.getValue(curRLFTag.getDefaultAttributeValue()));
                                }
                                m_bReplacementInProgress = true;
                        }
                        else if (curRLFTag.getTagName().equals(TAGS_REPLACE_END_TAG))
                        {
                                m_bReplacementInProgress = false;
                        }
                        else if (curRLFTag.getTagName().equals(TAGS_BGN_LOOP_TAG))
                        {
                                if (m_parserState == STATE_INSIDE_LOOP)
                                {
                                        // already in the loop
                                        // increment the turn
                                        m_turn++;
                                }
                                else
                                {
                                        // inside the loop for the first time for
                                        // this loop handler
                                        m_parserState = STATE_INSIDE_LOOP;

                                        // remember the position of the loop
                                        markReaderAtTheBeginingOfTheLoop(reader,curTagIndex);
                                        m_turn = 1;

                                        // get control handler
                                        try 
                                        {
                                                m_curControlHandler
                                                = m_formHandler.getControlHandler(
                                                        curRLFTag.getDefaultAttributeValue());
                                                if (m_curControlHandler.isDataAvailable() == false)
                                                {
                                                   if (m_curControlHandler.eliminateLoop() == true)
                                                   {
                                                      bIgnoreTags = true;
                                                      reportNoDataFound();
                                                   }
                                                }
                                        }                                                        
                                        catch(ControlHandlerException x)
                                        {
                                                AppObjects.log(AppObjects.LOG_CRITICAL,x);
                                                m_curControlHandler = null;
//                                                bIgnoreTags = true;
//                                                reportNoControlHandlerFound();
                                        }

                                }
                        }
                        else if (curRLFTag.getTagName().equals(TAGS_END_LOOP_TAG))
                        {
                                // end loop seen

                                // Ask control handler if you want to continue
                                // if continue
                                boolean bContinueFlag = false;
                                if (m_curControlHandler != null)
                                {
                                    bContinueFlag = m_curControlHandler.getContinueFlag();
                                }
                                if (bContinueFlag == true)
                                {
                                      // reset the mark to begining
                                      reader.reset();
                                      // Don't mark it anymore for every new line
                                      m_bMarkAtEveryNewLineRead = false;
                                      // break out of this loop so that you can read
                                      // the new line.
                                      break;
                                }
                                else    // no, don't continue
                                {
                                       bIgnoreTags = false;
                                        // let the mark go
                                        m_bMarkAtEveryNewLineRead = true;
                                        // reset the turn
                                        m_turn = 0;
                                        // you are in the open again
                                        m_parserState = STATE_OUTSIDE_LOOP;
                                        // rest the state to be out of loop
                                        // continue to process the rest of the line
                                        // At every read line you need to mark it
                                }
                        }
                        // move past this tag
                        beginIndex = curRLFTag.getTagEndIndex();
               }// end of second while for tokens
               if (bPrintTheRestOfTheLine)
               {
                        writeToStream(line.substring(beginIndex) + "\n");
               }
               if (m_bMarkAtEveryNewLineRead == true)
               {
                        reader.mark(FILE_IO_MARK_READ_AHEAD_LIMIT);
               }
      } // first while for lines
      reader.close();   
      m_formHandler.formProcessingComplete();   
  }// function_end

  private RLFTag1 findSpecificRLFTag(String inString, int bgnIndex, int endIndex)
  {
      java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(
                                                inString.substring(bgnIndex,endIndex));
      // discard the first RLF token
      tokenizer.nextToken();
      return new RLFTag1(tokenizer.nextToken(), bgnIndex, endIndex, tokenizer.nextToken());
  }
  private void markReaderAtTheBeginingOfTheLoop(BufferedReader reader, int tagBeginIndex)
                        throws java.io.IOException
  {
      reader.reset();
      reader.skip(tagBeginIndex);
      reader.mark(FILE_IO_MARK_READ_AHEAD_LIMIT);
      m_bMarkAtEveryNewLineRead = false;
  }
  private RLFTag1 nextTag(String line, int beginingIndex)
  {
   // needs to return the starting index of the string if found
   // should return -1 if not found
        int RLFTagStartIndex = line.indexOf(TAGS_RLF_TAG,beginingIndex);
        if (RLFTagStartIndex == -1 )
        {
            return null;
        }
        System.out.println("Found a tag");        
        int RLFTagEndIndex = line.indexOf( '>',RLFTagStartIndex) + 1;
        return findSpecificRLFTag(line
                                 ,RLFTagStartIndex
                                 ,RLFTagEndIndex);
  }
  private void writeToStream(String inString)
  {
        if (m_bReplacementInProgress == false)
        {
//                System.out.print(inString);
                m_printWriter.print(inString);
        }
  }
  private void reportNoControlHandlerFound()
  {
      m_printWriter.print("<emphasis>No data found </emphasis>");
  }
  private void reportNoDataFound()
  {
      m_printWriter.print("<emphasis>No data found </emphasis>");
  }
} 

class RLFTag1
{
        String m_defaultAttributeValue;
        String m_tagName;
        int m_startIndex = 0;
        int m_endIndex = 0;
        
        RLFTag1(String tag, int startIndex, int endIndex)
        {
                this(tag,startIndex, endIndex, "");
        }
        RLFTag1(String tag, int startIndex, int endIndex, String defaultAttributeValue)
        {
                m_startIndex = startIndex;
                m_endIndex = endIndex;
                m_tagName = tag;
                m_defaultAttributeValue = defaultAttributeValue;
        }
        String getTagName()
        {
                return m_tagName;
        }
        String getDefaultAttributeValue()
        {
                return m_defaultAttributeValue;
        }
        void setAttributeValuePair(String attributeName, String attributeValue)
        {
        }                
        String getAttributeValue(String attributeName)
        {
                return "";
        }
        int getStartIndex()
        {
               return m_startIndex;
        }
        int getTagEndIndex()
        {
               return m_endIndex;
        }
}
