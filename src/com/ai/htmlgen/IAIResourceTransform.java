/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.io.PrintWriter;
import com.ai.common.TransformException;
/**
 * Use this instead of IAITransform
 * Introduced for strategizing resource reads such as files and urls
 */
public interface IAIResourceTransform
{
   public void transformUsingResource( String resourcename
                          ,PrintWriter writer
                          , IFormHandler formHandler
                         ) throws TransformException, java.io.IOException;
}
