package com.ai.common;

import java.util.Hashtable;
import java.util.Map;

import com.ai.application.utils.AppObjects;
import com.ai.servlets.AspireConstants;

/**
 *
 * 12/4/15
 * ******************
 * Added sqlSubstitute as a global static function
 * Also procures a single SQLArgSubstitutor based on config file.
 * 
 * 11/15/2015
 * ***********************
 * Change how this class gets the SQLArgSubstitutor.
 * Use a strategy swap approach to get a SQLArgSubstitutor at run time
 * Allows backward compatibility while allowing future variations
 * Set the following in the config file
 * 
 * request.aspire.substitutions.sqlArgSubstitutor.className=\
 * com.ai.common.SQLArgSubstitutor2WithArgValidation
 * 
 * Once tested replace the default with 
 * com.ai.common.SQLArgSubstitutor1
 * 
 * The above is default class. A future class can be used.
 * @see SQLArgSubstitutor
 * @see SQLArgSubstitutor1
 * @see SQLArgSubstitutor2Updated
 * @see SQLArgSubstitutor2WithArgValidation
 *
 */
public class SubstitutorUtils
{
  private static GeneralArgSubstitutor gas = new GeneralArgSubstitutor();
  private static EncodeArgSubstitutor eas = new EncodeArgSubstitutor();

  // deprecated
  public static String generalSubstitute(String inString, Map args)
  {
    return SubstitutorUtils.gas.substitute(inString, new Hashtable(args));
  }

  // deprecated
  public static String urlencodeSubstitute(String inString, Map args)
  {
    return SubstitutorUtils.eas.substitute(inString, new Hashtable(args));
  }

  public static String generalSubstitute(String inString, IDictionary args)
  {
    return SubstitutorUtils.gas.substitute(inString, args);
  }

  public static String urlencodeSubstitute(String inString, IDictionary args)
  {
    return SubstitutorUtils.eas.substitute(inString, args);
  }
  
  public static String sqlSubstitute(String inString, IDictionary args)
  {
	  //AppObjects.info("SubstitutorUtils", "Before substitution: %s", inString);
	  //This is logged by the substitutor itself
	  AArgSubstitutor s = getSQLArgSubstitutor();
	  String rtnstring = null;
	  if (s instanceof ISQLArgSubstitutorOptimizedTag)
	  {
		  //This is an optimized substitutor
		  rtnstring = s.substitute( inString, args);
	  }
	  else
	  {
		  //This is an older unoptimized substitutor
		  AppObjects.warn("SubstitutorUtils", "You are using an older class: %s", s.getClass().getName());
		  rtnstring = s.substitute( inString, Utils.convertDictionary(args));
	  }
	  //This is logged by the substitutor itself
	  //AppObjects.info("SubstitutorUtils", "After substitution: %s", rtnstring);
	  return rtnstring;
  }
  /*
   * ***********************************
   * SQLArgSubstitutor Support
   * ***********************************
   */
  public static void main(String[] args)
  {
     com.ai.application.defaultpkg.ApplicationHolder.initApplication(args[0],args);
     Hashtable t = new Hashtable();
     t.put("foldername","Humanities current");
     String out = SubstitutorUtils.sqlSubstitute("f.folder_name={folderName.quote}", new MapDictionary(t));
     System.out.println(out);
  }                                   
  
  private static AArgSubstitutor sqlArgSubstitutor =  null;
  static 
  {
	   //Get it from the factory if available
	  sqlArgSubstitutor = (AArgSubstitutor)AppObjects.getObject(AspireConstants.SUB_SQLARG_SUBSTITUTOR,null,null);
	   if (sqlArgSubstitutor == null)
	   {
		   //It is not specified. Use a default
		   AppObjects.info("SubstitutorUtils", "Using default SQLArgSubstitutor %s", SQLArgSubstitutor.class.getName());
		   sqlArgSubstitutor =  new SQLArgSubstitutor();
	   }
	   else
	   {
		   //Valid argsubstitutor from config file
		   AppObjects.info("SubstitutorUtils", "Using SQLArgSubstitutor from config file: %s", 
				   sqlArgSubstitutor.getClass().getName());
	   }
  }
  public static AArgSubstitutor getSQLArgSubstitutor()
  {
	  return sqlArgSubstitutor;
  }
}//eof-class