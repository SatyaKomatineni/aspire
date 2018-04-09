package com.ai.test;
import java.util.*;
import java.io.*;
import com.ai.common.*;

public class CommandRequest
{
   private String m_command;
   private Map<String,String> m_args;
   
   public CommandRequest(final String commandArgs)
   {
      Vector commandVector = Tokenizer.tokenize(commandArgs,",");
      if (commandVector.size() <= 0)
      {
         m_command = null;
         return;
      }
      m_args = new HashMap<String,String>();
      m_command = (String)commandVector.get(0);
      if (commandVector.size() > 1)
      {
         Enumeration e=commandVector.elements();
         e.nextElement();
         while(e.hasMoreElements())
         {
            String arg = (String)e.nextElement();
            Vector keyValueVector = Tokenizer.tokenize(arg,"=");
            String key = (String)keyValueVector.get(0);
            String value = (String)keyValueVector.get(1); 
            m_args.put(key,value);
         }
      }
      return;
   }
   public CommandRequest(String commandName, Map<String,String> args)
   {
	   m_command = commandName;
	   m_args = args;
   }
   public String getCommand(){ return m_command;}
   public Map<String,String> getArgs(){return m_args;}
   
   public static CommandRequest readFromInput() throws IOException
   {
     
     BufferedReader bufReader = new BufferedReader(new InputStreamReader(System.in));
     CommandRequest cmdRequest = new CommandRequest(bufReader.readLine());
     bufReader.close();
     return cmdRequest;
   }
}
