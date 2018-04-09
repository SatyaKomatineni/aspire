package com.ai.masterpage;

import java.util.Map;
import com.ai.application.interfaces.IInitializable;
import com.ai.application.utils.AppObjects;
import com.ai.data.*;
import com.ai.application.interfaces.*;
import com.ai.common.*;
import com.ai.cache.*;

/**
 * Read the master page template string from a request
 * Break the string into two
 *
 * url inputs
 * ***********
 * requestname
 * params
 *
 * Configuration inputs
 * ***********************
 * masterPageTemplateRN:
 *    Request Name to retrieve the content string for a master page
 *    Typically points to a select statement via a DBRequestExecutor2
 *
 * Notes
 * *****************************
 * 1. The select statement should have a columns called "masterPageTemplate"
 * 2. Inside the content string of that you should have a subst string called
 *       {{aspire_content}} all lower case
 *
 * Ex:
 * ********************************
 * request.UserMasterPageRequest.classname=com.ai.masterpage.RequestBasedMasterPagePart
 * request.UserMasterPageRequest.masterPageTemplateRN=UserMasterPageTemplate
 *
 * request.UserMasterPageTemplate.classname=com.ai.db.DBRequestExecutor2
 * request.UserMasterPageTemplate.db=<database-alias>
 * request.UserMasterPageTemplate.stmt=\
 * \
 * select item_description as masterPageTemplate \
 * from items_table \
 * where item_id = (select master_template_item_id  \
 *                  from users \
 *                   where userid = thispage_user_id )26 \
 *
 */

public class RequestBasedMasterPagePart extends AMasterPageCreatorPart implements IInitializable
{

   private String mpRequestName = null;

    public void initialize(String requestName)
    {
       mpRequestName = AppObjects.getValue(requestName + ".masterPageTemplateRN",null);
    }

   protected IMasterPage create(String requestName, Map params)
          throws RequestExecutionException
   {
      //Get it from the cache
      Object obj = CacheUtils.getObjectFromCache(requestName, params);
      if (obj != null)
      {
         //Object is in the cache
         return (IMasterPage)obj;
      }

      AppObjects.info(this,"Object is not in the cachej for the masterPageTemplate");
      IDataCollection col = null;
      IMasterPage masterPage = null;
      try
      {
         col = (IDataCollection)AppObjects.getObject(mpRequestName, params);
         IIterator itr = col.getIIterator();
         if (itr.isAtTheEnd() == true)
         {
            AppObjects.warn(this,"No rows retrieved. NO master page for this user");
            masterPage = new EmptyMasterPage();
         }
         else
         {
            itr.moveToFirst();
            IDataRow dr = (IDataRow)itr.getCurrentElement();
            String masterPageTemplate = dr.getValue("masterPageTemplate");

            masterPage = this.getMasterPageFromString(masterPageTemplate);
         }

         //Place the object in cache
         CacheUtils.putObjectInCache(requestName, params,masterPage);
         return masterPage;
      }
      catch(DataException x)
      {
         throw new RequestExecutionException("Error:Could not get collection iterator",x);
      }
      catch(com.ai.data.FieldNameNotFoundException x)
      {
         throw new RequestExecutionException("Error:A field called masterPageTemplate not found in the relational data",x);
      }
      finally
      {
         if (col != null)
         {
            try {col.closeCollection();}
            catch(DataException x) { AppObjects.log("Error:Error closing collection",x);}
          }
      }//finally

   }//eof-function
   /**
    * Must return a valid page
    * null is not accepted
    */
   private IMasterPage getMasterPageFromString(String masterPageTemplate)
   {
      String headerPart = getHeaderPart(masterPageTemplate);
      String bodyPart = getBodyPart(masterPageTemplate);

      if (bodyPart == null)
      {
         //Assume the whole string is the body part
         bodyPart = masterPageTemplate;
      }
      int contentIndex = bodyPart.indexOf("{{ASPIRE_MP_CONTENT}}");
      if (contentIndex == -1)
      {
         AppObjects.error(this,"No master page found. aspire content tag is not there");
         return new EmptyMasterPage();
      }

      String topHalf = bodyPart.substring(0,contentIndex);
      String bottomHalf = bodyPart.substring(contentIndex + 21);

      AppObjects.info(this,"mp:Successfully constructing a master page");
      AppObjects.info(this, "mp:tophalf:%1s", topHalf);
      AppObjects.info(this,"mp:bottomhalf:%1s", bottomHalf);
      AppObjects.info(this,"mp:header:%1s", headerPart);

      IMasterPage masterPage = new DefaultMasterPage(topHalf, bottomHalf,headerPart);
      return masterPage;

   }
   private String getHeaderPart(String mpTemplate)
   {
      //Extract header first
      int headerStIndex = mpTemplate.indexOf("<!--ASPIRE_MP_HEADER_BEGIN-->");
      if (headerStIndex == -1)
      {
         AppObjects.info(this,"No header detected. Assuming no header");
         return "";
      }
      int headerEndIndex = mpTemplate.indexOf("<!--ASPIRE_MP_HEADER_END-->");
      if (headerEndIndex == -1)
      {
         AppObjects.error(this,"Error:Master page header start detected but no header end");
         return "";
      }
      return mpTemplate.substring(headerStIndex + 29, headerEndIndex);

   }//eof-function

   private String getBodyPart(String mpTemplate)
   {
      //Extract header first
      int bodyStIndex = mpTemplate.indexOf("<!--ASPIRE_MP_BODY_BEGIN-->");
      if (bodyStIndex == -1)
      {
         AppObjects.info(this,"No Body part detected. Assuming the entire document as a body");
         return null;
      }
      int bodyEndIndex = mpTemplate.indexOf("<!--ASPIRE_MP_BODY_END-->");
      if (bodyEndIndex == -1)
      {
         AppObjects.error(this,"Master page header start detected but no header end");
         return null;
      }
      return mpTemplate.substring(bodyStIndex + 27, bodyEndIndex);
   }//eof-function
}//eof-class