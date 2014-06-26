package au.edu.unimelb.plantcell.servers.mascot.html;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPException;

import au.edu.unimelb.plantcell.servers.mascot.core.v2.SearchType;
 
/**
 * This utility class provides an abstraction layer for sending multipart HTTP
 * POST requests to a web server. Refer to http://www.codejava.net/java-se/networking/upload-files-by-sending-multipart-request-programmatically
 * 
 * @author www.codejava.net
 *
 */
public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private int count;
    
    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public MultipartUtility(String requestURL, String charset)
            throws IOException {
        this.charset = charset;
         
        // creates a unique boundary based on time stamp
        boundary = "-----------------------------" + System.currentTimeMillis();
         
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "mascotee");
        httpConn.setReadTimeout(4 * 60 * 60 * 1000);		// four hours in milliseconds: need this for mascot searches where they can take a very long time...
        //httpConn.setRequestProperty("Test", "Bonjour");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
        count = 0;
    }
 
    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
    	if (name == null || value == null) {
    		return;
    	}
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
        count++;
    }
 
    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addDataFile(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: application/octet-stream")
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
        count++;
    }
    
    public void addDataURL(final String fieldName, final URL uploadURL) throws IOException {
    	String fileName = uploadURL.getFile();
    	if (fileName.lastIndexOf('/') >= 0) {
    		fileName = fileName.substring(fileName.lastIndexOf('/')+1);
    	}
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: application/octet-stream"
              )
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        InputStream is = uploadURL.openStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = is.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        is.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
        count++;
    }
 
    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }
     
    /**
     * Completes the request and receives response from the server.
     * @param logger 
     *
     * @throws FailedJobException if form submission fails or some other problem encountered
     * @return returns the "dat file number" eg. F0003457.dat for caller use
     */
    public String finish(final Logger logger) throws FailedJobException { 
    	assert(logger != null);
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
 
        BufferedReader reader = null;
        try {
        	// checks server's status code first
	        int status = httpConn.getResponseCode();
	        if (status == HttpURLConnection.HTTP_OK) {
	        	InputStream is = httpConn.getInputStream();
	            reader = new BufferedReader(new InputStreamReader(is));
	            String line = null;
	            int another_n = 0;
	            while ((line = reader.readLine()) != null) {
	            	String lc = line.toLowerCase();
	                if (lc.indexOf("error") >= 0 ||
	                		lc.indexOf("fail") >= 0 || lc.indexOf("problem") >= 0) {
	                	throw new FailedJobException("Job failed: "+line);
	                }
	                if (lc.indexOf("warning") >= 0) {
	                	logger.warning(line);
	                	another_n = 2;		// and log the following two lines as well (in case a multi-line warning)
	                	// but continue to process the line anyway...
	                } else if (another_n > 0) {
	                	another_n--;
	                	logger.warning(line);
	                	// and continue to process the line anyway...
	                }
	                if (lc.indexOf("master_results.pl") >= 0 && 
	                		lc.indexOf("search report") >= 0) {
	                	Pattern p = Pattern.compile("file=.*?(F\\d+\\.dat)\"");
	                	Matcher m = p.matcher(line);
	                	if (m.find()) {
	                		return m.group(1);
	                	} else {
	                		throw new FailedJobException("Could not identify DAT file: "+line);
	                	}
	                }
	            }
	            throw new FailedJobException("No results URL -- data not available!");
	        } else {
	            throw new FailedJobException("Server returned non-OK status: " + status);
	        }
        } catch (SocketTimeoutException ste) {
        	throw new FailedJobException("No update from mascot server in four hours... giving up: "+ste.getMessage());
        } catch (IOException ioe) {
        	throw new FailedJobException(ioe);
        } finally {
        	try {
            	httpConn.disconnect();
        		if (reader != null) {
        			reader.close();
        		}
			} catch (IOException e) {
				throw new FailedJobException(e);
			}
        }
    }

    /**
     * Throws an exception if the wrong number of parameters have been put onto the form of the specified type.
     * A runtime test to verify that all is well before submission takes place.
     * 
     * @throws SOAPException 
     */
	public void validateFormParameterCount(SearchType st) throws SOAPException {
		switch (st) {
		case MSMS:
			if (count < 36) {	// MS/MS Ion Searches pass 36 parameters + data file (for v2 mascot at least)
				throw new SOAPException("Too few form parameters for MS/MS search! got "+count);
			}
			break;
		case PMF:
			if (count < 31) {
				throw new SOAPException("Wrong # of parameters for PMF search! got "+count+" expected at least 31");
			}
			break;
		case SEQ_QUERY:
			break;
		}
	}

	public void addFormFieldList(String formFieldName, List<String> list) throws SOAPException {
		if (formFieldName == null || list == null) {
			throw new SOAPException("List of modifications may be zero, but not null!");
		}
		for (String mod : list) {
			addFormField(formFieldName, mod);
		}
	}
}