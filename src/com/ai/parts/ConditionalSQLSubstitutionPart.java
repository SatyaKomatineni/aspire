package com.ai.parts;

import com.ai.application.interfaces.*;
import com.ai.application.utils.AppObjects;
import com.ai.common.*;

import java.io.*;
import java.util.Map;
/**
 * Based on ConditionalSubstitutionPart
 * 
 * Notes
 * *********
 * This uses general arg subst by default
 * Use a derived class if you need a specialized secure
 * substitutor that can validate input.
 * 
 * @See ConditionalSubstitutionPart
 * 
 * 12/4/15
 * *************
 * Try using ConditionalSQLSubstitutionPart instead of the following
 * @see ConditionalSubstitutionPart
 * 
 */

public class ConditionalSQLSubstitutionPart extends ConditionalSubstitutionPart
{
    /**
     * Use SQLSubstitution
     * @param encodedString
     * @param arguments
     * @return
     */
    @Override
    protected String substitute(String encodedString, IDictionary arguments)
    {
    	return SubstitutorUtils.sqlSubstitute(encodedString,arguments);
    }
}//eof-class

