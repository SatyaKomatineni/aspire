package com.ai.generictransforms;

import com.ai.common.TransformException;
import java.io.*;
import javax.servlet.http.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import com.ai.htmlgen.*;

/**
 * @deprecated directly implement IGenericTransform and dont implement ICreator
 */
public abstract class AGenericTransform implements IGenericTransform, ICreator
{

    public abstract void transform(ihds data, PrintWriter out) throws TransformException;
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
    {
        return this;
    }
}
