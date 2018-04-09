package com.ai.common;

// Property file entry
// reques.aspire.defaultObjects.ExceptionAnalyzer.className=
public interface IExceptionAnalyzer 
{
   static public String NAME = com.ai.servlets.AspireConstants.DEFAULT_OBJECTS_CONTEXT + ".ExceptionAnalyzer";
   // Return null if the exception message is null
   String getRootCauseCode(Throwable t);

   //If the pattern matches return true
   boolean doYouMatch(Throwable t, final String pattern);
} 