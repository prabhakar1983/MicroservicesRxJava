package com.example.helloworld.resources;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import com.codahale.metrics.annotation.Timed;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
public class FamilyTreeResource {
    private final Client httpClient;
    private final String maxcvUserName;
    private final String maxcvPassword;
    private Response returnedResponse;
    
    private static final String AUTHORIZATION = "Authorization";

    public FamilyTreeResource(String userName, String password, Client httpClient) {
        this.httpClient = httpClient;
        this.maxcvUserName = userName;
        this.maxcvPassword = password;
    }

    // http://localhost:8080/services/callSerial?duns2=001005003&duns1=200443976
    @GET
    @Path("/callSerial")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response pullOSBProductInSequeuence(@QueryParam("duns1") String duns1, @QueryParam("duns2") String duns2) {
        Response response = httpClient.target("https://maxcvservices-stg.dnb.com/rest/Authentication").request()
					        		  .header("x-dnb-user", maxcvUserName)
					        		  .header("x-dnb-pwd", maxcvPassword)
					        		  .build(HttpMethod.POST)
					        		  .invoke();
        
        if(response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
        	String maxcvAuthToken =  response.getHeaderString(AUTHORIZATION);
        	
			Func1<String, Response> osbCallFunc = new Func1<String, Response>(){
				@Override
				public Response call(String duns) {
					System.out.println("Current Thread Name:" + Thread.currentThread().getName());
					if(duns != null && duns != "")
						return httpClient.target("https://maxcvservices-stg.dnb.com/V3.1/organizations/" + duns + "/products/LNK_UPF").request()
											.header(AUTHORIZATION, maxcvAuthToken)
											.build(HttpMethod.GET)
											.invoke();
					else 
						return Response.status(Status.SERVICE_UNAVAILABLE).entity("Not Available").build();
				}
			};
			
			Func2<Response, Response, Response> mergeFunc = new Func2<Response, Response,Response>(){
				@Override
				public Response call(Response linkageProductResponse1, Response linkageProductResponse2) {
					System.out.println("Current Thread Name:" + Thread.currentThread().getName());
					
					if(linkageProductResponse1.getStatus() == Status.OK.getStatusCode() && linkageProductResponse2.getStatus() == Status.OK.getStatusCode()) {
			    		return Response.status(Status.CREATED).entity(linkageProductResponse1.getEntity()).build();
			    	} else if(linkageProductResponse1.getStatus() == Status.OK.getStatusCode()) {
			    		return Response.status(Status.CREATED).entity(linkageProductResponse1.getEntity()).build();
			    	} else if(linkageProductResponse2.getStatus() == Status.OK.getStatusCode()) {
			    		return Response.status(Status.CREATED).entity(linkageProductResponse2.getEntity()).build();
			    	}else {
			    		return returnErrorResponse();
			    	}
				}
			};
			
			Action1<? super Response> onNextFunc = new Action1<Response>() {
				@Override
				public void call(Response response) {
					returnedResponse = response;
				}
			};
			
			Observable.just(duns1, duns2)
						.map(osbCallFunc)
			        	.reduce(mergeFunc).subscribe(onNextFunc);
			
			return returnedResponse;
        } else {
            return returnErrorResponse();
        }
    }
    
 // http://localhost:8080/services/callParrallel?duns2=001005003&duns1=200443976
    @GET
    @Path("/callParrallel")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response pullOSBProductInParrallel(@QueryParam("duns1") String duns1, @QueryParam("duns2") String duns2) {
        Response response = httpClient.target("https://maxcvservices-stg.dnb.com/rest/Authentication").request()
					        		  .header("x-dnb-user", maxcvUserName)
					        		  .header("x-dnb-pwd", maxcvPassword)
					        		  .build(HttpMethod.POST)
					        		  .invoke();
        
        if(response.getStatus() == Status.NO_CONTENT.getStatusCode()) {
        	String maxcvAuthToken =  response.getHeaderString(AUTHORIZATION);
        	
			Func1<String, Response> osbCallFunc = new Func1<String, Response>(){
				@Override
				public Response call(String duns) {
					System.out.println("Current Thread Name:" + Thread.currentThread().getName());
					
					if(duns != null && duns != "")
						return httpClient.target("https://maxcvservices-stg.dnb.com/V3.1/organizations/" + duns + "/products/LNK_UPF").request()
											.header(AUTHORIZATION, maxcvAuthToken)
											.build(HttpMethod.GET)
											.invoke();
					else 
						return Response.status(Status.SERVICE_UNAVAILABLE).entity("Not Available").build();
				}
			};
			
			Func2<Response, Response, Response> mergeFunc = new Func2<Response, Response,Response>(){
				@Override
				public Response call(Response linkageProductResponse1, Response linkageProductResponse2) {
					System.out.println("Current Thread Name:" + Thread.currentThread().getName());
					
					if(linkageProductResponse1.getStatus() == Status.OK.getStatusCode() && linkageProductResponse2.getStatus() == Status.OK.getStatusCode()) {
			    		return Response.status(Status.CREATED).entity(linkageProductResponse1.getEntity()).build();
			    	} else if(linkageProductResponse1.getStatus() == Status.OK.getStatusCode()) {
			    		return Response.status(Status.CREATED).entity(linkageProductResponse1.getEntity()).build();
			    	} else if(linkageProductResponse2.getStatus() == Status.OK.getStatusCode()) {
			    		return Response.status(Status.CREATED).entity(linkageProductResponse2.getEntity()).build();
			    	}else {
			    		return returnErrorResponse();
			    	}
				}
			};
			
			Action1<? super Response> onNextFunc = new Action1<Response>() {
				@Override
				public void call(Response response) {
					returnedResponse = response;
				}
			};
			
			Observable.just(duns1, duns2)
						.map(osbCallFunc)
			        	.reduce(mergeFunc)
			        	.subscribeOn(Schedulers.io())  // This guy make the parallel calls.
			        	.subscribe(onNextFunc);
			
			return returnedResponse;
        } else {
            return returnErrorResponse();
        }
    }
    
	private Response returnErrorResponse() {
		return Response.status(Status.SERVICE_UNAVAILABLE).entity("Failure").build();
	}
}