/*
 * Created on May 12, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.ai.parts;

import java.util.Map;

import com.ai.application.interfaces.AFactoryPart;
import com.ai.application.interfaces.ConfigException;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.common.StringUtils;

public class HtmlEncoderPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
       try
       {
          String encodingKey= AppObjects.getValue(requestName + ".encodingKey");
          String inEncodeString = (String)inArgs.get(encodingKey.toLowerCase());
          if (inEncodeString == null)
          {
        	  throw new RequestExecutionException("Encoding key not found in the args");
          }
          //encoding key value found
          return StringUtils.htmlEncode(inEncodeString);
       }
       catch(ConfigException x)
       {
          throw new RequestExecutionException("Error:config errror",x);
       }
    }//eof-function
}//eof-class

