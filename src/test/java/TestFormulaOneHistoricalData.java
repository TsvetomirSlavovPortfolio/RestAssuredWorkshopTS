import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class TestFormulaOneHistoricalData {

    public static Response response;
    public static String jsonAsString;

    @Test
    public void test_PrintTheResponse(){
        response =
                when().
                        get("http://ergast.com/api/f1/2017/circuits.json").
                then().
                        contentType(ContentType.JSON).
                extract().response();

        jsonAsString = response.prettyPeek().asString();
        System.out.println(jsonAsString);
    }

    @Test
    public void test_NumberOfCircuitsFor2017Season_ShouldBe20(){
        ValidatableResponse response = given().
        when().
            get("http://ergast.com/api/f1/2017/circuits.json").
        then().
            assertThat().
                //Captures the (JSON) response of the API call
                //Queries for all elements called circuitId using the Groovy GPath expression "MRData.CircuitTable.Circuits.circuitId"
                //Verifies using the static import of Hamcrest matcher which allows to just call the methods
                    body("MRData.CircuitTable.Circuits.circuitId", hasSize(20));
        //There are Hamcrest matchers for a large number of different checks, including equalTo() for equality, lessThan() and greaterThan() for comparison, hasItem() to check whether a collection contains a given element, and many more. Reference the Hamcrest library documentation for a full list of matchers.
        //If I look in PostMan the response this is an object.object.arrayOfObjects.oneOfTheKeys that is present in each of these 20 objects inside the array of objects Circuits
        System.out.println(response.toString());
    }

    /**
     *This example shows how to concatenate checks using the and() method
     */
    @Test
    public void test_ResponseHeaderData_ShouldBeCorrect(){
        given().
        when().
            get("http://ergast.com/api/f1/2017/circuits.json").
        then().
            assertThat().
                statusCode(200).
                and().
                contentType(ContentType.JSON).
                and().
                header("Content-Length", equalTo("4551"));
    }

    @Test
    public void test_ContentExistsAndIsInSpecificLocationUsingGroovyGPathExpressionAndHamcrestMatchers(){
        ValidatableResponse response = given().
                when().
                get("http://ergast.com/api/f1/2017/circuits.json").
                then().
                assertThat().
                        body("MRData.CircuitTable.Circuits.circuitId[0]", equalTo("albert_park"));
    }

    @Test
    public void test_ExtractListOfValuesOfRepeatingNodes(){
        Response response = get("http://ergast.com/api/f1/2017/circuits.json");
        ArrayList<String> allCircuitIds = response.path("MRData.CircuitTable.Circuits.circuitId");
        for(String name : allCircuitIds){
            System.out.println(name);
        }
    }

    @Test
    public void test_Md5CheckSumForTest_ShouldBe098f6bcd4621d373cade4e832627b4f6() {

        String originalText = "test";
        String expectedMd5CheckSum = "098f6bcd4621d373cade4e832627b4f6";

        given().
                param("text",originalText).
                when().
                get("http://md5.jsontest.com").
                then().
                assertThat().
                body("md5",equalTo(expectedMd5CheckSum));
    }

    @Test
    public void test_NumberOfCircuits_ShouldBe20_Parameterized() {

        String season = "2017";
        int numberOfRaces = 20;

        given().
                pathParam("raceSeason",season).
                when().
                get("http://ergast.com/api/f1/{raceSeason}/circuits.json").
                then().
                assertThat().
                body("MRData.CircuitTable.Circuits.circuitId",hasSize(numberOfRaces));
    }

    @DataProvider(name="seasonsAndNumberOfRaces")
    public Object[][] createTestDataRecords() {
        return new Object[][] {
                {"2017",20},
                {"2016",21},
                {"1966",9}
        };
    }

    @Test(dataProvider="seasonsAndNumberOfRaces")
    public void test_NumberOfCircuits_ShouldBe_DataDriven(String season, int numberOfRaces) {

        given().
                pathParam("raceSeason",season).
                when().
                get("http://ergast.com/api/f1/{raceSeason}/circuits.json").
                then().
                assertThat().
                body("MRData.CircuitTable.Circuits.circuitId",hasSize(numberOfRaces));
    }

    @Test
    public void test_APIWithBasicAuthentication_ShouldBeGivenAccess() {

        given().
                auth().
                preemptive().
                basic("username", "password").
                when().
                get("http://path.to/basic/secured/api").
                then().
                assertThat().
                statusCode(200);
    }

    @Test
    public void test_APIWithOAuth2Authentication_ShouldBeGivenAccess() {

        given().
                auth().
                oauth2("YOUR_AUTHENTICATION_TOKEN_GOES_HERE").
                when().
                get("http://path.to/oath2/secured/api").
                then().
                assertThat().
                statusCode(200);
    }


    // Passing parameters Passing parameters between tests
    //  Often, when testing RESTful APIs, you might need to create more complex test scenarios where you'll need to capture a value from the response of one API call and reuse it in a subsequent call. This is supported by REST Assured using the extract() method. As an example, here's a test scenario that extracts the ID for the first circuit of the 2017 Formula 1 season and uses it to retrieve and verify additional information on that circuit (in this case, the circuit is located in Australia):
    @Test
        public void test_ScenarioRetrieveFirstCircuitFor2017SeasonAndGetCountry_ShouldBeAustralia() {

        // First, retrieve the circuit ID for the first circuit of the 2017 season
        String circuitId = given().
                when().
                get("http://ergast.com/api/f1/2017/circuits.json").
                then().
                extract().
                path("MRData.CircuitTable.Circuits.circuitId[0]");

        // Then, retrieve the information known for that circuit and verify it is located in Australia
        given().
                pathParam("circuitId",circuitId).
                when().
                get("http://ergast.com/api/f1/circuits/{circuitId}.json").
                then().
                assertThat().
                body("MRData.CircuitTable.Circuits.Location[0].country",equalTo("Australia"));
    }

    ResponseSpecification checkStatusCodeAndContentType =
            new ResponseSpecBuilder().
                    expectStatusCode(200).
                    expectContentType(ContentType.JSON).
                    build();

    @Test
    public void test_NumberOfCircuits_ShouldBe20_UsingResponseSpec() {

        given().
                when().
                get("http://ergast.com/api/f1/2017/circuits.json").
                then().
                assertThat().
                spec(checkStatusCodeAndContentType).
                and().
                body("MRData.CircuitTable.Circuits.circuitId",hasSize(20));
    }





}
