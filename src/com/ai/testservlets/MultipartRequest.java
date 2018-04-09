/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
// Copyright (C) 1998 by Jason Hunter <jhunter@acm.org>.  All rights reserved.
// Use of this class is limited.  Please see the LICENSE for more information.

package com.ai.testservlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.ai.servlets.UploadedFile;
import com.ai.application.utils.*;

/** 
 * A utility class to handle <tt>multipart/form-data</tt> requests,
 * the kind of requests that support file uploads.  This class can 
 * receive arbitrarily large files (up to an artificial limit you can set),
 * and fairly efficiently too.  
 * It cannot handle nested data (multipart content within multipart content)
 * or internationalized content (such as non Latin-1 filenames).
 * <p>
 * It's used like this:
 * <blockquote><pre>
 * MultipartRequest multi = new MultipartRequest(req, ".");
 * &nbsp;
 * out.println("Params:");
 * Enumeration params = multi.getParameterNames();
 * while (params.hasMoreElements()) {
 *   String name = (String)params.nextElement();
 *   String value = multi.getParameter(name);
 *   out.println(name + " = " + value);
 * }
 * out.println();
 * &nbsp;
 * out.println("Files:");
 * Enumeration files = multi.getFileNames();
 * while (files.hasMoreElements()) {
 *   String name = (String)files.nextElement();
 *   String filename = multi.getFilesystemName(name);
 *   String type = multi.getContentType(name);
 *   File f = multi.getFile(name);
 *   out.println("name: " + name);
 *   out.println("filename: " + filename);
 *   out.println("type: " + type);
 *   if (f != null) {
 *     out.println("f.toString(): " + f.toString());
 *     out.println("f.getName(): " + f.getName());
 *     out.println("f.exists(): " + f.exists());
 *     out.println("f.length(): " + f.length());
 *     out.println();
 *   }
 * }
 * </pre></blockquote>
 *
 * A client can upload files using an HTML form with the following structure.
 * Note that not all browsers support file uploads.
 * <blockquote><pre>
 * &lt;FORM ACTION="/servlet/Handler" METHOD=POST
 *          ENCTYPE="multipart/form-data"&gt;
 * What is your name? &lt;INPUT TYPE=TEXT NAME=submitter&gt; &lt;BR&gt;
 * Which file to upload? &lt;INPUT TYPE=FILE NAME=file&gt; &lt;BR&gt;
 * &lt;INPUT TYPE=SUBMIT&GT;
 * &lt;/FORM&gt;
 * </pre></blockquote>
 * <p>
 * The full file upload specification is contained in experimental RFC 1867,
 * available at <a href="http://www.ietf.org/rfc/rfc1867.txt">
 * http://www.ietf.org/rfc/rfc1867.txt</a>.
 *
 * @author <b>Jason Hunter</b>, Copyright &#169; 1998-1999
 * @version 1.5, 00/02/04, added auto MacBinary decoding for IE on Mac
 * @version 1.4, 00/01/05, added getParameterValues(),
 *                         WebSphere 2.x getContentType() workaround,
 *                         stopped writing empty "unknown" file
 * @version 1.3, 99/12/28, IE4 on Win98 lastIndexOf("boundary=") workaround
 * @version 1.2, 99/12/20, IE4 on Mac readNextPart() workaround
 * @version 1.1, 99/01/15, JSDK readLine() bug workaround
 * @version 1.0, 98/09/18
 */
public class MultipartRequest {

  private static final int DEFAULT_MAX_POST_SIZE = 1024 * 1024;  // 1 Meg
  private static final String NO_FILE = "unknown";

  private HttpServletRequest req;
  private File dir;
  private int maxSize;
  private IMultipartRequestListner m_listner;
  
  private Hashtable parameters = new Hashtable();  // name - Vector of values
  private Hashtable files = new Hashtable();       // name - UploadedFile

  /**
   * Constructs a new MultipartRequest to handle the specified request, 
   * saving any uploaded files to the given directory, and limiting the 
   * upload size to 1 Megabyte.  If the content is too large, an
   * IOException is thrown.  This constructor actually parses the 
   * <tt>multipart/form-data</tt> and throws an IOException if there's any 
   * problem reading or parsing the request.
   *
   * @param request the servlet request
   * @param saveDirectory the directory in which to save any uploaded files
   * @exception IOException if the uploaded content is larger than 1 Megabyte
   * or there's a problem reading or parsing the request
   */
  public MultipartRequest(HttpServletRequest request,
                          String saveDirectory
                          ,IMultipartRequestListner listner) throws IOException {
    this(request, saveDirectory, DEFAULT_MAX_POST_SIZE,listner);
  }

