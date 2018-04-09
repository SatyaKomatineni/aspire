/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.extensions.xmlconfig;

import javax.xml.parsers.*;
import org.w3c.dom.*;

public interface IConfigSectionProcessor 
{
  public void processSection(Node xmlNode, IConfigSectionProcessorOutput out);
} 
