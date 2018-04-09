/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.htmlgen;
import java.io.*;
import com.ai.application.utils.AppObjects;

import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;

public class HtmlParser implements IAITransform, ICreator, ISingleThreaded
{

   public static void performTransform( String htmlFilename
                                        ,PrintWriter writer
                                        , IFormHandler formHandler
                                        )
        throws java.io.IOException
      {
        HtmlParser parser = new HtmlParser();
        parser.transform(htmlFilename, writer, formHandler);
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

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
   
  public void transform(String htmlFilename, PrintWriter writer, IFormHandler formHandler)
        throws java.io.IOException
  {
  // begin with an outside loop state
      m_formHandler = formHandler;
      m_printWriter = writer;

      BufferedReader reader = new BufferedReader(new FileReader(htmlFilename));
      String line;
      boolean bPrintTheRestOfTheLine = true;
      while((line = reader.readLine()) != null)
      {
               int beginIndex = 0;
               int curTagIndex =0;
               while((curTagIndex = nextTag(line,beginIndex)) != -1)
               {
                        // write the stream so far up to the tag
                        writeToStream(line.substring(beginIndex,curTagIndex));

                        // Find out what kind of a tag it is
                        int RLFTagEndIndex = line.indexOf( '>',curTagIndex) + 1;
                        RLFTag specificRLFTag = findSpecificRLFTag(line
                                                 ,curTagIndex
                                                 ,RLFTagEndIndex);
                        if (specificRLFTag.getTagName().equals(TAGS_REPLACE_TAG))
                        {
                                // write replacement code here
                                // Do nothing for right now
                                if (m_parserState == STATE_INSIDE_LOOP)
                                {
                                        if (m_curControlHandler != null)
                                        {
                                                writeToStream(m_curControlHandler.getValue(
                                                        specificRLFTag.getDefaultAttributeValue()
                                                        ,m_turn));
                                        }
                                        else
                                        {
                                                writeToStream("Control handler not found");
                                        }
                                }
                                else
                                {
                                        writeToStream(m_formHandler.getValue(specificRLFTag.getDefaultAttributeValue()));
                                }
                                m_bReplacementInProgress = true;
                        }
                        else if (specificRLFTag.getTagName().equals(TAGS_REPLACE_END_TAG))
                        {
                                m_bReplacementInProgress = false;
                        }
                        else if (specificRLFTag.getTagName().equals(TAGS_BGN_LOOP_TAG))
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
                                                        specificRLFTag.getDefaultAttributeValue());
                                        }                                                        
                                        catch(ControlHandlerException x)
                                        {
                                                AppObjects.log(AppObjects.LOG_CRITICAL,x);
                                                m_curControlHandler = null;
                                        }

                                }
                        }
                        else if (specificRLFTag.getTagName().equals(TAGS_END_LOOP_TAG))
                        {
                                // end loop seen

                                // Ask control handler if you want to continue
                                // if continue
                                if (m_curControlHandler.getContinueFlag() == true)
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
                        beginIndex = RLFTagEndIndex;
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
  private RLFTag findSpecificRLFTag(String inString, int bgnIndex, int endIndex)
  {
      java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(
                                                inString.substring(bgnIndex,endIndex));
      // discard the first RLF token
      tokenizer.nextToken();
      return new RLFTag(tokenizer.nextToken(), tokenizer.nextToken());
  }
  private void markReaderAtTheBeginingOfTheLoop(BufferedReader reader, int tagBeginIndex)
                        throws java.io.IOException
  {
      reader.reset();
      reader.skip(tagBeginIndex);
      reader.mark(FILE_IO_MARK_READ_AHEAD_LIMIT);
      m_bMarkAtEveryNewLineRead = false;
  }
  private int nextTag(String line, int beginingIndex)
  {
        return line.indexOf(TAGS_RLF_TAG,beginingIndex);
  }
  private void writeToStream(String inString)
  {
        if (m_bReplacementInProgress == false)
        {
//                System.out.print(inString);
                m_printWriter.print(inString);
        }
  }
}

class RLFTag
{
        String m_defaultAttributeValue;
        String m_tagName;
        RLFTag(String tag)
        {
                this(tag,"");
        }
        RLFTag(String tag, String defaultAttributeValue)
        {
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
}
