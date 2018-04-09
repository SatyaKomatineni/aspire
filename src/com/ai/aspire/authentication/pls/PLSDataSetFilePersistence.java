package com.ai.aspire.authentication.pls;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ai.application.interfaces.ConfigException;
import com.ai.application.utils.AppObjects;
import com.ai.common.FileUtils;
import com.ai.servlets.AspireConstants;

/**
 * 6/18/17
 * ****************
 * @author satya
 * To assist save and retrieve for logging keys
 * 
 * Will be invoked by aspire factories
 * Singleton
 * 
 * Config file entries
 * ********************
 * 
 * -- This class implements looking up random keys for a user
 * -- if the user has logged in before
 * request.aspire.authentication.persistentSupportLoginObject.classname=\
 * com.ai.aspire.authentication.pls.PersistentLoginSupport2
 *
 * -- This class is invoked by the previous class
 * -- to store the user login satus persistently in a file.
 * request.aspire.authentication.plsDataPersistenceObject.classname=\
 * com.ai.aspire.authentication.pls.PLSDataSetFilePersistence
 * 
 * -- This is the filename used by the class above
 * -- Being stored in the web-inf location it is not visible
 * aspire.authentication.plsDataPersistenceFilename=\
 * aspire:\\web-inf\\aspire\\temp\\persistent-login-info.xml
 * 
 * Goal
 * *****************
 * Use a file to save and restore the dataset
 * represented by LoggedInUserKeysDataset. 
 * 
 * Uses jaxb to get a string first
 *
 * @see LoggedInUserKeysDataset
 * @see PersistentLoginSupport2
 */

public class PLSDataSetFilePersistence
implements IPLSDataSetPersistence
{
	//File reference to save and restore
	private static String FILE_IDENTIFIER = AspireConstants.AC_PLS_DATA_PERSISTENCE_FILE_NAME;
	private static String absoluteFilename = null;
	
	//Hava static marshallers
	private static JAXBContext jc;
	private static Marshaller m;
	private static Unmarshaller um;
   
   //Initialize them
   static
   {
	   try {
		   jc = JAXBContext.newInstance(LoggedInUserKeysDataset.class);
		   m = jc.createMarshaller();
		   um = jc.createUnmarshaller();
		   m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		   
		   absoluteFilename = FileUtils.translateFileIdentifier(FILE_IDENTIFIER);
	   }
	   catch(JAXBException x) {
		   throw new RuntimeException("Not able to create jaxb context objects",x);
	   }
	   catch(ConfigException x)
	   {
		   throw new RuntimeException("Not able to read filename for:" + FILE_IDENTIFIER);
	   }
   }
	
	@Override
	public void save(LoggedInUserKeysDataset data)
	{
		OutputStream os = null;
		try {
			//write to a file
			os = new FileOutputStream(absoluteFilename);
			m.marshal(data, os);
		}
		catch(FileNotFoundException x)
		{
			throw new RuntimeException("Not able to create for write:" + absoluteFilename,x);
		}
		catch(JAXBException x)
		{
			throw new RuntimeException("Not able to marshal login data",x);
		}
		finally
		{
			FileUtils.closeStream(os);
		}
	}
	@Override
	public LoggedInUserKeysDataset recover()
	{
		InputStream is = null;
		try {
			//write to a file
			is = new FileInputStream(absoluteFilename);
			return (LoggedInUserKeysDataset)um.unmarshal(is);
		}
		catch(FileNotFoundException x)
		{
			//File not found
			//it could be the first time
			AppObjects.log(AppObjects.LOG_WARN_S,x);
			AppObjects.warn(this,"Not able to open for read: %1s",absoluteFilename);
			return new LoggedInUserKeysDataset();
		}
		catch(JAXBException x)
		{
			throw new RuntimeException("Not able to unmarshal login data from file",x);
		}
		finally
		{
			FileUtils.closeStream(is);
		}
	}
}//eof-class
