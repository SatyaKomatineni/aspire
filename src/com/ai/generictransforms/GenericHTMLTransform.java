package com.ai.generictransforms;

import com.ai.htmlgen.*;
import com.ai.common.TransformException;
import java.io.*;
import com.ai.data.*;
import javax.servlet.http.*;
import com.ai.common.*;
import java.util.*;
import com.ai.application.utils.*;

/**
 * A transform to support generic html transformation
 * Content-Type=application/vnd.ms-excel|Content-Disposition=filename=form.xls
 */
public class GenericHTMLTransform extends AHttpGenericTransform implements IFormHandlerTransform {

  private static String headerIncludesString = null;

  /**
   * Make sure the derived class gives this class a chance
   * @param requestName
   */
  public void initialize(String requestName)
  {
     // initialize the base class methods
     super.initialize(requestName);

      //Read the filename that has the header strings in it
      String filename =   AppObjects.getValue(requestName + ".htmlHeadersFilename",null);

      //if there is no filename set it to null
      if (filename == null)
      {
         headerIncludesString = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"/aspire/style/style.css\"></head><body>";
         return;
      }

      //filename exists
      //read the file into a string
      try
      {
         String headerString = FileUtils.readFile(FileUtils.translateFileName(filename));
         headerIncludesString = headerString;
      }
      catch(java.io.IOException x)
      {
         AppObjects.log("Warning: Could not read the header template file",x);
         headerIncludesString = "<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"/aspire/style/style.css\"></head><body>";
      }
    }

    protected String getDerivedHeaders(HttpServletRequest request)
    {
        return "Content-Type=text/html;";
    }
    public void transform(ihds data, PrintWriter out) throws TransformException
    {
        staticTransform(data,out);
    }
    public void transform(IFormHandler data, PrintWriter out) throws TransformException
    {
        staticTransform((ihds)data,out);
    }
    private static String getHtmlHeader()
    {
          return headerIncludesString;
    }

    public static void staticTransform(ihds data, PrintWriter out) throws TransformException
    {
        try
        {
            out.println(getHtmlHeader());
            String loopnames = data.getValue("aspire_loopnames_to_render");
            if (loopnames == null) loopnames = "primaryloop";
            if (loopnames.equals("")) loopnames= "primaryloop";
            Vector vLoops = Tokenizer.tokenize(loopnames,",");
            for(Enumeration e=vLoops.elements();e.hasMoreElements();)
            {
                String loopname = (String)e.nextElement();
                writeTable(loopname,data,out);
            }
            out.println("</body>");
        }
        catch(DataException x)
        {
            throw new TransformException("Error: DebugTextTransform: Data Exception",x);
        }
    }

    /**
     * Locate the loop names
     */
    private static void writeTable(String loopname, ihds pageData, PrintWriter out)
            throws DataException
    {
        ihds data = pageData.getChild(loopname);
        if (data == null)
        {
            out.println("<p>A specified loop could not be found</p>");
            return;
        }
        if (data.isAtTheEnd())
        {
            out.println("<p>No rows found in this loop structure named:" + loopname + "</p>");
            return;
        }

        //Some rows exist

        // write metadata
        IMetaData m = data.getMetaData();
        IIterator columns = m.getIterator();

        //write table header
        out.println("<table class=\"aspire_autogen_table_1\"><tr class=\"header\">");

        for(columns.moveToFirst();!columns.isAtTheEnd();columns.moveToNext())
        {
            String columnName = (String)columns.getCurrentElement();
            out.println("<th>" + columnName + "</th>");
        }

        //close the header row
        out.println("</tr>");

        //write individual rows
        for(data.moveToFirst();!data.isAtTheEnd();data.moveToNext())
        {
            //Start a row
            out.println("<tr>");
            for(columns.moveToFirst();!columns.isAtTheEnd();columns.moveToNext())
            {
                String columnName = (String)columns.getCurrentElement();
                String columnValue = data.getValue(columnName);
                out.println("<td>" + columnValue + "</td>");
            }

            out.println("</tr>");
        }// for each row

        //Close the table
        out.println("</table>");
        data.close();
    }//eofc
}

