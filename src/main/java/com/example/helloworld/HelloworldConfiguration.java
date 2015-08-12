package com.example.helloworld;

import io.dropwizard.Configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloworldConfiguration extends Configuration {
    @NotEmpty
    private String userName;

    @NotEmpty
    private String password;
    
    @JsonProperty("userName")
	public String getUserName() {
		return userName;
	}
    
    @JsonProperty("userName")
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
    @JsonProperty("password")
	public String getPassword() {
		return password;
	}
	
    @JsonProperty("password")
	public void setPassword(String password) {
		this.password = password;
	}
    
}