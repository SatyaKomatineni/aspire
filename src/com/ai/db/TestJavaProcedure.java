/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db;
import java.sql.*;
import java.util.*;
import com.ai.application.interfaces.*;
import com.ai.application.interfaces.RequestExecutorResponse;
import com.ai.application.utils.*;

public class TestJavaProcedure extends DBBaseJavaProcedure
{
   public Object executeProcedure(Connection con, 
                                 boolean bConnectionCreator, 
                                 String requestName, 
                                 Hashtable arguments)
         throws DBException, SQLException
   {
      AppObjects.log("Arguments are: " + arguments.toString());
      AppObjects.log("Requestname is : " + requestName );
      return new RequestExecutorResponse(true);
   }         
} 
