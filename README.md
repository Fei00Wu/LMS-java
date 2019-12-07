# A branch of LMS-java
Unit tests with JUnit5 managed by Maven. 

This project is done using Maven and IntelliJ IDE. 
Only the MainTest.java requires a running derby database. To run MainTEst.java, download and install durby ("sudo apt-get install derby-tools" for ubuntu users), and then starting the derby server in the Database folder by using "derbyctl start" (Ubuntu) or "" (Window) command. Now MainTest.java can be run from IntelliJ (and all test cases should pass).

Notice that we modified some of the code (all about scanner) in Main.java and Library.java so that we could test the menu and interaction automaticaly.  All the modified code are labeled as "Modified section" in the two java files mentioned above. 

Input files for automated console-input-required tests can be found in src/test/resources. 

A report of the results of all test cases can be found in Test Results - RunAllTest.html.




 

