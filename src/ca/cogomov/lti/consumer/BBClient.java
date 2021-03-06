package ca.cogomov.lti.consumer;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import blackboard.blti.message.*;
import blackboard.blti.consumer.*;

/**
 * A stub java based LTI Consumer. This can be used to write your Tool Consumer
 * 
 * This application uses the Blackboard BLTI OAuth library
 *  
 * @author Bob Walker bwalker99@gmail.com
 *
 */
public class BBClient  extends HttpServlet {
	 
	// for testing only
	private static String DEFAULTSECRET = "mysecret123";  
	private static String DEFAULTLAUNCHSITE = "http://localhost:8080/bltiprovider/login";
	private static String DEFAULTUSER = "student1";
	private static String DEFAULTNAME = "Test User One";
	private static String DEFAULTCONSUMERKEY = "BLTTesterKey";
	
    public static final String BASICLTI_SUBMIT = "ext_basiclti_submit";
	
	public void doGet (HttpServletRequest request, HttpServletResponse response) 
		    throws ServletException, IOException  {
		    postClient(request, response);
		  }

     public void doPost (HttpServletRequest request, HttpServletResponse response) 
		    throws ServletException, IOException  {
		    postClient(request, response);
		  }
		  
	 private void postClient(HttpServletRequest request, HttpServletResponse response) {
		 boolean debug = false;
		 String launchsite = request.getParameter("launchsite");
		 if (launchsite == null || launchsite.length() == 0)
			 launchsite = DEFAULTLAUNCHSITE;
			  		
		 String mysecret = request.getParameter("mysecret");
		 if (mysecret == null || mysecret.length() == 0)
			 mysecret = DEFAULTSECRET;
		 
		 String userid = request.getParameter("user_id");
		 if (userid == null || userid.length() == 0) 
			 userid = DEFAULTUSER;
		 
		 String fullname = request.getParameter("full_name");
		 if (fullname == null || fullname.length() == 0) 
			 fullname = DEFAULTNAME;
		 		 
		 String consumerkey = request.getParameter("consumerkey");
		 if (consumerkey == null || consumerkey.length() == 0) 
			 consumerkey = DEFAULTCONSUMERKEY;

		 String temp = request.getParameter("debug");
		 if (temp != null && temp.equalsIgnoreCase("selected")) 
			 debug = true;
		 		 
	    BLTIMessage msg = new BLTIMessage(consumerkey);
	    msg.getResourceLink().setId( "testResourceId" );
	    msg.getResourceLink().setDescription("Testing LTI with Blackboard library");
	    msg.getUser().setId(userid);
	    msg.getUser().setLisSourcedId(userid);
	    msg.getUser().setFullName( fullname );
	    msg.getUser().addRole( new Role( "Learner" ) );
			    
	    msg.getContext().setType("CourseOffering");
	    msg.getToolConsumerInfo().setDescription("BLTIConsumerTest");
	    msg.getToolConsumerInfo().setEmail("tester@tester.ca");
	    msg.getToolConsumerInfo().setUrl("http://localhost:8080/blticonsumer");
	    msg.getToolConsumerInfo().setName("BLTIConsumerTester");
	    msg.getToolConsumerInfo().setGuid("0a6fd1b7-1258-48e3-ad41-7a0249aeb83a");
			    
	    msg.getLaunchPresentation().setDocumentTarget("window");
	    msg.getLaunchPresentation().setLocale("en_US");

	    temp = request.getRequestURL().toString();
	    temp = temp.substring(0,temp.lastIndexOf("/")); 	    	    
	    msg.getLaunchPresentation().setReturnUrl(temp + "/start.html");
			    
	    msg.getContext().setId("99dd04aa5b5e4514815d7122959bc6aa");
	    
	   temp = request.getParameter("custom_name");
	    if (temp != null && temp.length() > 0) { 
	    	String temp2 = request.getParameter("custom_value");
	    	if (temp2 != null && temp2.length() > 0) {
	    	    Map<String,String> cparams = msg.getCustomParameters();
	    	    cparams.put(temp, temp2);
	    	}
	    		
	    }
	    	    
			    
	    BLTIConsumer consumer = new BLTIConsumer( "POST", launchsite,msg );
	    consumer.sign(mysecret);
	    List<Map.Entry<String, String>> launchParams = consumer.getParameters();
			    
		String output = null;
		Map<String,String> tempmap = new TreeMap<String,String>();
		for (Map.Entry<String,String> e : launchParams) { 
			// Remove CR LF from entries. Sensitive on .Net Tool Providers 	
				tempmap.put(e.getKey(),e.getValue().replace("\n", "").replace("\r", ""));
		}
			output = postLaunchHTML(tempmap, launchsite, debug);	

		try {
			PrintWriter out = response.getWriter();
			out.println(output);
		}
		catch (Exception e) {
				e.printStackTrace();
		}

			// showStuff(request);    
	 }
		  

