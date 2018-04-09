/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.jawk;
import java.io.*;

public interface IFileProcessorListener
{
    public void beginOfFile(BufferedReader reader );
    public void newLine( final String line );
    public  void endOfFile();
    public String getRegExpressionStr();
}
