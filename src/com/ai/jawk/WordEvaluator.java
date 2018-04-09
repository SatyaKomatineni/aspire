package com.ai.jawk;
import java.util.*;
import com.ai.common.*;

public class WordEvaluator implements IEvaluator
{
   private Vector m_wordVector = null;
   private String m_inString = null;
   public WordEvaluator(String inString)
   {
      m_wordVector = Tokenizer.tokenize(inString," \t");
      m_inString = inString;
   }
   public String evaluate(String key)
   {
      if (key.equals("*"))
      {
         return m_inString;
      }
      int index = Integer.parseInt(key);
      return (String)m_wordVector.elementAt(index-1);
   }
}
