package ca.cogomov.lti.consumer;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.imsglobal.lti.*;
import org.imsglobal.lti.launch.*;

import java.io.PrintWriter;

public class IMSGlobalClient  extends HttpServlet {
	
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
			  		
			String launchsite = request.getParameter("launchsite");   // required
			if (launchsite == null || launchsite.length() == 0)
				 launchsite = DEFAULTLAUNCHSITE;
					  		
			String mysecret = request.getParameter("mysecret");   // required
			 if (mysecret == null || mysecret.length() == 0)
					 mysecret = DEFAULTSECRET;
				 
			String userid = request.getParameter("user_id");
			 if (userid == null || userid.length() == 0) 
					 userid = DEFAULTUSER;
				              
             Map<String,String> parameters = new TreeMap<String,String>();
             // Required
             parameters.put(BasicLTIConstants.RESOURCE_LINK_ID, "429785226");
             parameters.put(BasicLTIConstants.LTI_VERSION,BasicLTIConstants.LTI_VERSION_1);
             parameters.put(BasicLTIConstants.LTI_MESSAGE_TYPE,BasicLTIConstants.LTI_MESSAGE_TYPE_BASICLTILAUNCHREQUEST);

             // Recommended
             parameters.put(BasicLTIConstants.USER_ID,userid);
             parameters.put(BasicLTIConstants.LIS_PERSON_SOURCEDID,userid);
             parameters.put(BasicLTIConstants.LIS_PERSON_NAME_FULL,"Tester One" );             
             
             // Optional
             /*
             parameters.put(BasicLTIConstants.RESOURCE_LINK_DESCRIPTION, "Testing LTI with IMSGlobal Library");
             parameters.put(BasicLTIConstants.ROLES,"Learner");
             parameters.put(BasicLTIConstants.CONTEXT_TYPE_COURSE_OFFERING,"CourseOffering");
             parameters.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_DESCRIPTION,"BLTIConsumerTest");
             parameters.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_CONTACT_EMAIL,"tester@test.ca");
             parameters.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_URL,"http://localhost:8080/blticonsumer");
             parameters.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_NAME,"BLTIConsumerTester");
             parameters.put(BasicLTIConstants.TOOL_CONSUMER_INSTANCE_GUID,"0a6fd1b7-1258-48e3-ad41-7a0249aeb83a");
             parameters.put(BasicLTIConstants.LAUNCH_PRESENTATION_DOCUMENT_TARGET,"window");                
             parameters.put(BasicLTIConstants.LAUNCH_PRESENTATION_LOCALE,"en_US");                
             parameters.put(BasicLTIConstants.LAUNCH_PRESENTATION_RETURN_URL,"http://localhost:8080/blticonsumer/start.html");                
             parameters.put(BasicLTIConstants.CONTEXT_ID,"99dd04aa5b5e4514815d7122959bc6aa");
   */
             // Required for IMSGlobal implementation
             parameters.put(BASICLTI_SUBMIT,"something");
             
     	    
     	    String temp = request.getParameter("custom_name");
     	    if (temp != null && temp.length() > 0) { 
     	    	String temp2 = request.getParameter("custom_value");
     	    	if (temp2 != null && temp2.length() > 0) {
     	    	    parameters.put("custom_" + temp, temp2);
     	    	}
     	    		
     	    }     	    	             	   
			
			LtiSigner ltiSigner = new LtiOauthSigner();
			
			try {
			    Map<String, String> signedParameters = ltiSigner.signParameters(parameters, "ConsumerKey", mysecret, launchsite, "POST");           
			    // oauth_signature seems to have a CR LF at the end. Remove them.  			    
				signedParameters.put("oauth_signature", signedParameters.get("oauth_signature").replace("\n","").replace("\r", "")) ;  					 
			    String output = BasicLTIUtil.postLaunchHTML(signedParameters,launchsite,false);
			    PrintWriter out = response.getWriter();
				System.out.println(output);
				out.println(output);
			}
			catch (Exception e) {
				e.printStackTrace();
			}             			
			    
		  }
		  
	}