		  /** 
		   * Handy. Shows a lot of the servlet objects available
		   */
		  private void showStuff(HttpServletRequest request) {
		  	System.out.println(this.getClass().getName() +":getContextPath=   " + request.getContextPath());
		  	System.out.println(this.getClass().getName() +":getPathInfo=      " + request.getPathInfo());
		  	System.out.println(this.getClass().getName() +":getPathTranslated=" + request.getPathTranslated());
		  	System.out.println(this.getClass().getName() +":getQueryString=   " + request.getQueryString());
		  	System.out.println(this.getClass().getName() +":getRequestURI=    " + request.getRequestURI());
		  	System.out.println(this.getClass().getName() +":getRequestURL=    " + request.getRequestURL());	
		  	System.out.println(this.getClass().getName() +":getServletPath=   " + request.getServletPath());
		    System.out.println(this.getClass().getName() +"Parameters:");
		    java.util.Enumeration E = request.getParameterNames();
		    while (E.hasMoreElements()) { 
		      String param = (String)E.nextElement();
		      System.out.println("  " + param + "=" + request.getParameter(param));
		      }
		  }
		  
   /**
     * Copied from imsglobal code.
     *  
     * Create the HTML to render a POST form and then automatically submit it.
     * Make sure to call {@link #cleanupProperties(Properties)} before signing.
     *
     * @param cleanProperties Assumes you have called
     * {@link #cleanupProperties(Properties)} beforehand.
     * @param endpoint The LTI launch url.
     * @param debug Useful for viewing the HTML before posting to end point.
     * @return the HTML ready for IFRAME src = inclusion.
     */
		    public static String postLaunchHTML(
		            final Map<String, String> cleanProperties, String endpoint, boolean debug) {
		        if (cleanProperties == null || cleanProperties.isEmpty()) {
		            throw new IllegalArgumentException(
		                    "cleanProperties == null || cleanProperties.isEmpty()");
		        }
		        if (endpoint == null) {
		            throw new IllegalArgumentException("endpoint == null");
		        }
		        Map<String, String> newMap = null;
		        if (debug) {
		            // sort the properties for readability
		            newMap = new TreeMap<String, String>(cleanProperties);
		        } else {
		            newMap = cleanProperties;
		        }
		        StringBuilder text = new StringBuilder();
		        // paint form
		        text.append("<div id=\"ltiLaunchFormSubmitArea\">\n");
		        text.append("<form action=\"");
		        text.append(endpoint);
		        text.append("\" name=\"ltiLaunchForm\" id=\"ltiLaunchForm\" method=\"post\" ");
		        text.append(" encType=\"application/x-www-form-urlencoded\" accept-charset=\"utf-8\">\n");
		        
		        for (Entry<String, String> entry : newMap.entrySet()) {
		            String key = entry.getKey();
		            String value = entry.getValue();
		            if (value == null) {
		                continue;
		            }
		            if (key.equals(BASICLTI_SUBMIT)) {
		                text.append("<input type=\"submit\" name=\"");
		            } else {
		                text.append("<input type=\"hidden\" name=\"");
		            }
		            text.append(key);
		            text.append("\" value=\"");
		            text.append(value);
		            text.append("\"/>\n");
		        }
		        text.append("</form>\n");
		        text.append("</div>\n");		        
	        
		        // Paint the auto-pop up if we are transitioning from https: to http:
		        // and are not already the top frame...
		        text.append("<script type=\"text/javascript\">\n");
		        text.append("if (window.top!=window.self) {\n");
		        text.append("  theform = document.getElementById('ltiLaunchForm');\n");
		        text.append("  if ( theform && theform.action ) {\n");
		        text.append("   formAction = theform.action;\n");
		        text.append("   ourUrl = window.location.href;\n");
		        text.append("   if ( formAction.indexOf('http://') == 0 && ourUrl.indexOf('https://') == 0 ) {\n");
		        text.append("      theform.target = '_blank';\n");
		        text.append("      window.console && console.log('Launching http from https in new window!');\n");
		        text.append("    }\n");
		        text.append("  }\n");
		        text.append("}\n");
		        text.append("</script>\n");
		               
		        // paint debug output
		        if (debug) {
		            text.append("<pre>\n");
		            text.append("<b>BasicLTI Endpoint</b>\n");
		            text.append(endpoint);
		            text.append("\n\n");
		            text.append("<b>BasicLTI Parameters:</b>\n");
		            for (Entry<String, String> entry : newMap.entrySet()) {
		                String key = entry.getKey();
		                String value = entry.getValue();
		                if (value == null) {
		                    continue;
		                }
		                text.append(key);
		                text.append("=");
		                text.append(value);
		                text.append("\n");
		            }
		            text.append("</pre>\n");
		        
		        } else {
	            // paint auto submit script
		        	text.append(" <script language=\"javascript\"> \n"	            		
                            + "	document.getElementById(\"ltiLaunchFormSubmitArea\").style.display = \"none\";\n"                                                  
                            + "	document.ltiLaunchForm.submit(); \n" + " </script> \n");
	        }		        
		        		        
		        String htmltext = text.toString();        
		        return htmltext;
		    }

	}

