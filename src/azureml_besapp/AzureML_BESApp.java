/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package azureml_besapp;

import java.io.File;
import java.util.Scanner;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.*;
/**
 *
 * @author neerajkh
 */

public class AzureML_BESApp {


    public static String apikey;
    public static String apiurl;
    public static String startJobUrl;
    public static String jsonBody;
    public static String jobId;//1ebfc0453a214f5a90114d962827fd45
    /**
     * Read the JSON schema from the file rrsJson.json
     * 
     * @param filename It expects a fully qualified file name that contains input JSON file
     */		
    public static void readJson(String filename) {
        try {
            File apiFile = new File(filename);
            Scanner sc = new Scanner(apiFile);
            jsonBody = "";
            while (sc.hasNext()) {
                jsonBody += sc.nextLine()+"\n";
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
    
    /**
     * Read the API key and API URL of Azure ML request response REST API
     * 
     * @param filename fully qualified file name that contains API key and API URL
     */	
    public static void readApiInfo(String filename) {
        
        try {
            File apiFile = new File(filename);
            Scanner sc = new Scanner(apiFile);
            
            apiurl = sc.nextLine();
            apikey = sc.nextLine();
            startJobUrl = sc.nextLine();
            
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        
    }
    
    /**
     * Call REST API to submit job to Azure ML for batch predictions
     * @return response from the REST API
     */	
    public static String besHttpPost() {
        
        HttpPost post;
        HttpClient client;
        StringEntity entity;
        
        try {
            // create HttpPost and HttpClient object
            post = new HttpPost(apiurl);
            client = HttpClientBuilder.create().build();
            
            // setup output message by copying JSON body into 
            // apache StringEntity object along with content type
            entity = new StringEntity(jsonBody, HTTP.UTF_8);
            entity.setContentEncoding(HTTP.UTF_8);
            entity.setContentType("text/json");

            // add HTTP headers
            post.setHeader("Accept", "text/json");
            post.setHeader("Accept-Charset", "UTF-8");
        
            // set Authorization header based on the API key
            post.setHeader("Authorization", ("Bearer "+apikey));
            post.setEntity(entity);

            // Call REST API and retrieve response content
            HttpResponse authResponse = client.execute(post);
            
            jobId = EntityUtils.toString(authResponse.getEntity()).replaceAll("\"", "");
            
            
            return jobId;
            
        }
        catch (Exception e) {
            
            return e.toString();
        }
    
    }
    
    /**
     * Call REST API to start previously submitted job 
     * @return response from the REST API
     */	
    public static String besStartJob() {
        //job_id/start?api-version=2.0
        return besStartJob(jobId);
    }
    
    /**
     * Call REST API for starting prediction job previously submitted 
     * 
     * @param job job to be started 
     * @return response from the REST API
     */	
    public static String besStartJob(String job){
        HttpPost post;
        HttpClient client;
        StringEntity entity;
        
        try {
            // create HttpPost and HttpClient object
            post = new HttpPost(startJobUrl+"/"+job+"/start?api-version=2.0");
            client = HttpClientBuilder.create().build();
         
            // add HTTP headers
            post.setHeader("Accept", "text/json");
            post.setHeader("Accept-Charset", "UTF-8");
        
            // set Authorization header based on the API key
            post.setHeader("Authorization", ("Bearer "+apikey));

            // Call REST API and retrieve response content
            HttpResponse authResponse = client.execute(post);
            
            if (authResponse.getEntity()==null)
            {
                return authResponse.getStatusLine().toString();
            }
            
            return EntityUtils.toString(authResponse.getEntity());
            
        }
        catch (Exception e) {
            
            return e.toString();
        }
    }
    
    /**
     * Call REST API for canceling the batch job 
     * 
     * @param job job to be started 
     * @return response from the REST API
     */	
    public static String besCancelJob(String job) {
        //job_id/start?api-version=2.0
        HttpDelete post;
        HttpClient client;
        StringEntity entity;
        
        try {
            // create HttpPost and HttpClient object
            post = new HttpDelete(startJobUrl+job);
            client = HttpClientBuilder.create().build();
         
            // add HTTP headers
            post.setHeader("Accept", "text/json");
            post.setHeader("Accept-Charset", "UTF-8");
        
            // set Authorization header based on the API key
            post.setHeader("Authorization", ("Bearer "+apikey));

            // Call REST API and retrieve response content
            HttpResponse authResponse = client.execute(post);
         
            if (authResponse.getEntity()==null)
            {
                return authResponse.getStatusLine().toString();
            }
            return EntityUtils.toString(authResponse.getEntity());
            
        }
        catch (Exception e) {
            
            return e.toString();
        }
    }
    
    /**
     * Call REST API for canceling the current job 
     * 
     * @return response from the REST API
     */	
    public static String besCancelJob() {
        return besCancelJob(jobId);
    }
    
    /**
     * @param args the command line arguments specifying JSON and API info file names
     */
    public static void main(String[] args) {
        // check for mandatory argments. This program expects 2 arguments 
        // first argument is full path with file name of JSON file and 
        // second argument is full path with file name of API file that contains API URL and API Key of request response REST API
        if (args.length < 2) {
			System.out.println("Incorrect usage. Please use the following calling pattern");
			System.out.println("java AzureML_BESApp <jsonFilename> <apiInfo Filename>");
	}
        
        try {
		
                // read JSON file name
                String jsonFile = args[0];
                // read API file name
		String apiFile = args[1];
            
                // call method to read API URL and key from API file
                readApiInfo(apiFile);
                
                // call method to read JSON input from the JSON file
		readJson(jsonFile);
                
                // print the response from Submit Job REST API
		System.out.println(besHttpPost());
                // print the respons from Start Job REST API
                System.out.println(besStartJob());
                
                //System.out.println(besStartJob("1ebfc0453a214f5a90114d962827fd45"));
                //System.out.println(besCancelJob("1202a382bf41443ba997dea7f8944b6e"));
                
                
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
}
