import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestFootbalDataOrgAPIGPathGroovyJSONFunctionsExample {

    @BeforeClass
    public static void setupRestAssured() {
        RestAssured.baseURI = "http://api.football-data.org";
        RestAssured.basePath = "/v1/";
        RequestSpecification requestSpecification = new RequestSpecBuilder().
                addHeader("X-Auth-Token", "59630412c186486eb2756302ab859eb4").//fze4b032141e23f453c22dbb2f78946b
                addHeader("X-Response-Control", "minified")
                .build();
        RestAssured.requestSpecification = requestSpecification;
    }

    @Test
    public void extractSingleValue_findSingleTeamName() {
        Response response = get("teams/66");
        String teamName = response.path("name");
        System.out.println(teamName);
    }

    @Test
    public void extractSingleValue_findSingleTeamName_specifyJsonPath() {
        Response response = get("teams/66");
        JsonPath jsonPath = new JsonPath(response.asString());
        String teamName = jsonPath.get("name");
        System.out.println(teamName);
    }

    @Test
    public void extractSingleValue_findSingleTeamName_responseAsString() {
        String responseAsString = get("teams/66").asString();
        String teamName = JsonPath.from(responseAsString).get("name");
        System.out.println(teamName);
    }

    @Test
    public void extractSingleValue_findSingleTeamName_getEverythingInOneGo() {
        String teamName = get("teams/66").path("name");
        System.out.println(teamName);
    }

    @Test
    public void extractSingleValue_findSingleTeamName_useAssertion() {
        given().
                when().
                get("teams/66").
                then().
                assertThat().
                body("name", equalTo("Manchester United FC"));
    }

    @Test
    public void extractFirstValueWhenSeveralReturned_findFirstTeamName() {
        Response response = get("http://api.football-data.org/v1/competitions/426/teams");
        String firstTeamName = response.path("teams.name[0]");
        System.out.println(firstTeamName);
    }

    @Test
    public void extractLastValueWhenSeveralReturned_findLastTeamName() {
        Response response = get("http://api.football-data.org/v1/competitions/426/teams");
        String lastTeamName = response.path("teams.name[-1]");
        System.out.println(lastTeamName);
    }

    @Test
    public void extractListOfValues_findAllTeamNames() {
        Response response = get("competitions/426/teams");
        ArrayList<String> allTeamNames = response.path("teams.name");
        System.out.println(allTeamNames);
    }

    @Test
    public void extractListOfMapsOfElements_findAllTeamData() {
        Response response = get("competitions/426/teams");
        ArrayList<Map<String,?>> allTeamData = response.path("teams");
        System.out.println(allTeamData);
    }

    //.find is a GPath JSON iterator and we loop through the whole JSON or part of it.
    //This is really powerful
    @Test
    public void extractMapOfElementsWithFind_findAllTeamDataForSingleTeam(){
        Response response = get("competitions/426/teams");
        Map<String,?> allTeamDataForSingleTeam = response.path("teams.find { it.name == 'Leicester City FC' }");
        System.out.println(allTeamDataForSingleTeam);
    }

    @Test
    public void extractSingleValueWithFind_findAPlayerWithACertainJerseyNumber(){
        Response response = get("teams/66/players");
        String certainPlayer = response.path("players.find { it.jerseyNumber == 20 }.name");
        System.out.println(certainPlayer);
    }

    @Test
    public void extractListOfValuesWithFindAll_findAllPlayersWithJerseyNumberGreaterThan10(){
        Response response = get("teams/66/players");
        List<String> playerNames = response.path("players.findAll { it.jerseyNumber > 10 }.name");
        System.out.println(playerNames);
    }

    @Test
    public void extractSingleValueWithHighestValueOfOtherElement_findHighestPlayerNumber(){

        Response response = get("teams/66/players");
        String highestNumberPlayer = response.path("players.max { it.jerseyNumber }.name");
        System.out.println(highestNumberPlayer);
    }

    @Test
    public void extractSingleValueWithLowestValueOfOtherElement_findLowestPlayerNumber() {

        Response response = get("teams/66/players");
        String lowestNumberPlayer = response.path("players.min { it.jerseyNumber }.name");
        System.out.println(lowestNumberPlayer);
    }

    @Test
    public void extractMultipleValuesWithCollectAndSumThem_addUpAllJerseyNumbers(){
        Response response = get("teams/66/players");
        int sumOfJerseys = response.path("players.collect { it.jerseyNumber }.sum() ");
        System.out.println(sumOfJerseys);
    }

    @Test
    public void extractMapOfObjectWithFindAllAndFind_findSinglePlayerOfACertainPositionAndNationality() {
        Response response = get("http://api.football-data.org/v1/teams/66/players");
        Map<String,?> playerOfCertainPosition = response.path("players.findAll { it.position == \"Centre-Back\" }.find { it.nationality == \"Argentina\" }");
        System.out.println(playerOfCertainPosition);
    }

    @Test
    public void extractMapOfObjectWithFindAllAndFindUsingParameters_findSinglePlayerOfACertainPositionAndNationality() {
        Response response = get("http://api.football-data.org/v1/teams/66/players");
        String position = "Right-Back";
        String nationality = "Ecuador";
        Map<String,?> playerOfCertainPositionByParameters = response.path("players.findAll { it.position == '%s' }.find { it.nationality == '%s' }",
                position, nationality);
        System.out.println(playerOfCertainPositionByParameters);
    }

    @Test
    public void extractListOfMapOfElementsWithMultipleFindAlls_findAllPlayersOfACertainPositionAndNationality() {
        Response response = get("http://api.football-data.org/v1/teams/66/players");
        String position = "Centre-Back";
        String nationality = "England";
        ArrayList<Map<String,?>> allPlayersOfCertainPositionAndNationality = response.path("players.findAll { it.position == '%s' }.findAll { it.nationality == '%s' }",
                position, nationality);
        System.out.println(allPlayersOfCertainPositionAndNationality);
    }



}




