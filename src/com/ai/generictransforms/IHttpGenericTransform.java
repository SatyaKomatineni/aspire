package com.ai.generictransforms;

import com.ai.htmlgen.*;
import com.ai.common.TransformException;
import java.io.*;
import javax.servlet.http.*;

/**
 * Adds the ability to set headers for a http page
 * @see IFormHandlerTransform
 * @see ihds
 */

public interface IHttpGenericTransform extends IGenericTransform
{
    public String getHeaders(HttpServletRequest request);
}