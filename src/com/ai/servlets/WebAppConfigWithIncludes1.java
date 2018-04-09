package com.ai.servlets;

import com.ai.application.defaultpkg.CConfigWithIncludes1;
import com.ai.application.interfaces.IResourceReader;

public class WebAppConfigWithIncludes1 extends CConfigWithIncludes1
{
   protected IResourceReader getExternalResourceReaderHook()
   {
      return new WebAppResourceReader();
   }

}