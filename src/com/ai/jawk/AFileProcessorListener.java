/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.jawk;
import java.io.*;

public abstract class  AFileProcessorListener implements IFileProcessorListener
{
    public void beginOfFile(BufferedReader reader )
    {
    }
    public String getRegExpressionStr()
    {
        return null;
    }
    public void endOfFile()
    {
    }
}

