package com.ai.masterpage;

/**
 * status: Experimental
 * Represents a master page for all pages
 */

public interface IMasterPage
{
   public String headerInclude() throws com.ai.servlets.AspireServletException;
   public String topHalf() throws com.ai.servlets.AspireServletException;
   public String bottomHalf() throws com.ai.servlets.AspireServletException;
}