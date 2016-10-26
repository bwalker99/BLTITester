# BLTITester
How to for LTI Consumers and Providers

This is a java based project the implements both an LTI Tool Provider and Tool Consumer. 
You can use the code to add to your java projects and the examples and testing of any kind of LTI connection. 

### To deploy:
1. Build from antfile. This will create an blticomsumer.war and bltiprovider.war. 
2. Install on any servlet container (ie Tomcat). 

To test on localhost, go to http://localthost:8080/blticonsumer/start.html and follow the instructions. 
The default provider is on the same host. There are other instructions on this page as well. 

Special thanks to Stephen Vickers as IMSGlobal (and elsewhere) for his help in developing this project. 
Indeed, this is based on his tools that do the same thing (albiet with much more sophistication) in PHP. 

See: lti.tools/test/tp.php and lti.tools/test/tp.php

Other references: 
https://imsglobal.org/learning-tools-interoperability-sample-code

This code is available at: github.com/bwalker99/BLTITester  
