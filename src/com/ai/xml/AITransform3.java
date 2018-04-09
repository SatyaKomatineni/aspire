/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.xml;

import java.io.*;
import com.ai.application.utils.AppObjects;
import com.ai.htmlgen.IFormHandler;
import com.ai.htmlgen.IControlHandler;
import com.ai.htmlgen.ControlHandlerException;
import java.util.StringTokenizer;
import com.ai.htmlgen.IAITransform;
import com.ai.application.interfaces.ISingleThreaded;
import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.htmlgen.*;

/**
 * An attempt to separate parsing from evaluation
 * The evaluations will be passed as inputs
 * IFormHandler is being passed in as input
 * In this case the IFEvaluator will be passed in as input as well
 */
public class AITransform3 implements IAITransform, ICreator, ISingleThreaded
{

   public static void processHtmlPage( String htmlFilename
                                        ,PrintWriter writer
                                        , IFormHandler formHandler
                                        )
        throws java.io.IOException
      {
        AITransform3 parser = new AITransform3();
        parser.transform(htmlFilename, writer, formHandler);
   }

   static final int STATE_LOOP_BEGIN_ENCOUNTERED = 2;
   static final int STATE_INSIDE_LOOP = 1;
   static final  int STATE_OUTSIDE_LOOP = 3;
   static final  int STATE_IGNORE_TAGS = 4;

   // required for marking a stream to perform loops
   static final int FILE_IO_MARK_READ_AHEAD_LIMIT = 4096;

   static final String TAGS_RLF_TAG = "<!--RLF_TAG";
   static final int TAGS_RLF_TAG_LENGTH = TAGS_RLF_TAG.length();
   static final String TAGS_REPLACE_TAG = "REPLACE_BGN";
   static final String TAGS_REPLACE_END_TAG = "REPLACE_END";
   static final String TAGS_END_LOOP_TAG = "END_LOOP";
   static final String TAGS_BGN_LOOP_TAG = "BGN_LOOP";
   static final String TAGS_BGN_IF_TAG = "BGN_IF";
   static final String TAGS_END_IF_TAG = "END_IF";

   // object level variables
   IFormHandler         m_formHandler;
   int                  m_turn = 1;
   IControlHandler      m_curControlHandler;
   int                  m_parserState = STATE_OUTSIDE_LOOP;
   boolean              m_bMarkAtEveryNewLineRead = true;
   boolean              m_bReplacementInProgress = false;
   PrintWriter          m_printWriter;
      // ignore tags required for eliminating sections of the page
      // if tags use it. Loop tags use it.
   boolean m_bIgnoreTags = false;
   String  m_ignoreTagsOwner = null;

   private IBooleanExpressionEvaluator m_booleanExprEvaluator = null;

   public AITransform3(IBooleanExpressionEvaluator evaluator)
   {
      if (evaluator != null)
      {
         m_booleanExprEvaluator = evaluator;
      }
      else
      {
         m_booleanExprEvaluator = getDefaultBooleanExpressionEvaluator();
      }
   }
   public AITransform3()
   {
      this(null);
   }
   private IBooleanExpressionEvaluator getDefaultBooleanExpressionEvaluator()
   {
      try
      {
         return
         (IBooleanExpressionEvaluator)
         AppObjects.getIFactory().getObject(IBooleanExpressionEvaluator.NAME,null);
      }
      catch(RequestExecutionException x)
      {
         AppObjects.log("Error: No boolean expression evaluator configured",x);
         return null;
      }
   }
   // Functions

