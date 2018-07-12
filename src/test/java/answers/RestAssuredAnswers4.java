package answers;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RestAssuredAnswers4 {
	
	@BeforeClass
	public void initPath() {
		
		RestAssured.baseURI = "http://localhost:9876";
	}
	
	/*******************************************************
	 * Request an authentication token for the Paypal sandbox
	 * API and write the response to the console
	 * Use Basic authentication
	 * username = oauth
	 * password = gimmeatoken
	 * Use /v1/oauth2/token
	 * Store this authentication token in a String variable
	 * for future reference
	 ******************************************************/
	
	String accessToken;
	
	@BeforeClass
	public void retrieveOAuthToken() {
						
		accessToken = 
				
		given().
			auth().
			preemptive().
			basic("oauth","gimmeatoken").
		when().
			get("/v1/oauth2/token").
		then().
			extract().
			path("access_token");
	}
	
	/*******************************************************
	 * Create a ResponseSpecification that checks whether:
	 * - the response has statusCode 200
	 * - the response contentType is JSON
	 * - the value of MRData.CircuitTable.Circuits.circuitName[0]
	 *   is equal to 'Albert Park Grand Prix Circuit'
	 ******************************************************/
	
	ResponseSpecification respSpec;
	
	@BeforeClass
	public void createResponseSpecification() {
		
		respSpec = new ResponseSpecBuilder().
				expectStatusCode(200).
				expectContentType(ContentType.JSON).
				expectBody("MRData.CircuitTable.Circuits.circuitName[0]", equalTo("Albert Park Grand Prix Circuit")).
				build();		
	}
	
	/*******************************************************
	 * Request a list of payments for this account and check
	 * that the number of payments made equals 4.
	 * Use OAuth2 authenticatie with the previously stored
	 * authentication token.
	 * Use /v1/payments/payment/
	 * Value to be retrieved is in the paymentsCount field
	 ******************************************************/
	
	@Test
	public void checkNumberOfPayments() {
		
		given().
			auth().
			oauth2(accessToken).
		when().
			get("/v1/payments/payment/").
		then().
			assertThat().
			body("paymentsCount",equalTo(4));
	}
	
	/*******************************************************
	 * Retrieve the circuit data for the first race in 2014
	 * Use the previously created ResponseSpecification to
	 * execute the specified checks
	 * Use /api/f1/2014/1/circuits.json
	 * Additionally, check that the circuit is located in Melbourne
	 ******************************************************/
	
	@Test
	public void useResponseSpecification() {
		
		given().
		when().
			get("/api/f1/2014/1/circuits.json").
		then().
			spec(respSpec).
			and().
			body("MRData.CircuitTable.Circuits.Location[0].locality",equalTo("Melbourne"));
	}
}