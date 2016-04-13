MicroService README

Build:

	using maven, "mvn clean package"

Start:

	java -jar target/dropwizard-microservice-0.0.1-SNAPSHOT.jar server dropwizard-example.yml

	With Debug:

	java -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y,suspend=n -jar target/dropwizard-microservice-0.0.1-SNAPSHOT.jar server dropwizard-example.yml

Build and Start:

	mvn clean package && java -jar target/dropwizard-microservice-0.0.1-SNAPSHOT.jar server dropwizard-example.yml

Usage:
