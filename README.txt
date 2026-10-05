Jorja Knaus A01458374

Open two command prompts.

Upload server
1. Go to the UploadServer folder
2. run
	javac *.java
3. run
	java -classpath . UploadServer
4. go to 
	http://localhost:8082/
5. Upload a file with a data and caption to view the alphabetically sorted list of uploaded files

Console app
1. Leave UploadServer running on one command promt
2. Open the other command prompt
3. run
	javac *.java
4. run
	java - classpath . Activity
5. This will send a png file with a date and caption to Upload Server.