   void setIgnoreTags(RLFTag1 ownerTag)
   {
      AppObjects.log("Info: Ignoring tags for " + ownerTag.getGivenName());
      m_bIgnoreTags = true;
      m_ignoreTagsOwner = ownerTag.getGivenName();
   }
   void resetIgnoreTags(RLFTag1 ownerTag)
   {
      AppObjects.log("Info: Minding tags for " + ownerTag.getGivenName());
      m_bIgnoreTags = false;
      m_ignoreTagsOwner = null;
   }
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }

  public void transform(String htmlFilename, PrintWriter writer, IFormHandler formHandler)
        throws java.io.IOException
  {
      AppObjects.log("aitransform: Inside the parser");
  // begin with an outside loop state
      m_formHandler = formHandler;
      m_printWriter = writer;

      BufferedReader reader = new BufferedReader(new FileReader(htmlFilename));
      String line;
      boolean bPrintTheRestOfTheLine = true;


      reader.mark(FILE_IO_MARK_READ_AHEAD_LIMIT);
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

                        if(m_bIgnoreTags == true )
                        {
                            // SPECIAL CASE where the control handler fetched no data
                            // Only look for the end tag
                            if (curRLFTag.getGivenName().equals(m_ignoreTagsOwner))
                            {
                                 beginIndex = curRLFTag.getTagEndIndex();
//                                 bIgnoreTags = false;
                                 resetIgnoreTags(curRLFTag);
                            }
                            else
                            {
//                                 continue;
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
                        AppObjects.log("Debug: Processing tag " + curRLFTag.getTagName() + ":" +  curRLFTag.getDefaultAttributeValue() );
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
                                if (curRLFTag.getShortTag() == false)
                                {
                                   m_bReplacementInProgress = true;
                                }
                        }
                        else if (curRLFTag.getTagName().equals(TAGS_REPLACE_END_TAG))
                        {
                                m_bReplacementInProgress = false;
                        }
                        else if (curRLFTag.getTagName().equals(TAGS_BGN_LOOP_TAG))
                        {
                                // if this is by itself on a line
                                if (line.length() >= curRLFTag.getTagEndIndex())
                                {
                                 bPrintTheRestOfTheLine = false;
                                }
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
                                        markReaderAtTheBeginingOfTheLoop(reader,curRLFTag.getTagEndIndex());
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
                                                      setIgnoreTags(curRLFTag);
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
//****************************
//********tags_end_loop_tag
//****************************
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
                                      bPrintTheRestOfTheLine = false;
//                                      writeToStream("\n");
                                      // break out of this loop so that you can read
                                      // the new line.
                                      m_turn++;
                                      break;
                                }
                                else    // no, don't continue
                                {
//                                       bIgnoreTags = false;
                                       resetIgnoreTags(curRLFTag);
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
//****************************
//********tags_bgn_if_tag
//****************************
                        else if (curRLFTag.getTagName().equals(TAGS_BGN_IF_TAG))
                        {
                           // get the key/value pair
                           String conditionKeyValue = curRLFTag.getDefaultAttributeValue();
                           AppObjects.log("info.htmlgen: condition" + conditionKeyValue );

                           boolean bCondition = true;
                           // if it has a function in it
                           if (conditionKeyValue.indexOf('(') != -1)
                           {
                              // this is a function
                              IBooleanExpressionEvaluator eval = m_booleanExprEvaluator;
                              if (m_parserState == STATE_INSIDE_LOOP)
                              {
                                 bCondition = eval.evaluate(conditionKeyValue,
                                                   formHandler,
                                                   m_curControlHandler,
                                                   m_turn);
                              }
                              else
                              {
                                 bCondition = eval.evaluate(conditionKeyValue
                                                      ,formHandler
                                                      ,null
                                                      ,0);
                              }

                           }
                           else
                           {
                              // break up into given key/value
                              StringTokenizer tknizr = new StringTokenizer(conditionKeyValue,"=");
                              String givenKey = tknizr.nextToken();
                              String givenValue = tknizr.nextToken();

                              // Get the condition value and matchit with the given value
                              // ignore the section if they don't match
                              // otherwise include the section if they match
                              String conditionValue="";
                              if (m_parserState == STATE_INSIDE_LOOP)
                              {
                                 if (m_curControlHandler != null)
                                 {
                                    conditionValue = m_curControlHandler.getValue(givenKey,m_turn);
                                 }
                              }
                              else
                              {
                                 conditionValue = formHandler.getValue(givenKey);
                              }
                              AppObjects.log("info.htmlgen: conditionValue " + conditionValue );
                              AppObjects.log("info.htmlgen: givenValue " + givenValue + "1" );
                              bCondition = givenValue.equals(conditionValue);
                           }



                           if (!bCondition)
                           {
                              // set the state to ignore input
                              setIgnoreTags(curRLFTag);
                           }
                        }
//****************************
//********tags_end_if_tag
//****************************
                        else if (curRLFTag.getTagName().equals(TAGS_END_IF_TAG))
                        {
                           // nothing to do
                        }
//****************************
//********default case processing
//****************************
                        else // default case
                        {
                           // nothing to do
                        }
//****************************
//********end of tag processing
//****************************
                        // move past this tag
                        beginIndex = curRLFTag.getTagEndIndex();
               }// end of second while for tokens
               if (bPrintTheRestOfTheLine)
               {
                  writeToStream(line.substring(beginIndex) + "\n");
               }
               else
               {
                  bPrintTheRestOfTheLine = true;
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
      String tagName = tokenizer.nextToken();
      String tagValue = tokenizer.nextToken();
      String givenName = null;
      if (tokenizer.hasMoreTokens())
      {
         givenName = tokenizer.nextToken();
      }
      if (givenName.equals("-->"))
      {
         givenName = null;
      }
      return new RLFTag1(tagName, bgnIndex, endIndex, tagValue, givenName);
  }
  private void markReaderAtTheBeginingOfTheLoop(BufferedReader reader, int tagEndIndex)
                        throws java.io.IOException
  {
      reader.reset();
      reader.skip(tagEndIndex);
      reader.mark(FILE_IO_MARK_READ_AHEAD_LIMIT);
      m_bMarkAtEveryNewLineRead = false;
  }
  private RLFTag1 nextTag(String line, int beginingIndex)
  {
   // needs to return the starting index of the string if found
   // should return -1 if not found
        boolean bSimpleTag = false;
        int RLFTagStartIndex = line.indexOf(TAGS_RLF_TAG,beginingIndex);
        if (RLFTagStartIndex == -1 )
        {
            RLFTagStartIndex = line.indexOf("{{",beginingIndex);
            if (RLFTagStartIndex == -1)  return null;
            bSimpleTag = true;
        }
        if (bSimpleTag == false)
        {
//           AppObjects.log("aitransform: Found a tag");
           int RLFTagEndIndex = line.indexOf( '>',RLFTagStartIndex) + 1;
           return findSpecificRLFTag(line
                                    ,RLFTagStartIndex
                                    ,RLFTagEndIndex);
         }
         else
         {
            // found a simple tag "{" which always is a replace tag
//            AppObjects.log("aitransform: Found a simple tag");
            int RLFTagEndIndex = line.indexOf("}}",RLFTagStartIndex) + 2;
            String tagValue = line.substring(RLFTagStartIndex+2,RLFTagEndIndex-2);
            RLFTag1 tag = new RLFTag1(TAGS_REPLACE_TAG,RLFTagStartIndex, RLFTagEndIndex, tagValue,null);
            tag.setShortTag(true);
            return tag;
         }
  }
  private void writeToStream(String inString)
  {
        if (m_bIgnoreTags == true)
        {
            return;
        }
        if (m_bReplacementInProgress == true)
        {
            return;
        }
//      System.out.print(inString);
        m_printWriter.print(inString);
  }
  private void reportNoControlHandlerFound()
  {
      m_printWriter.print("<emphasis>No data found </emphasis>");
  }
  private void reportNoDataFound()
  {
//      m_printWriter.print("<emphasis>No data found </emphasis>");
   AppObjects.log("Warn:No data found for a loop. Eliminating rows");
  }
class RLFTag1
{
        String m_defaultAttributeValue;
        String m_tagName;
        String m_givenName = null;
        int m_startIndex = 0;
        int m_endIndex = 0;

        boolean m_bShortTag = false;

        public void setShortTag(boolean bShortTag )
        {
         m_bShortTag = bShortTag;
        }
        public boolean getShortTag()
        {
         return m_bShortTag;
        }

        RLFTag1(String tag, int startIndex, int endIndex)
        {
                this(tag,startIndex, endIndex, "",null);
        }
        RLFTag1(String tag, int startIndex, int endIndex
            , String defaultAttributeValue, String givenName)
        {
                m_startIndex = startIndex;
                m_endIndex = endIndex;
                m_tagName = tag;
                m_defaultAttributeValue = defaultAttributeValue;
                setGivenName(givenName);
        }
        void setGivenName(String givenName)
        {
         if (givenName == null)
         {
            m_givenName = null;
         }
         else
         {
            m_givenName = givenName.toLowerCase();
         }
        }
        String getTagName()
        {
                return m_tagName;
        }
        String getGivenName()
        {
               if (m_givenName != null)
               {
                  return m_givenName;
               }
               // givenname is null
                if (getTagName().equals(TAGS_BGN_LOOP_TAG)
                     || getTagName().equals(TAGS_END_LOOP_TAG) )
                {
                  return getDefaultAttributeValue();
                }
                // non loop tag and null case
                return "aspire.annonymous";
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
//******************************************************************
//* End of a private class
//******************************************************************
}

