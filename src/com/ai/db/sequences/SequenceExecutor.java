/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db.sequences;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;
import com.ai.data.*;

/**
 *
 * Property file responsibilities
 *
 * request.MySeqGenerator.className=SequenceExecutor
 * request.MySeqGenerator.seqName=NewSequence
 *
 *
 */
public class SequenceExecutor implements ICreator 
{
        public Object executeRequest(String requestName, Object args)
                throws RequestExecutionException
        {
            try
            {

               IFactory factory = AppObjects.getIFactory();
            
               String seqName = AppObjects.getIConfig().getValue(requestName + ".seqName");
               ISequenceGenerator seqGen = 
                  (ISequenceGenerator)factory.getObject(ISequenceGenerator.NAME,null);
               String seqNumber = seqGen.getNextSequenceFor(seqName);

               Vector metaFields = new Vector();
               metaFields.add(seqName + "_value");
               Vector rowVector = new Vector();
               rowVector.add(seqNumber);

               return new VectorDataCollection(metaFields,rowVector);
            }
            catch (SequenceException x)
            {
               throw new RequestExecutionException("Error: Could not get a sequence number",x);
            }               
            catch(ConfigException x)
            {
               throw new RequestExecutionException("Error: Sequence name not found",x);
            }
            catch(com.ai.data.InvalidVectorDataCollection x)
            {
               throw new RequestExecutionException("Error: Invalid vector data collection",x);
            }
            
        }
} 