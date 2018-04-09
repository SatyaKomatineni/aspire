package com.ai.parts;

import com.ai.application.interfaces.*;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.data.*;
import javax.servlet.http.*;
import com.ai.common.*;

/**
 * A sample part to be used for debugging
 *
 * Additional property file arguments
 * None
 *
 * Expected input args
 * name
 * 
 * Output/Behaviour
 * 1.resultName: Completed hello word string
 * 2. Will write a debug message to the log
 *
 */

public class HelloWorldPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
            throws RequestExecutionException
    {
    	String name = (String)inArgs.get("name");
    	if (name == null)
    	{
    	   name = "annonymous";
    	}
    	
    	String message = "HelloWorld:" + name;
    	AppObjects.info(this,message);
    	return message;
    	
    }//eof-function
}//eof-class
