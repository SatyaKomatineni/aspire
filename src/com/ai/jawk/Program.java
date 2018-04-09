package com.ai.jawk;
import java.util.*;
import com.ai.common.*;

/**
 * Program is an ordered set of string literals and expressions
 * string literal: Any continuous string that is not an expression
 * expression: object.function(param1,param2,..).function(param1,param2..)
 * param1 could be another expression
 *
 * evaluating an expression
 *    1. evaluate each param
 *    2. identify object
 *    3. perform function on the object
 *
 * usage
 *********
 * Program p = new Program(args);
 * String resString = p.execute(new WordEvaluator(line));
 *
 * Interpretation
 * Spit out every argument from args, and replace the $ signs with words from line
 * 
 * 
 */
public class Program 
{
   static int ST_LITERAL = 0;
   static int ST_OBJECT = 1;

   Vector m_exprVector = null;
   
   public Program(String programString)
   {
      m_exprVector = getProgramVector(programString);
   }
   
   public Program(Vector strings)
   {
      m_exprVector = new Vector();
      for(Enumeration e=strings.elements();e.hasMoreElements();)
      {
         String curString = (String)e.nextElement();
         if (curString.charAt(0) == '$')
         {
            // expression
            m_exprVector.addElement(new Expression(curString.substring(1)));
         }
         else
         {
            if (m_exprVector.size() == 0)
            { // first element
               m_exprVector.addElement(new Literal(curString + " "));
            }
            else
            { // non-first element
               m_exprVector.addElement(new Literal( " " + curString + " "));
            }
         }   
      }
   }      
   public Program(String strings[])
   {
      m_exprVector = new Vector();
      for(int i=0;i<strings.length;i++)
      {
         String curString = strings[i];
         if (curString.charAt(0) == '$')
         {
            // expression
            m_exprVector.addElement(new Expression(curString.substring(1)));
         }
         else
         {
            if (m_exprVector.size() == 0)
            { // first element
               m_exprVector.addElement(new Literal(curString + " "));
            }
            else
            {
               m_exprVector.addElement(new Literal(" " + curString + " "));
            }
         }
      }
        
   }
   
   public String execute(IEvaluator eval)
   {
      StringBuffer buf = new StringBuffer();
      for(Enumeration e=m_exprVector.elements();e.hasMoreElements();)
      {
         BasePart bp = (BasePart)e.nextElement();
         buf.append(bp.evaluate(eval));
      }
      return buf.toString();
   }
   private Vector getProgramVector(String inStr)
   {
      
      Vector exprVector = new Vector();      
      StringBuffer curLiteral = new StringBuffer();
      StringBuffer curExpression = new StringBuffer();
      int curState = ST_LITERAL;
      
      for(int i=0;i<inStr.length();i++)
      {
         int curChar = inStr.charAt(i);
         if (curState == ST_LITERAL)
         {
            if (curChar != '$')
            {
               curLiteral.append(curChar);
            }
            else
            {
               // state change
               if (curLiteral.length() > 0)
               {
                  exprVector.addElement(new Literal(curLiteral.toString()));
                  curLiteral = new StringBuffer();
               }
               curState = ST_OBJECT;
            }
            continue;
         }
         if (curState == ST_OBJECT)
         {
            if(isNumber(curChar))
            { 
               curExpression.append(curChar);
            }
            else if(curChar == '*')
            {
               curExpression.append(curChar);
            }
            else 
            {
               exprVector.addElement(new Expression(curExpression.toString()));
               curExpression = new StringBuffer();
               curLiteral.append(curChar);
               curState = ST_LITERAL;
            }
            continue;
         }
         
      }// for each character
      return exprVector;
   }// end of function
   private boolean isNumber(int inChar)
   {
      return (inChar >= '0' && inChar <= '9') ? true:false;
   }
} 


interface BasePart
{
   String evaluate(IEvaluator eval);
}
class Literal implements BasePart
{
   private String m_self = null;
   Literal(String literal)
   {
      m_self = literal;
   }
   public String evaluate(IEvaluator eval)
   {
      return m_self;
   }
}
class Expression implements BasePart
{
   String m_object = null;
   String function = null;
   Vector params = null; // set of expressions
   
   public Expression(String object)
   {
      m_object = object;
   }
   public String evaluate(IEvaluator eval)
   {
      return eval.evaluate(m_object);
   }
}  


