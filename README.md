# mock-gps-tracking-system
A containerised system that simulates GPS coordinate messages being sent to a database.
Solo project meant to demonstrate use of containers and microservices.

In this Java project, each maven module is a micro service to be containerised.
This project is to simulate many different services operating over a network, locally.

producer module
	Produces simulated GPS messages in JSON format 
	{"serial_number" : String
	 "time" : Timestamp
	 "longitude" : float64 
	 "latitude" : float64}
	 
writer module
	writes and updates GPS messages to postgres database
	
common module
	Common Java files
	
dev module
	Testing
	
If you need to add a microservice, you must update the following:
1. Create a new maven project, in the dev_java directory with the group_id as com.buckl3y
2. add appropriate dependencies in new pom.xml
3. create parent reference in new pom.xml
4. add module to parent pom.xml
5. add dependencies to parent.xml
6. Create Dockerfile for service
7. Add service to docker-compose.yml
	
