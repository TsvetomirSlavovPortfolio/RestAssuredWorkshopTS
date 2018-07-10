import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;

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
                        body("MRData.CircuitTable.Circuits.circuitId", hasSize(20));
    }


}
