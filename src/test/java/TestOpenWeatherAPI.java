import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import static io.restassured.RestAssured.get;

public class TestOpenWeatherAPI {

    @Test
    public void test_HamcrestMatchers(){
        Response response = get("http://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b1b15e88fa797225412429c1c50c122a1");
        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
        assertThat(statusCode, is(equalTo(200)));
    }

    @Test
    public void test_LogTheWholeResponseToTheConsole(){
        given().
        get("http://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b1b15e88fa797225412429c1c50c122a1").
        then().statusCode(200).log().all();
    }

    @Test
    public void test_LogTheResponseToTheConsole(){
        given().
                get("http://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b1b15e88fa797225412429c1c50c122a1").
                then().log().body();
    }

    @Test
    public void test_LogTheStatusToTheConsole(){
        given().
                get("http://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b1b15e88fa797225412429c1c50c122a1").
        then().
                log().status();
    }

    @Test
    public void test_ParameterAndValidateParticularPartInJSONBody(){
        given().
                param("q", "London,uk").param("appid", "b1b15e88fa797225412429c1c50c122a1").
                get("http://samples.openweathermap.org/data/2.5/weather").
        then().
                assertThat().body("name", equalTo("London"));
    }

    @Test
    public void test_HeadersForAuthorization(){
        given().
                param("q", "London,uk").param("appid", "b1b15e88fa797225412429c1c50c122a1").
                header("SOMEVAL", "SOMEDATA").
                get("http://samples.openweathermap.org/data/2.5/weather").
                then().
                assertThat().body("name", equalTo("London"));
    }

    @Test
    public void test_ValidatingMultipleContent(){
        given().
                param("q", "London,uk").param("appid", "b1b15e88fa797225412429c1c50c122a1").
                get("http://samples.openweathermap.org/data/2.5/weather").
                then().
                assertThat().body("name", equalTo("London")).body("base", equalTo("stations")).statusCode(200).log().all();
    }

    @Test
    public void test_XPathApproach(){
        given().
                param("q", "London,uk").param("appid", "b1b15e88fa797225412429c1c50c122a1").
                get("http://samples.openweathermap.org/data/2.5/weather").
                then().
                body("name", containsString("London"));
    }

    @Test
    public void test_IgnoreCase(){
        given().
                param("q", "London,uk").param("appid", "b1b15e88fa797225412429c1c50c122a1").
                get("http://samples.openweathermap.org/data/2.5/weather").
                then().
                body("name", equalToIgnoringCase("london"));
    }

    @Test
    public void test_XML_UsingParamAndXPathBecauseWeUseSlashForXPathAndDotForJSONToTraverseToTheNodeThatWeWantToGrab(){
        given().
                param("q", "London,uk").param("mode", "xml").param("appid", "b1b15e88fa797225412429c1c50c122a1").
                get("http://samples.openweathermap.org/data/2.5/weather").
                then().
                body(hasXPath("/current/city/country"), containsString("GB")).log().all();
    }


}
