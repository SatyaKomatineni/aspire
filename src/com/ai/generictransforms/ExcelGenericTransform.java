package com.ai.generictransforms;
import com.ai.htmlgen.*;
import com.ai.common.TransformException;
import java.io.*;
import com.ai.data.*;
import javax.servlet.http.*;

/**
 * A transform to support debugging
 * Content-Type=application/vnd.ms-excel|Content-Disposition=filename=form.xls
 */
public class ExcelGenericTransform extends AHttpGenericTransform implements IFormHandlerTransform {

    private static String s_separator = "\t";
    protected String getDerivedHeaders(HttpServletRequest request)
    {
        return "Content-Type=application/vnd.ms-excel|Content-Disposition=filename=aspire-hierarchical-dataset.xls";
    }
    public void transform(ihds data, PrintWriter out) throws TransformException
    {
        staticTransform(data,out);
    }
    public void transform(IFormHandler data, PrintWriter out) throws TransformException
    {
        staticTransform((ihds)data,out);
    }
    public static void staticTransform(ihds data, PrintWriter out) throws TransformException
    {
        try
        {
            writeALoop("MainData",data,out,"");
        }
        catch(DataException x)
        {
            throw new TransformException("Error: ExcelGenericTransform: Data Exception",x);
        }
    }
    private static void writeALoop(String loopname, ihds data, PrintWriter out, String is)
            throws DataException
    {
        println(out,is, ">> Writing data for loop:" + loopname);

        // write metadata
        IMetaData m = data.getMetaData();
        IIterator columns = m.getIterator();

        StringBuffer colBuffer = new StringBuffer();
        for(columns.moveToFirst();!columns.isAtTheEnd();columns.moveToNext())
        {
            String columnName = (String)columns.getCurrentElement();
            colBuffer.append(columnName).append(s_separator);
        }
        println(out,is,colBuffer.toString());

        //write individual rows
        for(data.moveToFirst();!data.isAtTheEnd();data.moveToNext())
        {
            StringBuffer rowBuffer = new StringBuffer();
            for(columns.moveToFirst();!columns.isAtTheEnd();columns.moveToNext())
            {
                String columnName = (String)columns.getCurrentElement();
                rowBuffer.append(data.getValue(columnName));
                rowBuffer.append(s_separator);
            }
            println(out,is,rowBuffer.toString());

            // recursive call to print children
            IIterator children = data.getChildNames();
            for(children.moveToFirst();!children.isAtTheEnd();children.moveToNext())
            {
                //for each child
                String childName = (String)children.getCurrentElement();
                ihds child = data.getChild(childName);
                writeALoop(childName,child,out,is + "\t");
            }//for each child
        }// for each row

        data.close();
        println(out,is,">> Writing data for loop:" + loopname + " is complete");
    }//eofc
    private static void println(PrintWriter out, String indentationString, String line)
    {
        out.print(indentationString);
        out.print(line);
        out.print("\n");
    }
}
