/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;

public interface IServletRepository 
{
   public Object getObject(final String name );
   public void setObject(final String name, Object obj);
} 