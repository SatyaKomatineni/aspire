/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.io.PrintWriter;

public interface IAITransform
{
   public static final String GET_TRANSFORM_OBJECT = "AppObjects.transform";
   /**
    * @Deprecated. use the IAIResourceTransform
    */
//   public void transformOld( String htmlFilename
   public void transform( String htmlFilename
                          ,PrintWriter writer
                          , IFormHandler formHandler
                         ) throws java.io.IOException;
}
