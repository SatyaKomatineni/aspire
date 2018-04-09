package com.ai.xml;
import org.w3c.dom.*;
import java.io.*;
import com.ai.common.*;

public interface IXMLOutputter 
{
   static public String NAME="XMLOutputter";
   public void output(Document doc, Writer writer) throws IOException, TransformException;
} 
