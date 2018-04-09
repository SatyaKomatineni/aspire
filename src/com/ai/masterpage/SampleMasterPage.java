package com.ai.masterpage;

/**
 * Test master page.
 */

public class SampleMasterPage implements IMasterPage
{
   public String topHalf() throws com.ai.servlets.AspireServletException
   {
      return "This is the top half";
   }

   public String bottomHalf() throws com.ai.servlets.AspireServletException
   {
      return "This is the bottom half";
   }
   public String headerInclude() throws com.ai.servlets.AspireServletException
   {
      return "";
   }

}