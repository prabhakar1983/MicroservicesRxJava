package com.example.helloworld;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;

import org.glassfish.jersey.client.JerseyClientBuilder;

import com.example.helloworld.resources.FamilyTreeResource;


public class HelloworldApplication extends Application<HelloworldConfiguration> {
	
    public static void main(String[] args) throws Exception {
        new HelloworldApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world applicaiton";
    }

    @Override
    public void initialize(Bootstrap<HelloworldConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(HelloworldConfiguration configuration, Environment environment) {
    	final Client httpClient = new JerseyClientBuilder().build();
    	
    	final FamilyTreeResource resource = new FamilyTreeResource(configuration.getUserName(), configuration.getPassword(), httpClient);
    	
    	environment.jersey().register(resource);
    }

}