package ca.cogomov.lti.consumer;


import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.imsglobal.lti.BasicLTIUtil;


import java.io.PrintWriter;

import blackboard.blti.message.*;
import blackboard.blti.consumer.*;


public class BBClient  extends HttpServlet {
	 
	private static String DEFAULTSECRET = "mysecret123"; 
	private static String DEFAULTLAUNCHSITE = "http://localhost:8080/bltiprovider/login";
	private static String DEFAULTUSER = "medstu1";
	
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
		 String launchsite = request.getParameter("launchsite");
		 if (launchsite == null || launchsite.length() == 0)
			 launchsite = DEFAULTLAUNCHSITE;
			  		
		 String mysecret = request.getParameter("mysecret");
		 if (mysecret == null || mysecret.length() == 0)
			 mysecret = DEFAULTSECRET;
		 
		 String userid = request.getParameter("user_id");
		 if (userid == null || userid.length() == 0) 
			 userid = DEFAULTUSER;
		 
	    BLTIMessage msg = new BLTIMessage( "ConsumerKey" );
	    msg.getResourceLink().setId( "testResourceId" );
	    msg.getResourceLink().setDescription("Testing LTI with Blackboard library");
	    msg.getUser().setId(userid);
	    msg.getUser().setLisSourcedId(userid);
	    msg.getUser().setFullName( "Test User One" );
	    msg.getUser().addRole( new Role( "Learner" ) );
			    
	    msg.getContext().setType("CourseOffering");
	    msg.getToolConsumerInfo().setDescription("BLTIConsumerTest");
	    msg.getToolConsumerInfo().setEmail("tester@tester.ca");
	    msg.getToolConsumerInfo().setUrl("http://localhost:8080/blticonsumer");
	    msg.getToolConsumerInfo().setName("BLTIConsumerTester");
	    msg.getToolConsumerInfo().setGuid("0a6fd1b7-1258-48e3-ad41-7a0249aeb83a");
			    
	    msg.getLaunchPresentation().setDocumentTarget("window");
	    msg.getLaunchPresentation().setLocale("en_US");
	    msg.getLaunchPresentation().setReturnUrl("http://localhost:8080/blticonsumer/start.html");
			    
	    msg.getContext().setId("99dd04aa5b5e4514815d7122959bc6aa");
			    			    
			    
	    BLTIConsumer consumer = new BLTIConsumer( "POST", launchsite,msg );
	    consumer.sign(mysecret);
	    List<Map.Entry<String, String>> launchParams = consumer.getParameters();
			    
			System.out.println("====================\nPosting to : " + launchsite);
			String output = null;
			Map<String,String> tempmap = new TreeMap<String,String>();
			for (Map.Entry<String,String> e : launchParams) { 
				tempmap.put(e.getKey(),e.getValue());
				System.out.println(e.getKey() + "=" + e.getValue());
			}
			output = postLaunchHTML(tempmap, launchsite, false);
	

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
//		        text.append(" encType=\"application/x-www-form-urlencoded\">\n");		        
		        for (Entry<String, String> entry : newMap.entrySet()) {
		            String key = entry.getKey();
		            String value = entry.getValue();
		            if (value == null) {
		                continue;
		            }
		            // This will escape the contents pretty much - at least
		            // we will be safe and not generate dangerous HTML
		     //       key = htmlspecialchars(key);
		     //       value = htmlspecialchars(value);
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
		        
		        System.out.println("===SoFar===:" + text.toString());

	        
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
		                // text.append(htmlspecialchars(key));
		                text.append(key);
		                text.append("=");
		                // text.append(htmlspecialchars(value));
		                text.append(value);
		                text.append("\n");
		            }
		            text.append("</pre>\n");
		        } else {
		            // paint auto submit script
		            text.append(" <script language=\"javascript\"> \n"
	                            + "	document.getElementById(\"ltiLaunchFormSubmitArea\").style.display = \"none\";\n"
		                            + "	nei = document.createElement('input');\n"
		                            + "	nei.setAttribute('type', 'hidden');\n"
		                            + "	nei.setAttribute('name', '"
		                            + BASICLTI_SUBMIT
		                            + "');\n"
		                            + "	nei.setAttribute('value', '"
		                            + newMap.get(BASICLTI_SUBMIT)
		                            + "');\n"
		                            + "	document.getElementById(\"ltiLaunchForm\").appendChild(nei);\n"
		                            + "	document.ltiLaunchForm.submit(); \n" + " </script> \n");
		        }

		        String htmltext = text.toString();        
		        return htmltext;
		    }

	}

