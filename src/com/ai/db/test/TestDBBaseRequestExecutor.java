/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.db.test;

import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.sql.*;
import java.util.*;
import com.ai.common.*;

// Please move this object to com.ai.common or db
// 
import com.ai.application.interfaces.RequestExecutorResponse;

import com.ai.db.*;

public class TestDBBaseRequestExecutor extends DBBaseRequestExecutor 
{

   public Object executeStatements(Connection con,
                                       Object args,
                                       Vector statementVector,
                                       Vector modifiedStatementVector )
                                       throws java.sql.SQLException
   {
      System.out.println("Request structure ");
      System.out.println(args.toString());
      System.out.println(statementVector.toString());
      System.out.println(modifiedStatementVector.toString());
      return new RequestExecutorResponse(true);
   }                                       
}  