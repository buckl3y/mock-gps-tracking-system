# mock-gps-tracking-system
A containerised system that simulates GPS coordinate messages being sent to a database.
Solo project meant to demonstrate use of containers and microservices.

gps_producer
	Produces simulated GPS messages in JSON format 
	{"serial_number" : String	
	 "longitude" : float64 
	 "latitude" : float64}
	 
gps_writer
	writes and updates GPS messages to postgres database
	
postgres
	init postgres database

tester
	unit tests to assert functionality
	
