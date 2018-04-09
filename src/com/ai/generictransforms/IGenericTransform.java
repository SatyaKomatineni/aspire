package com.ai.generictransforms;

import com.ai.htmlgen.*;
import com.ai.common.TransformException;
import java.io.*;

/**
 * Ability to transform a hierarchical data set to another data format
 * @see IFormHandlerTransform
 * @see ihds
 */
public interface IGenericTransform
{
    public void transform(ihds data, PrintWriter out) throws TransformException;
}