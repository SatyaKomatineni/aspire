package com.ai.htmlgen;
import com.ai.application.utils.*;

public class ExpressionEvaluatorFactory
{
  public static IExpressionEvaluator getSelf()
    throws com.ai.application.interfaces.RequestExecutionException
  {
    return (IExpressionEvaluator)AppObjects.getObjectAbsolute("request.aspire.expressionevaluator",null);
  }
}