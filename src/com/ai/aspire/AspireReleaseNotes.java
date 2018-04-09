/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire;
/**
 *
 * 
 * Release 2.0 Build 45: June 24th, 2017
 * ***************************************
 * Added URL renaming for migrating DisplayNoteMPURL
 * See RenameURLHandler (an HttpEventHandler)
 * 
 * Release 2.0 Build 44: June 24th, 2017
 * ***************************************
 * Added path to the PLS cookie
 * Removed the browser cookie when needed
 * This is to fix the login in-persistence based on URLs
 * 
 * Release 2.0 Build 43: June 19th, 2017
 * ***************************************
 * Consolidate persistent login support
 * Avoid relogin every time server has restarted
 * key url: http://satyakomatineni.com/item/5197
 * Title: Expand persistent logins
 * 
 * Key package this is implemented in:
 * com.ai.aspire.authentication.pls
 * 
 * 
 * Release 2.0 Build 42: July 7th, 2016
 * ***************************************
 * Updated FileWriter capability with a new class
 * Introduced DataObject, SimpleDataObject, DynamicDataObject
 * Enhanced TestCollection support
 * com.ai.common.console package introduced
 *  
 * Release 2.0 Build 41: December 16th, 2015
 * ***************************************
 * Better logging in GlobalExceptionHandler to log url
 * Better logging for context in SQLArgSubstitutor2WithArgValidation
 *  
 * Release 2.0 Build 40: December 4th, 2015
 * ***************************************
 * To fix initializers. See comments in the source file
 * SQL injection addressed
 * 
 * SQLArgSubstitutor2Updated.java
 * SQLArgSubstitutor2WithArgValidation.java 
 * OracleQuoteTranslatorStrict.java
 * Key Property files to be read from web-inf
 * 
 * Release 2.0 Build 39: June 14th, 2014
 * ***************************************
 * specific support for sat-scoring-app as an exmaple
 * security and other constraints added
 * event handling for sub users
 * sub users introduced
 * Fix for if conditions under loops
 *     Take a look at SimpleBEEvaluator2
 * Tried to deprecate a few things but that will be a major effort
 * Typefaces integrated
 * Added the ability to insert multiple rows from a web page 
 * Added the necessary part:
 * DBLoopMultiFieldRequestExecutorPart
 * See the summary of changes at: satyakomatineni.com/item/4846
 * 
 * 
 * Release 2.0 Build 38: April 14th, 2014
 * ************************************
 * Added public upload test part
 * 
 * Release 2.0 Build 37: May 28th, 2013
 * ************************************
 * Urgent fix for security hole
 * 
 * Release 2.0 Build 36: May 28th, 2013
 * ************************************
 * Introduced http digest method
 * Introduced Persistent Login Support
 * See http://satyakomatineni.com/item/4565 for more details
 * 
 * Release 2.0 Build 35: Dec 12th, 2012
 * ************************************
 * Fixed a bug with PipelineReader
 * See the pipelinereader comments to see changes
 * StringToInputStreamPart changed to ignore case
 * 
 * Release 2.0 Build 34: July 15, 2012
 * ************************************
 * Introduced SQLInStringsClauseTranslator
 * 
 * Release 2.0 Build 33: July 15, 2012
 * ************************************
 * Introduced SQLInIdClauseTranslator
 * 
 * Release 2.0 Build 32: Apr 15, 2012
 * ************************************
 * ai.akc.akcList package started
 * custom object implementation
 * Utils.massert introduced and utilized
 * test1 4/15
 * list implementation release
 * completed 5/12/2012
 * 
 * Release 2.0 Build 31: Feb 28th 2012
 * ************************************
 * Change some more log calls.
 * log set 1: 2/29
 * log set 2 (filters): 3/1
 * log set 3 (htmlgen): 3/1
 * log set 4 (servlets): 3/2
 * log set 5 (trace): 3/2
 * Log set 6 (info): 3/2
 * Wrap it up and repackaged: 4/05
 * 
 * Release 2.0 Build 30: Feb 18th 2012
 * ************************************
 * Introduced new log methods for efficiency
 * Introduced CollectionWorkSplitterPart
 * Introduced DBObjectPart
 * Early logging changes (2/25)
 * 
 * Release 2.0 Build 29: Dec 28th 2010
 * ***************************************
 * Moved the code to the new laptop
 * Hope I got the source right
 * experimental
 * In prepration for:
 * user defined paths release
 * 
 * Changed files:
 * .\ai\akc\paramfilters\AKCDisplayItemParamFilter.java
 * .\ai\akc\paramfilters\AKCDisplayItemParamFilterRequest.java
 * .\ai\akc\paramfilters\AKCDisplayParamFilter.java
 * .\ai\akc\paramfilters\AKCDisplayParamFilterRequest.java
 * .\ai\akc\paramfilters\AKCShortURLParamFilter.java
 * .\ai\akc\paramfilters\CLog1.java
 * .\ai\akc\paramfilters\ShortURLMapPerOwner.java
 * .\ai\akc\paramfilters\ShortURLMapRegistry.java
 * .\ai\application\defaultpkg\CLog2.java
 * .\ai\aspire\AspireReleaseNotes.java
 * .\ai\cache\CacheUtils.java
 * .\ai\cache\DefaultCachingService.java
 * .\ai\cache\ICachingService.java
 * .\ai\cache\InvalidateCachePart1.java
 * .\ai\servlets\paramfilters\AParamFilterRequest.java
 * .\ai\servletutils\ServletUtils.java
 * 
 * Release 2.0 Build 28: Feb 20th 2010
 * ***************************************
 * akc parameter filter added (2/12/10)
 * same(a,b,c) if expression added (2/12/10)
 * CopyParametersIfNotAvailablePart
 * IgnoreResult
 * AFactoryPart (updated)
 * ConditionalSubstitutionPart
 * This release is to support sharing of folders
 * akc param filter will leave private urls alone(2/20/10)
 * 
 * Release 2.0 Build 27: Dec 5th 2009
 * ***************************************
 * (SecurityRelease: Prevent Public Access to private Documents)
 * Stop giving access to private content
 * AspireLoggedInPart to add a couple of login utility related functions
 * new class: URLAccessRightsAuthorization
 * new key called: authRequestName on the url
 * Changed servletutils to prevent secure variable copying
 * DocumentSecurityProvider: new class for akc added
 * com.ai.akc.security: a new package added for akc (convenience)
 * FilterUtils: converttoboolean added with a default
 * StringUtils: convertoboolean added
 * DefaultGlobalExceptionHandler1: ability tohandle auth exceptions differently
 * PrintUtils: to support the above
 * BaseServlet: calling getparameters moved up above the isAccess method
 *    throws an auth exception if access not allowed
 * SecureVariables: new class for holding what is a secure variable
 *    for auth does not copy secure variables into parameters
 * AspireLoginPart: Two methods added to get the status of login
 * 
 * Release 2.0 Build 26.1: Aug 12th 2008
 * ***************************************
 * (ResourceCleanup Release)
 * ResourceCleanup support bug fix
 * Cleanup threadlocal properly
 * Clone the list as it might get impacted
 * 
 * Release 2.0 Build 26: Aug 12th 2008
 * ***************************************
 * ResourceCleanup support
 * Fixed a bug with GenericTableHandler and RandomTableHandler
 * Enabled connection pool managers for events
 * 
 * Release 2.0 Build 25.0
 * **********************
 * Contains all the code to support inner master pages
 * statemachine integrated
 * 
 * Release 2.0 Build 24.0
 * **********************
 * November 1st
 * GenericMapper added
 * Update servlet filter added
 * temp directory added to fileutils
 * 
 * Release 2.0 Build 23.6
 * **********************
 * com.ai.parts.HtmlEncoder added
 * StringUtils has changed as well
 * 
 * Release 2.0 Build 23.5
 * ***********************
 * com.ai.db.cpjndi.ConnectionPoolConnectionManager5 added
 * 
 * Release 2.0 Build 23.3
 * **********************
 * Small clean up for cp4
 * 
 * Release 2.0 Build 23.2
 * **********************
 * Dec 22nd - first cut of connection manager 4
 * Preloading connections
 * cleanup task will keep the connections 
 * 
 * Release 2.0 Build 23.1
 * **********************
 * Dec 15th - the whole db.rel2 package was created
 * whole new way of using transactions
 * still in process
 * the old still works
 * 
 * Dec 18th - app initializers now work with plain objects
 * they don't have to be app initializers
 * todo: update the documentation
 * 
 * Release 2.0 Build 22.7
 * ******************************
 * Nov 2nd: Added AspirURLStringReaderPart
 * Oct 15th: Added RandomTableHandler61
 * 
 * Release 2.0 Build 22.5
 * ******************************
 * sept 28th: SessionHDSTransformerPart added
 * sept 5th: flush and close avoided at the end of the baseservlet
 *  
 * Release 2.0 Build 22.4
 * ******************************
 * ConnectionPoolManager3 added. Connection pool tuned
 * FilterEnabledFactory3 added
 * 
 * Release 2.0 Build 22.3
 * ******************************
 * GlobalExceptionHandler introduced
 * RecursiveSubstitutionPart added
 * 
 * Release 2.0, Build 22.2
 * ******************************
 * AspireLoginPart changed for target url redirection
 * 
 * Release 2.0, Build 22.0
 * ******************************
 * DBBaseJavaProcedure changed for better exception handling
 * (22.1) Connection events added
 * 
 * Release 2.0, Build 21.6
 * ******************************
 * GenericXSL transforms added
 * Supports xslt in akc
 * ListDataCollection support added
 * CLog2 introduced
 * ApplicationHolder has an additional init method
 * 
 * Release 2.0, Build 21.5
 * ******************************
 * AliasedFactory3 introduced specializing the FilterEnabledFactory2
 * ForeachPart available
 * It is possible to use class aliases now in properties files
 * SaveAttributesPart for inserting multiple rows available
 * 
 * Release 2.0, Build 21.4
 * ******************************
 * DBProcedure.java added
 * ValueDecoderPart1 added
 *
 * Release 2.0, Build 21.0
 * ******************************
 * URLStringReaderPart introduced
 * Lot of deprecations removed
 * ServletCompatibility introduced
 * Login tags in session changed
 *
 * Release 2.0, Build 20.0
 * ******************************
 * Ability to use aspireContext
 * Ability to use /display /update for servlet paths
 * External session manager added
 * LoginValidator added
 * AspireLoginPart added
 * ServerSideRedirect is an option for every url
 * DUpdateServletRequestFailureResponse1 added for serversideredirect
 * Better equipped for handling form field errors with server side redirect
 * Better equipped for handling security through login pages as opposed to httpauthentication
 *
 * Release 2.0, Build 19.1
 * ******************************
 * RandomTableHandler7 introduced to fix ihds problem
 *
 * Release 2.0, Build 19.0
 * ****************************
 * Requires 2.3 servlets to compile although it can still run on 2.1 servlets
 * Added setEncodingHeader for requests
 * Added HttpEventDistributor
 * Added FileCollectionReader to support photographs
 * EmbeddedXML generic format
 * IApplicationInitializer1 added
 * caching support
 * Master pages
 *
 * Release 2.0, Build 18.6
 * ****************************
 * 18.6 Fixed expression evaluation with DefaultExpressionEvaluator1
 * 18.5 Provided exceptions for if functions
 * 18.5 Fix RandomTableHandler6 when there is no data
 * 18.5 HttpEvents are getting called
 * 18.4 RandomTableHandler6 added with support for paging
 * 18.3 gt, gte, lt, lte, numberEquals added to if functionality
 * 18.2 FormHandler will only return string keys
 * Documentation of parts updated
 * 18.1 No data found is configurable
 *
 *
 * Release 2.0, Build 18.0
 * *************************
 * Reusable headers and trailers facility added
 * A number of parts added under parts package
 * 'if' processing improved with not functionality
 * Default release is now for tomcat 4 and not tomcat 3
 * Recommended parts for this release
 *    AITransform6, DBHashtableFormHandler1, GenericTableHandler6,DBRequestExecutor2,StoredProcExecutor2
 *
 * Release 2.0, Build 17.1
 * *************************
 * Provides stream lined XML capabilities based on HDS
 * Recommended parts for this release
 *    AITransform6, DBHashtableFormHandler1, GenericTableHandler6,DBRequestExecutor2,StoredProcExecutor2
 *
 * Release 2.0, Build 17.0
 * *************************
 * Hierarchical Data set implementation
 * Recommended parts for this release
 *    AITransform6, DBHashtableFormHandler1, GenericTableHandler6,DBRequestExecutor2,StoredProcExecutor2
 *
 * Release 2.0, Build 16.4
 **************************
 * Preparing for WAR suppot
 * directories.aspire now points to the webapp root
 * CLog1 introduced in place of CLog for relative log files
 * Absolute paths still work
 *
 * Release 2.0, Build 16.3
 **************************
 * XMLReader introduced
 *
 * Release 2.0, Build 16.2
 **************************
 * Repeat read of a column for microsoft access
 * SimpleDBAuthentication introduced
 * Recommended parts for this release
 *    AITransform6, GenericTableHandler5,DBRequestExecutor2,StoredProcExecutor2
 *
 * Release 2.0, Build 16.1
 **************************
 * All generictablehandlers now will accept rows either as strings or IDataRows
 * RSDataRow now handles CLOBs
 *
 * Release 2.0, Build 16
 **************************
 * Release prior to cleaning up the IDataCollection and IDataCollection1
 * Release that went out to AspireCharts
 * DBBaseJavaProcedure honours the default parameters
 * (p)substitutions partially streamlined
 * (p)Document the new substitution support
 *
 * Release 2.0, Build 15.3
 **************************
 * ClassicXSLTransform added
 * Previous few builds may not work with XSLT
 * JDOM used for outputting XML
 * Needs JDOM Jar file if you are going to be using XSLT
 *
 * Release 2.0, Build 15.2
 **************************
 * SetHeaders is now pluggable
 *
 * Release 2.0, Build 15.1
 **************************
 * I(A)DataCollectionProducer added
 * I(A)AspireUpdateHandler added
 * PreTranslateArgsMultiRequestExecutor introduced
 *
 * Release 2.0, Build 15.0
 **************************
 * Release for popcharts
 * AITransform6 and RandomTableHandler5 added
 *
 * Release 2.0, Build 14.7
 **************************
 * DefaultTotalsCalculatr added
 *
 * Release 2.0, Build 14.6
 **************************
 * MailRequestExecutor added
 *
 * Release 2.0, Build 14.5
 **************************
 * Initializers added Aspire startup
 * XML/XSL support switched to JAXP
 * XML/XSL requires JAXP related jar files
 *
 * Release 2.0, Build 14.4
 **************************
 * Improvements in AppInitServlet1
 *
 * Release 2.0, Build 14.3
 **************************
 * Major bug fixes for AITransform3
 * Do not use Build 14.2 if you want to use AITransform3
 *
 * Release 2.0, Build 14.2
 **************************
 * AITransform3 fixes the eliminateLoop problem
 * AITransform3 should still be considered experimental until further notice
 * AppInitSerlet1 introduced to fix the path dependency in the web.xml
 *
 * Release 2.0, Build 14.1
 **************************
 * IDataRow is now zero index based
 *
 * Release 2.0, Build 14.0
 **************************
 * IDataRow and DBRSCollection2 introduced
 * DBRequestExecutor2 and StoredProcedureExecutor2 introduced
 *
 * Release 2.0, Build 13.8
 **************************
 * Field scrapper introduced along with AITransform3
 *
 * Release 2.0, Build 13.5
 **************************
 * Error caught and thrown at the async level
 *
 * Release 2.0, Build 13.2
 **************************
 * Flexible exception analysis introduced
 *
 * Release 2.0, Build 13
 **************************
 * SecureVariables introduced
 * New method added to ILog interface
 *
 * Release 2.0, Build 12.5
 **************************
 * Exporter bug fix
 *
 * Release 2.0, Build 12.4
 **************************
 * Bug fix for rootCauseExtraction
 *
 * Release 2.0, Build 12.2
 **************************
 * Importer1 introduced for null columns
 *
 * Release 2.0, Build 12
 *************************
 * Exception specific error handling introduced
 * Support for unique inserts via EmptySetValidator added
 *
 * Release 2.0, Build 11.3
 *************************
 * Support for sequences added
 * Microsoft Access is the first database to be supported
 *
 * Release 2.0, Build 11.1
 *************************
 * ExportRequestExecutor added
 *
 * Release 2.0, Build 11
 *************************
 * Build 10.7 plus converted to a build release
 *
 * Release 2.0, Build 10.7
 *************************
 * Introduced functions into 'if' evaluation
 *
 * Release 2.0, Build 10.6
 *************************
 * When exceptions have null messages, logging is fixed.
 *
 * Release 2.0, Build 10.5
 *************************
 * Exceptions handled by the async request task better
 *
 * Release 2.0, Build 10.4
 *************************
 * Better exception handling in DBBaseJavaProcedure
 * to be tested
 *
 * Release 2.0, Build 10.3
 *************************
 * ITranslatorHashtable introduced
 * Exporter changed to use both ITranslator and ITranslatorHashtable
 * Note: See if IConfig need to be derived from CommonException
 *    See if that entitles a recompile for all utilities
 * Release 2.0, Build 10.2
 *************************
 * Adhoc exception logging for syncra
 * Remove this and find out what the problem is
 *
 * Release 2.0, Build 10
 ***********************
 * 1. Access control finalized"
 * 2. In the middle of access control upgrade"
 * 3. When loop is empty parent values searched"
 * 4. Loop keys searched in the parent as well"
 * 5. Realm bug fixed"
 * 6. Response headers set from the properties file for a Display URL"
 * 7. Primary enhancement: Error handling on the same page"
 * 8. Mail utility updated."
 * Release 2.0, Build 4
 **********************
 * 1. Modified GenericTableHandler4 to include new looping in JSP pages
 * 2. Modified DBRSCollection1 to allow isAtTheEnd() semantics for the iterator
 * 3. Updated documentation for JSP pages and the usage of the IDataCollection
 * 4. IControlHandler3 introduced for looping
 * 5. GenericTableHandler4 now supports IControlHandler3
 *
 */
public interface AspireReleaseNotes
{

}
