package com.ai.masterpage;

/**
 * A place holder for IMasterPage components
 */

public class DefaultMasterPage implements IMasterPage
{
   private String m_topHalf = null;
   private String m_bottomHalf = null;
   private String m_headerInclude = null;

   public DefaultMasterPage(String topHalf, String bottomHalf, String headerInclude)
   {
      m_topHalf = topHalf;
      m_bottomHalf = bottomHalf;
      m_headerInclude = headerInclude;
   }
   public String headerInclude() throws com.ai.servlets.AspireServletException { return m_headerInclude; }
   public String topHalf() throws com.ai.servlets.AspireServletException { return m_topHalf; }
   public String bottomHalf() throws com.ai.servlets.AspireServletException{ return m_bottomHalf;}

}