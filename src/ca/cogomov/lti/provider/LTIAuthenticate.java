package ca.cogomov.lti.provider;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

/**
 * Authenticate from an LTI Tool consumer. 
 *   
 */
public class LTIAuthenticate {
  javax.servlet.http.HttpServletRequest request;
  PrintWriter output;
 
  private String mysecret = null; 
  private String userid;
  private String sourcedid;
  private String fullname;
  
  public LTIAuthenticate(HttpServletRequest request,String mysecret,PrintWriter output) { 
	  this.request = request;
	  this.mysecret = mysecret;
	  this.output = output;
  }
/**
 * The main authentication method. 
 * Checks to see if LTI authentication works for this user. 
 */
  public boolean authenticate() {
	  boolean retval = false;
	  	  
	  blackboard.blti.message.BLTIMessage msg = blackboard.blti.provider.BLTIProvider.getMessage( request );
	  String key = msg.getKey();
		  
	  if ( blackboard.blti.provider.BLTIProvider.isValid( msg, mysecret ) ) { // That's it! Authenticated.

		  userid = request.getParameter("user_id");
		  sourcedid = request.getParameter("lis_person_sourcedid");
		  sourcedid = request.getParameter("lis_person_name_full");
		  retval = true;
		  }
	  else  {
		  // output.println("**Authenticated Failed** - Check webserver logfiles for error");
	  }
     return retval;
  }
  
  public String getUserid() { return userid; }
  public String getSourcedid() { return sourcedid; }
  public String getFullname() { return fullname;  }
}
  

