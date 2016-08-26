package ca.cogomov.lti.provider;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class BBProvider extends HttpServlet {
	String mysecret = "mysecret123";
	
   public void init () throws ServletException {
		System.out.println("Initializing " + this.getClass().getName() + " secret=" + mysecret);				
	     }
	
   public void doGet (HttpServletRequest request, HttpServletResponse response) 
		       throws ServletException, IOException  {
		    doPost(request,response);
		   }

	public void doPost (HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException  {
		    	
			try {
				PrintWriter out = response.getWriter();
				response.setContentType("text/html");				    
				out.println("<html><head><title>LTI Tester</title></head><body>");
				out.println("<h3>BLTI Mock Tool Provider</h3>");
				
				LTIAuthenticate ltiauth = new LTIAuthenticate(request,mysecret,out);
				if (ltiauth.authenticate()) { 				
				    out.println("Successfully authenticated.<br/><br/>");
					out.println("UserID=" +  ltiauth.getUserid() + "<br/>");
					out.println("SourcedID=" +  ltiauth.getSourcedid() + "<br/>");
					out.println("FullName=" +  ltiauth.getFullname() + "<br/>");
				}
				else {
				    out.println("**Authentication failed. See webserver log files **<br/>");
				}
				showParams(request,out);
			
				out.println("</body></html>");
				out.close();	
			//	showStuff(request);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		private void showParams(HttpServletRequest request,PrintWriter out) {
			out.println("<hr/>Parameter List\n<table>");
			java.util.Enumeration E = request.getParameterNames();
			while (E.hasMoreElements()) { 
		      String param = (String)E.nextElement();
		      out.println("<tr><td>" + param + "</td><td>" + request.getParameter(param) + "</td></tr>");
		      }
			out.println("</table>");
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
		  
}