  /**
   * Constructs a new MultipartRequest to handle the specified request, 
   * saving any uploaded files to the given directory, and limiting the 
   * upload size to the specified length.  If the content is too large, an 
   * IOException is thrown.  This constructor actually parses the 
   * <tt>multipart/form-data</tt> and throws an IOException if there's any 
   * problem reading or parsing the request.
   *
   * @param request the servlet request
   * @param saveDirectory the directory in which to save any uploaded files
   * @param maxPostSize the maximum size of the POST content
   * @exception IOException if the uploaded content is larger than 
   * <tt>maxPostSize</tt> or there's a problem reading or parsing the request
   */
  public MultipartRequest(HttpServletRequest request,
                          String saveDirectory,
                          int maxPostSize
                          ,IMultipartRequestListner listner) throws IOException {
    // Sanity check values
    if (request == null)
      throw new IllegalArgumentException("request cannot be null");
    if (saveDirectory == null)
      throw new IllegalArgumentException("saveDirectory cannot be null");
    if (maxPostSize <= 0) {
      throw new IllegalArgumentException("maxPostSize must be positive");
    }
    if (listner != null)
    {
      m_listner = listner;
    }
    else
    {
      // no listner available, use the default
      m_listner = new DefaultListner();
    }
    // Save the request, dir, and max size
    req = request;
    dir = new File(saveDirectory);
    maxSize = maxPostSize;

    // Check saveDirectory is truly a directory
    if (!dir.isDirectory())
      throw new IllegalArgumentException("Not a directory: " + saveDirectory);

    // Check saveDirectory is writable
    if (!dir.canWrite())
      throw new IllegalArgumentException("Not writable: " + saveDirectory);

    // Now parse the request saving data to "parameters" and "files";
    // write the file contents to the saveDirectory
    readRequest();
  }

  /**
   * Returns the names of all the parameters as an Enumeration of 
   * Strings.  It returns an empty Enumeration if there are no parameters.
   *
   * @return the names of all the parameters as an Enumeration of Strings
   */
  public Enumeration getParameterNames() {
    return parameters.keys();
  }

  /**
   * Returns the names of all the uploaded files as an Enumeration of 
   * Strings.  It returns an empty Enumeration if there are no uploaded 
   * files.  Each file name is the name specified by the form, not by 
   * the user.
   *
   * @return the names of all the uploaded files as an Enumeration of Strings
   */
  public Enumeration getFileNames() {
    return files.keys();
  }

