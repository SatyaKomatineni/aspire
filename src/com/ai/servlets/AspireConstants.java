/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

/**
 * 12/4/15: added
 * ****************
 * SUB_SQLARG_SUBSTITUTOR 
 * SUB_SQLARG_SUBSTITUTOR_MAX_INT_LENGTH
 * 
 */
public interface AspireConstants
{
//***************************************************************************************
//* Global Contexts
//***************************************************************************************
   public static String NAMING_CONTEXT          = "aspire"; // <----
   public static String SUBSTITUTIONS_CONTEXT   = NAMING_CONTEXT + "." + "substitutions";
   public static String PER_REQUEST_CONTEXT     = NAMING_CONTEXT + "." + "perRequest";
   public static String DEFAULT_OBJECTS_CONTEXT = NAMING_CONTEXT + "." + "defaultObjects";

   public static String PER_REQUEST_PRINT_WRITER = PER_REQUEST_CONTEXT + "." + "PrintWriter";


   public static String MULTIPART_REQUEST_OBJ_NAME = "aspire.MultipartRequestObjName";
   public static String STAGING_DIRECTORY_NAME = "aspire.StagingDirectory";
   public static String CREATED_PARMS_HEADER = "aspire.CreatedParameters";


//***************************************************************************************
//* Based on the main naming context: aspire
//***************************************************************************************
   public static String TREE_ROOT_PARAMETER = NAMING_CONTEXT + ".tree_root_parameter";

   public static String BOOL_TRUE = NAMING_CONTEXT + ".true";
   public static String BOOL_FALSE = NAMING_CONTEXT + ".false";

   public static String JDBC_CONNECTION_PARAM_KEY = NAMING_CONTEXT + ".reserved.jdbc_connection";
   public static String CONNECTION_OWNERSHIP_TRANSFER_FLAG_PARAM_KEY = NAMING_CONTEXT + ".reserved.transfer_connection_ownership";

   public static String LOGIN_PAGE_VALIDATION_REQUEST = NAMING_CONTEXT + ".validateLoginPage";
   public static String LOGIN_PAGE_URLS = NAMING_CONTEXT + ".loginPageURLs";
   public static String LOGIN_PAGE_PARAM_STRING = NAMING_CONTEXT + ".loginPageParamString";


//***************************************************************************************
//* AUTHENTICATION_CONTEXT: AC : aspire.authentication
//***************************************************************************************
   public static String AUTHENTICATION_CONTEXT = NAMING_CONTEXT + ".authentication";   // <----

   public static String AUTHENTICATION_OBJECT            = AUTHENTICATION_CONTEXT + ".authenticationObject";
   public static String AUTHENTICATE_USER                = AUTHENTICATION_CONTEXT + ".authenticateUser";
   public static String RESOURCE_EXTRACTION_OBJECT       = AUTHENTICATION_CONTEXT + ".resourceExtractionObject";
   public static String ACCESS_DENIED_PAGE               = AUTHENTICATION_CONTEXT + ".accessDeniedURL";
   public static String VERIFY_ACCESS                    = AUTHENTICATION_CONTEXT + ".verifyPageAccess";
   public static String USER_AUTHORIZATION               = AUTHENTICATION_CONTEXT + ".userAuthorization";
   public static String PASSWORD                         = AUTHENTICATION_CONTEXT + ".password";
   public static String REALM                            = AUTHENTICATION_CONTEXT + ".realm";
   public static String ANNONYMOUS_USER                  = AUTHENTICATION_CONTEXT + ".annonymous";
   public static String SESSION_INITIALIZER              = AUTHENTICATION_CONTEXT + ".sessionInitializer";
   public static String ASPIRE_USER_NAME_KEY             = "profile_user";
   public static String ASPIRE_ANNONYMOUS_USER_NAME      = "annonymous";
   public static String ASPIRE_LOGGEDIN_STATUS_KEY       = "profile_aspire_loggedin_status";
   public static String ASPIRE_HTTP_REQUEST_KEY          = "aspire_request";
   public static String ASPIRE_HTTP_RESPONSE_KEY         = "aspire_response";
   public static String ASPIRE_HTTP_SESSION_KEY          = "aspire_session";
   
