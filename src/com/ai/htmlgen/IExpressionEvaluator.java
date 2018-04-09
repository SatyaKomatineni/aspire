package com.ai.htmlgen;

import com.ai.common.*;
import java.io.*;

public interface IExpressionEvaluator
{
  public String evaluate(String expression, IDictionary args) throws TransformException;
  public void evaluate(String expression, IDictionary args, PrintWriter out)
    throws IOException, TransformException;
}