  /**
   * Returns the value of the named parameter as a String, or null if 
   * the parameter was not sent or was sent without a value.  The value 
   * is guaranteed to be in its normal, decoded form.  If the parameter 
   * has multiple values, only the last one is returned (for backward 
   * compatibility).  For parameters with multiple values, it's possible
   * the last "value" may be null.
   *
   * @param name the parameter name
   * @return the parameter value
   */
  public String getParameter(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String value = (String)values.elementAt(values.size() - 1);
      return value;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the values of the named parameter as a String array, or null if 
   * the parameter was not sent.  The array has one entry for each parameter 
   * field sent.  If any field was sent without a value that entry is stored 
   * in the array as a null.  The values are guaranteed to be in their 
   * normal, decoded form.  A single value is returned as a one-element array.
   *
   * @param name the parameter name
   * @return the parameter values
   */
  public String[] getParameterValues(String name) {
    try {
      Vector values = (Vector)parameters.get(name);
      if (values == null || values.size() == 0) {
        return null;
      }
      String[] valuesArray = new String[values.size()];
      values.copyInto(valuesArray);
      return valuesArray;
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the filesystem name of the specified file, or null if the 
   * file was not included in the upload.  A filesystem name is the name 
   * specified by the user.  It is also the name under which the file is 
   * actually saved.
   *
   * @param name the file name
   * @return the filesystem name of the file
   */
  public String getFilesystemName(String name) {
    try {
      UploadedFile file = (UploadedFile)files.get(name);
      return file.getFilesystemName();  // may be null
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the content type of the specified file (as supplied by the 
   * client browser), or null if the file was not included in the upload.
   *
   * @param name the file name
   * @return the content type of the file
   */
  public String getContentType(String name) {
    try {
      UploadedFile file = (UploadedFile)files.get(name);
      return file.getContentType();  // may be null
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns a File object for the specified file saved on the server's 
   * filesystem, or null if the file was not included in the upload.
   *
   * @param name the file name
   * @return a File object for the named file
   */
  public File getFile(String name) {
    try {
      UploadedFile file = (UploadedFile)files.get(name);
      return file.getFile();  // may be null
    }
    catch (Exception e) {
      return null;
    }
  }
  public UploadedFile   getUploadedFile(String name)
  {
      return (UploadedFile)files.get(name);
  }
  /**
   * The workhorse method that actually parses the request.  A subclass 
   * can override this method for a better optimized or differently
   * behaved implementation.
   *
   * @exception IOException if the uploaded content is larger than 
   * <tt>maxSize</tt> or there's a problem parsing the request
   */
  protected void readRequest() throws IOException {
    // Check the content type to make sure it's "multipart/form-data"
    // Access header directly to work around WebSphere 2.x oddity
    String type = req.getHeader("Content-Type");
    if (type == null || 
        !type.toLowerCase().startsWith("multipart/form-data")) {
      throw new IOException("Posted content type isn't multipart/form-data");
    }

    // Check the content length to prevent denial of service attacks
    int length = req.getContentLength();
    if (length > maxSize) {
      throw new IOException("Posted content length of " + length + 
                            " exceeds limit of " + maxSize);
    }

    // Get the boundary string; it's included in the content type.
    // Should look something like "------------------------12012133613061"
    String boundary = extractBoundary(type);
    if (boundary == null) {
      throw new IOException("Separation boundary was not specified");
    }
    AppObjects.log("fi: boundary :" + boundary );
    // Construct the special input stream we'll read from
    MultipartInputStreamHandler in =
      new MultipartInputStreamHandler(req.getInputStream(), length);

    // Read the first line, should be the first boundary
    String line = in.readLine();
    if (line == null) {
      throw new IOException("Corrupt form data: premature ending");
    }

    // Verify that the line is the boundary
    if (!line.startsWith(boundary)) {
      throw new IOException("Corrupt form data: no leading boundary");
    }

    // Now that we're just beyond the first boundary, loop over each part
    boolean done = false;
    while (!done) {
      done = readNextPart(in, boundary);
    }
  }

  /**
   * A utility method that reads an individual part.  Dispatches to 
   * readParameter() and readAndSaveFile() to do the actual work.  A 
   * subclass can override this method for a better optimized or 
   * differently behaved implementation.
   * 
   * @param in the stream from which to read the part
   * @param boundary the boundary separating parts
   * @return a flag indicating whether this is the last part
   * @exception IOException if there's a problem reading or parsing the
   * request
   *
   * @see readParameter
   * @see readAndSaveFile
   */
  protected boolean readNextPart(MultipartInputStreamHandler in,
                                 String boundary) throws IOException {
    // Read the first line, should look like this:
    // content-disposition: form-data; name="field1"; filename="file1.txt"
    String line = in.readLine();
    if (line == null) {
      // No parts left, we're done
      return true;
    }
    else if (line.length() == 0) {
      // IE4 on Mac sends an empty line at the end; treat that as the end.
      // Thanks to Daniel Lemire and Henri Tourigny for this fix.
      return true;
    }
   AppObjects.log(line);
    // Parse the content-disposition line
    String[] dispInfo = extractDispositionInfo(line);
    String disposition = dispInfo[0];
    String name = dispInfo[1];
    String filename = dispInfo[2];

    // Now onto the next line.  This will either be empty 
    // or contain a Content-Type and then an empty line.
    line = in.readLine();
    if (line == null) {
      // No parts left, we're done
      return true;
    }

    // Get the content type, or null if none specified
    String contentType = extractContentType(line);
    if (contentType != null) {
      // Eat the empty line
      line = in.readLine();
      if (line == null || line.length() > 0) {  // line should be empty
        throw new 
          IOException("Malformed line after content type: " + line);
      }
    }
    else {
      // Assume a default content type
      contentType = "application/octet-stream";
    }

    // Now, finally, we read the content (end after reading the boundary)
    if (filename == null) {
      // This is a parameter, add it to the vector of values
      String value = readParameter(in, boundary);
      if (value.equals("")) {
        value = null;  // treat empty strings like nulls
      }
      m_listner.newParameter(name,value);
      Vector existingValues = (Vector)parameters.get(name);
      if (existingValues == null) {
        existingValues = new Vector();
        parameters.put(name, existingValues);
      }
      existingValues.addElement(value);
    }
    else {
      // This is a file
      String suggestedFilename = m_listner.suggestAFilename(filename);
      readAndSaveFile(in, boundary, suggestedFilename, contentType);
      if (suggestedFilename.equals(NO_FILE)) {
        files.put(suggestedFilename, new UploadedFile(null, null, null,null));
      }
      else {
        files.put(name,
          new UploadedFile(dir.toString(), suggestedFilename, contentType, filename));
      }
    }
    return false;  // there's more to read
  }

  /**
   * A utility method that reads a single part of the multipart request 
   * that represents a parameter.  A subclass can override this method 
   * for a better optimized or differently behaved implementation.
   *
   * @param in the stream from which to read the parameter information
   * @param boundary the boundary signifying the end of this part
   * @return the parameter value
   * @exception IOException if there's a problem reading or parsing the 
   * request
   */
  protected String readParameter(MultipartInputStreamHandler in,
                                 String boundary) throws IOException {
    StringBuffer sbuf = new StringBuffer();
    String line;

    while ((line = in.readLine()) != null) {
      if (line.startsWith(boundary)) break;
      sbuf.append(line + "\r\n");  // add the \r\n in case there are many lines
    }

    if (sbuf.length() == 0) {
      return null;  // nothing read
    }

    sbuf.setLength(sbuf.length() - 2);  // cut off the last line's \r\n
    return sbuf.toString();  // no URL decoding needed
  }

  /**
   * A utility method that reads a single part of the multipart request 
   * that represents a file, and saves the file to the given directory.
   * A subclass can override this method for a better optimized or 
   * differently behaved implementation.
   *
   * @param in the stream from which to read the file
   * @param boundary the boundary signifying the end of this part
   * @param dir the directory in which to save the uploaded file
   * @param filename the name under which to save the uploaded file
   * @exception IOException if there's a problem reading or parsing the 
   * request
   */
  protected void readAndSaveFile(MultipartInputStreamHandler in,
                                 String boundary,
                                 String filename,
                                 String contentType) throws IOException {
    OutputStream out = null;
    // A filename of NO_FILE means no file was sent, so just read to the
    // next boundary and ignore the empty contents
    if (filename.equals(NO_FILE)) {
      out = new ByteArrayOutputStream();  // write to nowhere
    }
    // A MacBinary file goes through a decoder
    else if (contentType.equals("application/x-macbinary")){
      File f = new File(dir + File.separator + filename);
      out = new MacBinaryDecoderOutputStream(
            new BufferedOutputStream(
            new FileOutputStream(f), 8 * 1024));
    }
    // A real file's contents are written to disk
    else {
      File f = new File(dir + File.separator + filename);
      out = new BufferedOutputStream(new FileOutputStream(f), 8 * 1024);
    }

    byte[] bbuf = new byte[100 * 1024];  // 100K
    int result;
    String line;

    // ServletInputStream.readLine() has the annoying habit of 
    // adding a \r\n to the end of the last line.  
    // Since we want a byte-for-byte transfer, we have to cut those chars.
    boolean rnflag = false;
    while ((result = in.readLine(bbuf, 0, bbuf.length)) != -1) {
      // Check for boundary
      if (result > 2 && bbuf[0] == '-' && bbuf[1] == '-') { // quick pre-check
        line = new String(bbuf, 0, result, "ISO-8859-1");
        if (line.startsWith(boundary)) break;
      }
      // Are we supposed to write \r\n for the last iteration?
      if (rnflag) {
        out.write('\r'); out.write('\n');
        rnflag = false;
      }
      // Write the buffer, postpone any ending \r\n
      if (result >= 2 && 
          bbuf[result - 2] == '\r' && 
          bbuf[result - 1] == '\n') {
        out.write(bbuf, 0, result - 2);  // skip the last 2 chars
        rnflag = true;  // make a note to write them on the next iteration
      }
      else {
        out.write(bbuf, 0, result);
      }
    }
    out.flush();
    out.close();
  }

  // Extracts and returns the boundary token from a line.
  //
  private String extractBoundary(String line) {
    // Use lastIndexOf() because IE 4.01 on Win98 has been known to send the
    // "boundary=" string multiple times.  Thanks to David Wall for this fix.
    int index = line.lastIndexOf("boundary=");
    if (index == -1) {
      return null;
    }
    String boundary = line.substring(index + 9);  // 9 for "boundary="

    // The real boundary is always preceeded by an extra "--"
    boundary = "--" + boundary;

    return boundary;
  }

  // Extracts and returns disposition info from a line, as a String array
  // with elements: disposition, name, filename.  Throws an IOException 
  // if the line is malformatted.
  //
  private String[] extractDispositionInfo(String line) throws IOException {
    // Return the line's data as an array: disposition, name, filename
    String[] retval = new String[3];

    // Convert the line to a lowercase string without the ending \r\n
    // Keep the original line for error messages and for variable names.
    String origline = line;
    line = origline.toLowerCase();

    // Get the content disposition, should be "form-data"
    int start = line.indexOf("content-disposition: ");
    int end = line.indexOf(";");
    if (start == -1 || end == -1) {
      throw new IOException("Content disposition corrupt: " + origline);
    }
    String disposition = line.substring(start + 21, end);
    if (!disposition.equals("form-data")) {
      throw new IOException("Invalid content disposition: " + disposition);
    }

    // Get the field name
    start = line.indexOf("name=\"", end);  // start at last semicolon
    end = line.indexOf("\"", start + 7);   // skip name=\"
    if (start == -1 || end == -1) {
      throw new IOException("Content disposition corrupt: " + origline);
    }
    String name = origline.substring(start + 6, end);

    // Get the filename, if given
    String filename = null;
    start = line.indexOf("filename=\"", end + 2);  // start after name
    end = line.indexOf("\"", start + 10);          // skip filename=\"
    if (start != -1 && end != -1) {                // note the !=
      filename = origline.substring(start + 10, end);
      // The filename may contain a full path.  Cut to just the filename.
      int slash =
        Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
      if (slash > -1) {
        filename = filename.substring(slash + 1);  // past last slash
      }
      if (filename.equals("")) filename = NO_FILE; // sanity check
    }

    // Return a String array: disposition, name, filename
    retval[0] = disposition;
    retval[1] = name;
    retval[2] = filename;
    return retval;
  }

  // Extracts and returns the content type from a line, or null if the
  // line was empty.  Throws an IOException if the line is malformatted.
  //
  private String extractContentType(String line) throws IOException {
    String contentType = null;

    // Convert the line to a lowercase string
    String origline = line;
    line = origline.toLowerCase();

    // Get the content type, if any
    if (line.startsWith("content-type")) {
      int start = line.indexOf(" ");
      if (start == -1) {
        throw new IOException("Content type corrupt: " + origline);
      }
      contentType = line.substring(start + 1);
    }
    else if (line.length() != 0) {  // no content type, so should be empty
      throw new IOException("Malformed line after disposition: " + origline);
    }

    return contentType;
  }
}


// Class to filters MacBinary files to normal files on the fly
// Optimized for speed more than readability
class MacBinaryDecoderOutputStream extends FilterOutputStream {

  int bytesFiltered = 0;
  int dataForkLength = 0;

  public MacBinaryDecoderOutputStream(OutputStream out) {
    super(out);
  }

  public void write(int b) throws IOException {
    // Bytes 83 through 86 are a long representing the data fork length
    // Check <= 86 first to short circuit early in the common case
    if (bytesFiltered <= 86 && bytesFiltered >= 83) {
      int leftShift = (86 - bytesFiltered) * 8;
      dataForkLength = dataForkLength | (b & 0xff) << leftShift;
    }
    // Bytes 128 up to (128 + dataForkLength - 1) are the data fork
    else if (bytesFiltered < (128 + dataForkLength) && bytesFiltered >= 128) {
      out.write(b);
    }
    bytesFiltered++;
  }

  public void write(byte b[]) throws IOException {
    write(b, 0, b.length);
  }

  public void write(byte b[], int off, int len) throws IOException {
    // If the write is for content past the end of the data fork, ignore
    if (bytesFiltered >= (128 + dataForkLength)) {
      bytesFiltered += len;
    }
    // If the write is entirely within the data fork, write it directly
    else if (bytesFiltered >= 128 && 
             (bytesFiltered + len) <= (128 + dataForkLength)) {
      out.write(b, off, len);
      bytesFiltered += len;
    }
    // Otherwise, do the write a byte at a time to get the logic above
    else {
      for (int i = 0 ; i < len ; i++) {
        write(b[off + i]);
      }
    }
  }
}
class DefaultListner implements IMultipartRequestListner
{
   public boolean beginProcess(HttpServletRequest request, MultipartInputStreamHandler stream)
   {
      return true;
   }
   public boolean newParameter(String name, String value )
   {
      return true;
   }
   public boolean newFile(String filename)
   {
      return true;
   }
   public String suggestAFilename(String filename )
   {
      return filename;
   }
}