   public static String AC_GET_PASSWORD_REQUEST          	= AUTHENTICATION_CONTEXT + ".getPasswordRequest";
   public static String AC_PERSISTENT_LOGIN_SUPPORT_OBJECT	= AUTHENTICATION_CONTEXT + ".persistentSupportLoginObject";
   public static String AC_PLS_DATA_PERSISTENCE_OBJECT	= AUTHENTICATION_CONTEXT + ".plsDataPersistenceObject";
   public static String AC_PLS_DATA_PERSISTENCE_FILE_NAME	= AUTHENTICATION_CONTEXT + ".plsDataPersistenceFilename";

//***************************************************************************************
//* SESSION_CONTEXT
//***************************************************************************************
   public static String SESSION_CONTEXT                  = NAMING_CONTEXT + "." + "sessionSupport";  // <----
   public static String SESSION_SUPPORT_OBJECT           = SESSION_CONTEXT + ".sessionSupportObject";
   public static String SESSION_SUPPORT_MAIN_PAGE        = SESSION_CONTEXT + ".mainPage";
   public static String APPLY_SESSION_MANAGEMENT         = SESSION_CONTEXT + ".applySessionManagement";
   public static String SESSION_CREATE_AUTHORITY         = SESSION_CONTEXT + ".sessionCreateAuthority";
   public static String SESSION_SUPPORT_NEW_USER_SESSION_LOADER = SESSION_CONTEXT + ".newUserSessionLoader";


//***************************************************************************************
//* SERVLET_CONTEXT: SC
//***************************************************************************************
   public static String SERVLET_CONTEXT                  = NAMING_CONTEXT + "." + "servletSupport";  // <----
   public static String PAGE_REDIRECTOR                  = SERVLET_CONTEXT + "." + "pageRedirector";
   public static String SERVER_SIDE_REDIRECTOR           = SERVLET_CONTEXT + "." + "serverSideRedirector";
   public static String GENERIC_REDIRECTOR           	 = SERVLET_CONTEXT + "." + "genericRedirector";
   public static String REQUEST_OVERRIDE_DICTIONARY      = SERVLET_CONTEXT + "." + "overrideDictionary";
   public static String RESPONSE_HEADERS                 = SERVLET_CONTEXT + "." + "responseHeaders";
   public static String RESPONSE_HEADERS_CLASS           = SERVLET_CONTEXT + "." + "responseHeadersClass";
   public static String SC_WEB_APPLICATION_CONTEXT       = SERVLET_CONTEXT + "." + "webApplicationContext";
   public static int    SC_URL_TYPE_DISPLAY				 = 1;
   public static int 	SC_URL_TYPE_UPDATE				 = 2;

//***************************************************************************************
//* MASTER_PAGE_CONTEXT:MP
//***************************************************************************************
   public static String MP_MASTER_PAGE_CONTEXT                  = SERVLET_CONTEXT + ".masterPage";  // <----
   public static String MP_ENABLE_MASTER_PAGE = MP_MASTER_PAGE_CONTEXT + ".enable"; // (true,false:false)
   public static String MP_DEFAULT_TOP_HALF =  MP_MASTER_PAGE_CONTEXT + ".defaultTopHalfHTML";
   public static String MP_DEFAULT_BOTTOM_HALF =  MP_MASTER_PAGE_CONTEXT + ".defaultBottomHalfHTML";
   public static String MP_OVERRIDE_URL =  MP_MASTER_PAGE_CONTEXT + ".overRideUrl";// (true,fasle:false)
   public static String MP_GLOBAL_MASTER_PAGE_REQUEST_NAME =  MP_MASTER_PAGE_CONTEXT + ".globalMasterPageRequestName";// (true,fasle:false)

 //***************************************************************************************
 //* SUBSTITUTIONS_CONTEXT: SUB : aspire.substitutions
 //***************************************************************************************
   //default will be SQLArgSubstitutor class
   public static String SUB_SQLARG_SUBSTITUTOR = SUBSTITUTIONS_CONTEXT + ".sqlArgSubstitutor";
   //relative name
   public static String SUB_SQLARG_SUBSTITUTOR_MAX_INT_LENGTH = ".maxIntLength";
}
