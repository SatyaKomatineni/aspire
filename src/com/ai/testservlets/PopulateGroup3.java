/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.testservlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

// ai related imports
import com.ai.servletutils.*;
import com.ai.application.utils.*;
import com.ai.application.defaultpkg.*;
import com.ai.data.*;
import com.ai.common.*;
import com.ai.servlets.*;

public class PopulateGroup3 extends ProfileEnabledServlet
{

  //Initialize global variables
  public void init(ServletConfig config) throws ServletException
  {
    super.init(config);
  }

   public void serviceRequest(String user,
                                HttpSession session,
                                String uri,
                                String query,
                                Hashtable parameters,
                                PrintWriter out
                                ,HttpServletRequest request
                                ,HttpServletResponse response )
   {
        // set the response header so that it is always expired
        response.setDateHeader("Expires",0);
      try
      {
         AppObjects.log(parameters.toString());
         String command = (String)parameters.get("command");
         String client_gid =  (String)parameters.get("client_gid");
         StringBuffer buf = getJSForOpen(client_gid,command, parameters);
//         ServletUtils.writeOutAsJS(out,buf);
         out.print(embedIntoOnLoad(buf.toString()));
      }
      catch(com.ai.data.FieldNameNotFoundException x)
      {
         PrintUtils.writeCompleteMessage(out
            ,"Retrieved data not in tree format\n" + x.getMessage());
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         PrintUtils.writeCompleteMessage(out
            ,"Retrieved data not in tree format\n"
            + x.getMessage());
      }
      catch(com.ai.data.DataException x)
      {
         PrintUtils.writeCompleteMessage(out
            ,"Retrieved data not in tree format\n"
            + x.getMessage());
      }
   }                               
  private StringBuffer getJSForMainGroup() 
         throws com.ai.data.DataException, com.ai.application.interfaces.RequestExecutionException
  {

      StringBuffer buf = new StringBuffer();
      // initialize the root group
      buf.append("rootGroup = parent.initGroup(\"root\",parent.TREE_FRAME.document,\"TREE_FRAME\");");

      // get additional expanded groups
      IDataCollection tCol = (IDataCollection)AppObjects.getIFactory().getObject("GET_TRADELANES",null);
      IIterator itr = tCol.getIIterator();      
      AppObjects.log("before for");      
      for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
      {
         IDataRow dataRow = (IDataRow)itr.getCurrentElement();
         buf.append("\nparent.addGroup(rootGroup.id"
                    + "," +  ServletUtils.quote(dataRow.getValue(0))
                    + "," +  ServletUtils.quote(dataRow.getValue(0)) 
                    + ");" );
         AppObjects.log(buf.toString());                    
         System.out.flush();
      }
      tCol.closeCollection();
      // Go ahead and redraw the group
//      buf.append("\nparent.redrawGroup(rootGroup.id);");
      return buf;      
  }
  private StringBuffer getJSForOpen(String gid, String command, Hashtable args) 
         throws com.ai.data.DataException
         ,com.ai.application.interfaces.RequestExecutionException
         ,com.ai.data.FieldNameNotFoundException
  {

      StringBuffer buf = new StringBuffer();
      if(Integer.parseInt(gid) == 0)
      {
         String rootName = (String)args.get(AspireConstants.TREE_ROOT_PARAMETER);
         if (rootName == null) { rootName = "root"; }
         // initialize the root group
         buf.append("rootGroup = parent.initGroup(" 
                        + ServletUtils.quote(rootName)
                        + ",\"TREE_FRAME\");");
         gid = "rootGroup.id";
      }
      
      // get additional expanded groups
      IDataCollection tCol = null;

      try
      {
         tCol = (IDataCollection)AppObjects.getIFactory().getObject(command,args);
         IIterator itr = tCol.getIIterator();      
         IMetaData metaData = tCol.getIMetaData();
         AppObjects.log("before for");      
         for(itr.moveToFirst();!itr.isAtTheEnd();itr.moveToNext())
         {
            IDataRow dataRow = (IDataRow)itr.getCurrentElement();
         
            buf.append("\nparent.addGroup(" + gid
                       + "," +  ServletUtils.quote(dataRow.getValue("groupname"))
                       + "," +  ServletUtils.quote(dataRow.getValue("expand_command"))
                       + "," +  ServletUtils.quote(dataRow.getValue("url","none"))
                       + "," +  ServletUtils.quote(dataRow.getValue("target","none"))
                       + "," +  ServletUtils.quote(dataRow.getValue("nodetype","group"))
                       + ");" );
   //         AppObjects.log(buf.toString());                    
            System.out.flush();
         }
      
         // Go ahead and redraw the group
         buf.append("\nparent.redrawGroup(" + gid + ");" );
         return buf;      
      }
      finally
      {
         tCol.closeCollection();
      }
  }
  private String embedIntoOnLoad(String js)
  {
    StringBuffer buf = new StringBuffer("<html><head>\n");
    ServletUtils.appendJSHeader(buf);
    buf.append("\nfunction onLoadHandler() { \n");
    buf.append(js);
    buf.append("\n}");
    ServletUtils.appendJSTail(buf);
    buf.append("</head>");
    buf.append("<body onLoad="); 
    buf.append( ServletUtils.quote("javascript:onLoadHandler()") );
    buf.append(">");
    buf.append("\n</body></html>");
    return buf.toString();
  }
} 