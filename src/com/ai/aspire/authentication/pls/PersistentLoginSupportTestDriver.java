package com.ai.aspire.authentication.pls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ai.common.FileUtils;

/**
 * 
 * This is a test driver to test 
 * to see if the class can be persisted using XML as is
 * 
 * @see LoggedInUserKeysDataset
 * @see PLSUserDetail
 * 
 */
public class PersistentLoginSupportTestDriver 
{
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
	   }
	   catch(JAXBException x) {
		   throw new RuntimeException(x);
	   }
   }
   public static void main(String[] args)
   {
	   try	{
		   test2();
	   }
	   catch(Exception x)	{
		   throw new RuntimeException(x);
	   }
   }
  
   private static void test2() throws Exception
   {
	   LoggedInUserKeysDataset data = LoggedInUserKeysDataset.createASample();
       
       //Marshal to system output: java to xml
	   printMessage("Direct output as XML");
       m.marshal(data,System.out);
       
       //get it as a string first
       String s = getObjectAsString(data);
	   printMessage("String output as XML");
	   System.out.println(s);
	   
	   //Read it back as object
	   LoggedInUserKeysDataset newData 
	   = (LoggedInUserKeysDataset)getStringAsObject(s);
	   
       //get it as a string first
       String s1 = getObjectAsString(newData);
	   printMessage("String output as XML");
	   System.out.println(s1);
   }
   
   private static void printMessage(String x)
   {
	   System.out.println("********************************");
	   System.out.println(x);
	   System.out.println("********************************");
   }
   private static String getObjectAsString(Object o)
   throws JAXBException
   {
	   ByteArrayOutputStream bos = new ByteArrayOutputStream();
	   try	{
		   m.marshal(o,bos);
		   return bos.toString();
	   }
	   finally	{
		   FileUtils.closeStream(bos);
	   }
   }
   private static Object getStringAsObject(String s)
   throws JAXBException
   {
	   //create a string input stream
	   return um.unmarshal(new ByteArrayInputStream(s.getBytes()));
   }
}//eof-class
