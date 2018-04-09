package com.ai.servlets;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.utils.AppObjects;
import com.ai.common.Tokenizer;

/**
 * 6/26/2017
 * **************
 * Retire DisplayNoteMPURL with DisplayNoteIMPURL
 * 
 * Use this structure. 
 * #retire the old URL
 * request.RenameURLHandler.classname=com.ai.servlets.RenameURLHandler
 * request.RenameURLHandler.urlNames=DisplayNoteMPURL
 * request.RenameURLHandler.key.DisplayNoteMPURL=DisplayNoteIMPURL
 * 
 * Use aspire_replace_urls=no if you want to force older behavior
 * 
 * Multiplicity
 * ****************
 * This is a singleton
 */
public class RenameURLHandler extends DefaultHttpEvents implements IInitializable
{
  
   //Map<OldUrlName, newUrlName>
	//Use this later
   private Map<String,String> renameURLMap = new HashMap<String,String>();
   private static final String DONT_REPLACE_URLS_KEY = "aspire_replace_urls";
   private static final String NO = "no";
   
   public void initialize(String requestName)
   {
	  try 
	  {
	      //mandatory argument
	      String oldUrlMapNames = AppObjects.getValue(requestName + ".urlNames");
	      List<String> oldUrlMapNameList = Tokenizer.tokenizeAsList(oldUrlMapNames, ",");
	      for (String oldUrlname:oldUrlMapNameList)
	      {
	    	  String newUrlName = AppObjects.getValue(requestName + ".key." + oldUrlname);
	    	  AppObjects.info(this, "Replacing url: %1s with new url: %2s",oldUrlname, newUrlName);
	    	  renameURLMap.put(oldUrlname.toLowerCase(), newUrlName);
	      }
	      AppObjects.info(this,"Initialzied RenameURLHandler");
	  }
	  catch(ConfigException x)
	  {
		  AppObjects.info(this,"could not read urlnames for request %s", requestName);
		  throw new RuntimeException("Could not read configuration for rename url maps",x);
	  }
   }
   public boolean beginAspireRequest(String coreuser, HttpSession session,String uri,String query, Hashtable parameters
           ,PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws AspireServletException
   {
	      AppObjects.info(this,"Begin aspire request with proper args");
	      AspireURL aspireUrl = AspireURL.createAppropriateAspireURL(request);
	      if (!(aspireUrl instanceof AspireDisplayURL))
	      {
	    	  //it is not a display URL
	    	  return true;
	      }
	      
	      //it is a display url. See if replacement is in force
	      String replaceOrNot = (String)parameters.get(DONT_REPLACE_URLS_KEY);
	      if (replaceOrNot != null) return true;
	      //if (replaceOrNot.equalsIgnoreCase(NO)) return true;
	      
	      //Replacement is in effect
	      String displayUrl = ((AspireDisplayURL)aspireUrl).getName();
	      String newDisplayUrl = renameURLMap.get(displayUrl.toLowerCase());
	      
	      if (newDisplayUrl != null)
	      {
	    	  AppObjects.info(this, "Replacing the old url with %s", newDisplayUrl);
	    	  parameters.remove(AspireURL.URL_QUALIFIER_DISPLAY);
	    	  parameters.put(AspireURL.URL_QUALIFIER_DISPLAY, newDisplayUrl);
	      }
	      return true;
   }
}//eof-class
