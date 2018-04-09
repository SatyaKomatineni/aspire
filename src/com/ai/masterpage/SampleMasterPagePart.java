package com.ai.masterpage;
import java.util.Map;
import com.ai.application.interfaces.RequestExecutionException;

public class SampleMasterPagePart extends AMasterPageCreatorPart
{
   private static IMasterPage m_mp = new SampleMasterPage();
   protected IMasterPage create(String requestName, Map params)
          throws RequestExecutionException
   {
      return m_mp;
   }
}