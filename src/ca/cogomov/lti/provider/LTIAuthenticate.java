package ca.cogomov.lti.provider;

import javax.servlet.http.HttpServletRequest;

/**
 * Authenticate from an LTI Tool consumer.
 * Used the Blackboard BLTI library for the OAuth authentication. 
 * There are probably other OAuth libraries than can do this as well. 
 * @author Bob Walker bwalker99@gmail.com 
 *   
 */
public class LTIAuthenticate {
  javax.servlet.http.HttpServletRequest request;
 
  private String mysecret = null; 
  private String userid;
  private String sourcedid;
  private String fullname;
  
  public LTIAuthenticate(HttpServletRequest request,String mysecret) { 
	  this.request = request;
	  this.mysecret = mysecret;
  }
/**
 * The main authentication method. 
 * Checks to see if LTI authentication works for this user. 
 */
  public boolean authenticate() {
	  boolean retval = false;
	  	  
	  blackboard.blti.message.BLTIMessage msg = blackboard.blti.provider.BLTIProvider.getMessage( request );
		  
	  if ( blackboard.blti.provider.BLTIProvider.isValid( msg, mysecret ) ) { // That's it! Authenticated.

		  userid = request.getParameter("user_id");
		  sourcedid = request.getParameter("lis_person_sourcedid");
		  fullname = request.getParameter("lis_person_name_full");
		  retval = true;
		  }
     return retval;
  }
  
  public String getUserid() { return userid; }
  public String getSourcedid() { return sourcedid; }
  public String getFullname() { return fullname;  }
}
  

