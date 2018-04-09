/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servletutils;
import java.util.*;

public interface IURL 
{
   public String protocol();
   public String host();
   public String port();
   public String uri();
   public String queryString();
   public Hashtable params();
   public String url();
} 
