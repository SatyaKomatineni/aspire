package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.utils.TransformUtils;
import com.ai.common.*;
import java.io.*;
import java.util.Hashtable;
import java.util.Map;
/**
 * Takes an Aspire URL name and exports the contents of that URL output to the specified filename
 *
 * Additional property file arguments
 * 1. aspireUrlName=<name of the aspire url>
 * 2. outputFilename=<export filename with substitution enabled>
 *
 * Output
 * 1.resultName: Absolute filename to which the contents are exported
 *
 */

public class URLExporterPart extends AFactoryPart
{
    protected Object executeRequestForPart(String requestName, Map inArgs)
        throws RequestExecutionException
    {
        PrintWriter w = null;
        String aspireFilename = null;
        try
        {
		//mandatory args
            String aspireURLName = AppObjects.getValue(requestName + ".aspireUrlName");
            String filename = AppObjects.getValue(requestName + ".outputFilename");

            String subFilename = SubstitutorUtils.generalSubstitute(filename, inArgs);
            aspireFilename = FileUtils.translateFileName(subFilename);
            w = new PrintWriter(new FileOutputStream(aspireFilename));
            TransformUtils.transform(aspireURLName, (Hashtable)inArgs, w);
            String s = filename;
            return s;
        }
        catch(ConfigException x)
        {
            throw new RequestExecutionException("Error: ConfigException. See the embedded exception for details", x);
        }
        catch(IOException x)
        {
            throw new RequestExecutionException("Error: Error wrting to file:".concat(String.valueOf(String.valueOf(aspireFilename))), x);
        }
        catch(TransformException x)
        {
            throw new RequestExecutionException("Error: TransformException", x);
        }
        finally
        {
            if(w != null)
                w.close();
        }
    }
}
