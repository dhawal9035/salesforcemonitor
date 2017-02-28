# salesforcemonitor
1) Clone the repository using the following link: https://github.com/dhawal9035/salesforcemonitor.git 

	Import the project into your IDE. Spring STS or Eclipse.

2) The sql file for the database has also been provided. So import the sql file onto MySQL Workbench or the tool that you are using.
   Change the username and password as per what you have in your sql server. I have used root/root as user/password for my server. Please go to application.properties
   file to make the necessary changes
   
3) Once the database and the application is setup, run the App.java file as Java Application.

4) Go to http://localhost:8080/index.html

Few important things to note:

I have a data.json file in the project so that it is possible to check if the notifications are being sent or not.
The code that directly takes the API data is also there. 

MainService.java	contains the location to the data.json file. Please change it to point to your local directory. Also there is a code for accessing the API
if you want to do that. Comments are written on which lines to comment and uncomment.
InstanceModel[] instances = (InstanceModel[]) mapper.readValue(new File("C:/Users/Dhawal/git/salesforcemonitor/src/main/resources/data.json"), InstanceModel[].class);
