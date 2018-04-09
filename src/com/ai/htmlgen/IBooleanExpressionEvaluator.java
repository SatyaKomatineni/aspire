package com.ai.htmlgen;

public interface IBooleanExpressionEvaluator 
{
   static public String NAME = "Aspire.BooleanExpressionEvaluator";
   boolean evaluate(final String expression
           ,IFormHandler pageData
           ,IControlHandler loopData
           ,int curTurn );
} 
