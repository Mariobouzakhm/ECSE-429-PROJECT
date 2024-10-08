package project;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosUnexpectedTest {
    private Process p;

    private JSONObject paramsMap1;
    private JSONObject paramsMap2;

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


        paramsMap1 = new JSONObject();
        paramsMap1.put("title", "Mock Title");
        paramsMap1.put("description", "Mock Description");
        paramsMap1.put("doneStatus", false);

        paramsMap2 = new JSONObject();
        paramsMap2.put("title", "Mock Title 2");
        paramsMap2.put("description", "Mock Description 2");
        paramsMap2.put("doneStatus", true);

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

    /**
     * Should Return 404 as indicated in the datasheet, but instead returns 400
     */
    @Test
    public void PutInvalidTodoTest() {
        paramsMap2.remove("title");

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap2.toJSONString());

        request.put("/todos/1")
                .then()
                .assertThat()
                .statusCode(404);
    }

    /**
     * Unexpected Behaviour, not documented but should return some form of error to indicate that the
     * todo does not exist
     */
    @Test
    public void GetInvalidTodoCategoriesTest() {
        given().when().get("/todos/10/categories").then().assertThat()
                .statusCode(404);
    }

    /**
     * Unexpected Behaviour, not documented but should return some form of error to indicate that the
     * todo does not exist
     */
    @Test
    public void HeadInvalidTodoCategoriesTest() {
        given().when().head("/todos/10/categories").then().assertThat().statusCode(404);
    }

    /**
     * Unexpected Behavior should give 400
     */
    @Test
    public void PostInvalidCategoryValidTodoTest() {
        JSONObject paramsMap = new JSONObject();
        paramsMap.put("id", String.valueOf(5));

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap.toJSONString());

        request.post("/todos/"+1+"/categories")
                .then()
                .assertThat()
                .statusCode(400);
    }

    /**
     * Unexpected beahvaior, should be 400 instead of 404.
     */
    @Test
    public void PostInvalidTasksOfTodoTest() {
        JSONObject paramsMap = new JSONObject();
        paramsMap.put("id", "8");

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap.toJSONString());

        request.post("/todos/"+1+"/tasksof")
                .then()
                .assertThat()
                .statusCode(400);
    }

    /**
     * Unexpected Behavior, todo with this id does not exist.
     */
    @Test
    public void GetInvalidTodoTasksOfTest() {
        given().when().get("/todos/3/tasksof").then().assertThat()
                .statusCode(404).extract().jsonPath().getMap("$");
    }

    /**
     * Unexpected Behavior, todo with this id does not exist
     */
    @Test
    public void HeadInvlidTodoTasksOfTest() {
        given().when().head("/todos/3/tasksof").then().assertThat().statusCode(404);
    }
}
