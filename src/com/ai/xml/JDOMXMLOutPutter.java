package com.ai.xml;
import org.jdom.input.*;
import org.jdom.output.*;

import com.ai.application.interfaces.*;

import org.w3c.dom.*;
import java.io.*;
import com.ai.common.*;

public class JDOMXMLOutPutter implements IXMLOutputter, ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   

   public void output(Document doc, Writer writer) throws IOException, TransformException
   {
      DOMBuilder db = new DOMBuilder();
      XMLOutputter fmt = new XMLOutputter();
      fmt.output(db.build(doc),writer);
   }
} 
