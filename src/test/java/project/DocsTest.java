package project;

import io.restassured.RestAssured;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@TestMethodOrder(MethodOrderer.Random.class)
public class DocsTest {
    private Process p;

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
    public void GetDocsTest() {
        given().when().get("/docs").then().assertThat().statusCode(200);
    }

    @Test
    public void GetDocsSwaggerTest() { given().when().get("/docs/swagger").then().assertThat().statusCode(200); }
}
