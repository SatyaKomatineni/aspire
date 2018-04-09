/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.data;
import java.util.*;
/**
 * To supply a data collection that is empty
 * In otherwords whereever you need an empty collection construct and pass this class
 */
public class EmptyDataCollection implements IDataCollection1 {

        public  IMetaData getIMetaData()
               throws DataException
       {
         return new VectorMetaData( new Vector() );
       }

        public IIterator getIIterator()
               throws DataException
               {
                  return new VectorIterator(new Vector());
               }
        public void closeCollection()
            throws DataException{}

        public IIterator getDataRowIterator()
            throws DataException
            {
               return new VectorIterator(new Vector() );
            }
            
   public EmptyDataCollection() 
   {
   }
} 