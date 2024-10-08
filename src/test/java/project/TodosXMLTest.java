package project;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosXMLTest {
    private Process p;

    private StringBuilder paramsMap1;
    private StringBuilder paramsMap2;
    private StringBuilder paramsMap3;

    @BeforeEach
    public void beforeEachTest() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4567;

        Runtime runtime = Runtime.getRuntime();
        try {
            this.p = runtime.exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        paramsMap1 = new StringBuilder();
        paramsMap1.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        paramsMap1.append("<todo>");
        paramsMap1.append("<title>Mock Title</title>");
        paramsMap1.append("<description>Mock Description</description>");
        paramsMap1.append("<doneStatus>false</doneStatus>");
        paramsMap1.append("</todo>");

        paramsMap2 = new StringBuilder();
        paramsMap2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        paramsMap2.append("<todo>");
        paramsMap2.append("<title>Mock Title 2</title>");
        paramsMap2.append("<description>Mock Description 2</description>");
        paramsMap2.append("<doneStatus>true</doneStatus>");
        paramsMap2.append("</todo>");

        paramsMap3 = new StringBuilder();
        paramsMap2.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        paramsMap3.append("<todo>");
        paramsMap3.append("<description>Mock Description 2</description>");
        paramsMap3.append("<doneStatus>true</doneStatus>");
        paramsMap3.append("</todo>");

        given().when().get("/docs").then().assertThat().statusCode(200);
    }

    @AfterEach
    public void afterEachTest() {
        try {
            p.destroy();
            Thread.sleep(500);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void GetTodosTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        Response res = request.when().get("/todos").then()
                .assertThat().statusCode(200).extract().response();
    }

    @Test
    public void HeadTodosTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        Response res = request.when().head("/todos").then()
                .assertThat().statusCode(200).extract().response();
    }

    @Test
    public void PostTodosTest() {
        int beforeList = getLengthOfTodos();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");
        request.body(paramsMap1.toString());

        request.post("/todos")
                .then()
                .assertThat()
                .statusCode(201);

        int afterList = getLengthOfTodos();
        assertEquals(beforeList + 1, afterList);
    }

    @Test
    public void PostValidTodoTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");
        request.body(paramsMap2.toString());

        request.post("/todos/1")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void PostMissingTodoTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");
        request.body(paramsMap3.toString());

        request.post("/todos/1")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void PutValidTodoTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");
        request.body(paramsMap2.toString());

        Response response = request.put("/todos/1")
                .then()
                .assertThat()
                .statusCode(200).extract().response();
    }

    @Test
    public void DeleteValidTodosTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        int beforeList = getLengthOfTodos();
        given().when().delete("/todos/1").then().
                assertThat().
                statusCode(200).
                extract().response();
        int afterList = getLengthOfTodos();
        assertEquals(beforeList, afterList +1);
    }

    @Test
    public void DeleteInvalidTodosTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        int beforeList = getLengthOfTodos();
        given().when().delete("/todos/10").then().
                assertThat().
                statusCode(404).
                extract().response();
        int afterList = getLengthOfTodos();
        assertEquals(beforeList, afterList);
    }

    private int getLengthOfTodos() {
        RequestSpecification request = given();
        request.header("Content-Type", "application/xml");
        request.header("Accept", "application/xml");

        Response response = request.when()
                .get("/todos").then().assertThat().statusCode(200)
                .extract().response();
        XmlPath xmlPath = new XmlPath(HTML, response.getBody().asString());
        return countNumberOfMatches(response.getBody().asString(), "</todo>");
    }

    private int countNumberOfMatches(String initial, String instance) {
        int currentIndex = 0;
        int matches = 0;

        while (currentIndex != -1) {
            currentIndex = initial.indexOf(instance, currentIndex);

            if (currentIndex != -1) {
                currentIndex += instance.length();
                matches++;
            }
        }

        return matches;
    }
}
