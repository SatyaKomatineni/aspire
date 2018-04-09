package com.ai.servlets;

import com.ai.application.utils.*;
import com.ai.application.interfaces.*;
import com.ai.resourcecleanup.SWIResourceCleanup;

import javax.servlet.http.*;

import java.util.*;

public class ResourceCleanupHandler extends DefaultHttpEvents
{
	   public boolean endRequest(HttpServletRequest request, HttpServletResponse response) throws AspireServletException
	   {
		  try
		  {
		      AppObjects.info(this,"Request end event. Cleaning up resources if needed.");
		      SWIResourceCleanup.cleanup();
		      return true;
		  }
		  catch(Throwable t)
		  {
			  AppObjects.log("Error: exception in finally clause of base servlet through endRequest event",t);
			  return true;
		  }
	   }
}//eof-class