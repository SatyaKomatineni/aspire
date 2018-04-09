package com.ai.masterpage;

/**
 * Test master page.
 */

public class EmptyMasterPage implements IMasterPage
{
   public String topHalf() throws com.ai.servlets.AspireServletException
   {
      return "";
   }

   public String bottomHalf() throws com.ai.servlets.AspireServletException
   {
      return "";
   }
   public String headerInclude() throws com.ai.servlets.AspireServletException
   {
      return "";
   }

}
