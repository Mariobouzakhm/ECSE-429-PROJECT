package project;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.Random.class)
public class TodosTest {
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
     * /TODOS Test
     */
    @Test
    public void GetTodosTest() {
        given().when().get("/todos").then().assertThat().statusCode(200);
    }

    @Test
    public void HeadTodosTest() {
        given().when().head("/todos").then().assertThat().statusCode(200);
    }

    @Test
    public void PostTodosTest() {
        int beforeList = getLengthOfTodos();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap1.toJSONString());

        request.post("/todos")
                .then()
                .assertThat()
                .statusCode(201)
                .body("title", equalTo(paramsMap1.get("title")))
                .body("doneStatus", equalTo(paramsMap1.get("doneStatus").toString()))
                .body("description", equalTo(paramsMap1.get("description")));


        int afterList = getLengthOfTodos();
        assertEquals(beforeList + 1, afterList);
    }

    @Test
    public void PostTodosEmptyTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.post("/todos")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void PostTodosWrongParamsTest() {
        paramsMap1.remove("title");

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.post("/todos")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void OptionsTodosTest() {
        given().when().options("/todos").then().assertThat().statusCode(200);
    }

    @Test
    public void PatchTodosTest() {
        given().when().patch("/todos").then().assertThat().statusCode(405);
    }

    @Test
    public void PutTodosTest() {
        given().when().put("/todos").then().assertThat().statusCode(405);
    }
    @Test
    public void DeleteTodosTest() {
        given().when().delete("/todos").then().assertThat().statusCode(405);
    }

    /**
     * /TODOS:id Test
     */

    @Test
    public void GetValidTodoTest() {
        given().when().get("/todos/1").then().assertThat()
                .statusCode(200)
                .body("todos[0].id", equalTo(String.valueOf(1)));
    }

    @Test
    public void GetInvalidIdTodoTest() {
        given().when().get("/todos/10").then().assertThat()
                .statusCode(404).extract().response();
    }


    @Test
    public void HeadValidTodoTest() {
        given().when().head("/todos/1").then().assertThat()
                .statusCode(200);
    }

    @Test
    public void HeadInvalidTodoTest() {
        given().when().head("/todos/10").then().assertThat()
                .statusCode(404).extract().response();
    }

    @Test
    public void PostValidTodoTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap2.toJSONString());

        request.post("/todos/1")
                .then()
                .assertThat()
                .statusCode(200)
                .body("title", equalTo(paramsMap2.get("title")))
                .body("doneStatus", equalTo(paramsMap2.get("doneStatus").toString()))
                .body("description", equalTo(paramsMap2.get("description")));
    }

    @Test
    public void PostInvalidTodoTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap2.toJSONString());

        request.post("/todos/4")
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void PostMissingTodoTest() {
        paramsMap2.remove("title");

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap2.toJSONString());

        request.post("/todos/1")
                .then()
                .assertThat()
                .statusCode(200)
                .body("doneStatus", equalTo(paramsMap2.get("doneStatus").toString()))
                .body("description", equalTo(paramsMap2.get("description")));
    }

    @Test
    public void PutValidTodoTest() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap2.toJSONString());

        request.put("/todos/1")
                .then()
                .assertThat()
                .statusCode(200)
                .body("title", equalTo(paramsMap2.get("title")))
                .body("doneStatus", equalTo(paramsMap2.get("doneStatus").toString()))
                .body("description", equalTo(paramsMap2.get("description")));
    }

    @Test
    public void DeleteValidTodosTest() {
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
        int beforeList = getLengthOfTodos();
        given().when().delete("/todos/10").then().
                assertThat().
                statusCode(404).
                extract().response();
        int afterList = getLengthOfTodos();
        assertEquals(beforeList, afterList);
    }

    @Test
    public void PostThenDeleteValidTodo() {
        int beforeList = getLengthOfTodos();

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap1.toJSONString());

        Map<String, Object> responseMap =  request.post("/todos")
                .then()
                .assertThat()
                .statusCode(201)
                .body("title", equalTo(paramsMap1.get("title")))
                .body("doneStatus", equalTo(paramsMap1.get("doneStatus").toString()))
                .body("description", equalTo(paramsMap1.get("description"))).extract().jsonPath().getMap("$");

        int addedId = Integer.valueOf((String) responseMap.get("id"));
        int addedList = getLengthOfTodos();

        given().when().delete("/todos/" + addedId).then().
                assertThat().
                statusCode(200).
                extract().response();

        int afterDeletedList = getLengthOfTodos();

        assertEquals(beforeList + 1, addedList);
        assertEquals(beforeList, afterDeletedList);
    }

    @Test
    public void OptionsValidTodoId() {
        given().when().options("/todos/1").then().assertThat().statusCode(200);
    }

    @Test
    public void PathValidTodoId() {
        given().when().patch("/todos/1").then().assertThat().statusCode(405);
    }

    /**
     * /TODOS:id/categories Test
     */
    @Test
    public void GetValidTodoCategoriesTest() {
        Map<String, Object> responseMap = given().when().get("/todos/1/categories").then().assertThat()
                .statusCode(200).extract().jsonPath().getMap("$");

        ArrayList<LinkedHashMap<String, Object>> list = (ArrayList) responseMap.get("categories");
        LinkedHashMap<String, Object> category = list.get(0);

        assertNotNull(category.get("title"));
        assertNotNull(category.get("id"));
    }

    @Test
    public void HeadValidTodoCategoriesTest() {
       given().when().head("/todos/1/categories").then().assertThat().statusCode(200);
    }

    @Test
    public void PostCategoryValidTodoTest() {
        int categoriesBefore = getCategoriesSize(1);

        JSONObject paramsMap = new JSONObject();
        paramsMap.put("id", String.valueOf(2));

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap.toJSONString());

        request.post("/todos/"+1+"/categories")
                .then()
                .assertThat()
                .statusCode(201);


        int categoriesAfter = getCategoriesSize(1);
        assertEquals(categoriesBefore + 1, categoriesAfter);
    }

    @Test
    public void DeleteTodoCategoryTest() {
        int beforeList = getCategoriesSize(1);

        given().when().delete("/todos/1/categories/1").then().
                assertThat().
                statusCode(200).
                extract().response();

        int afterList = getCategoriesSize(1);
        assertEquals(beforeList, afterList + 1);
    }

    @Test
    public void DeleteTodoWrongCategoryTest() {
        int beforeList = getCategoriesSize(1);

        given().when().delete("/todos/1/categories/2").then().
                assertThat().
                statusCode(404).
                extract().response();

        int afterList = getCategoriesSize(1);
        assertEquals(beforeList, afterList);
    }

    @Test
    public void DeleteInvalidTodoCategoryTest() {
        given().when().delete("/todos/100/categories/2").then().
                assertThat().
                statusCode(404).
                extract().response();
    }

    /**
     * /TODOS:id/tasksof
     */
    @Test
    public void GetValidTodoTasksOfTest() {
        Map<String, Object> responseMap = given().when().get("/todos/1/tasksof").then().assertThat()
                .statusCode(200).extract().jsonPath().getMap("$");

        ArrayList<LinkedHashMap<String, Object>> list = (ArrayList) responseMap.get("projects");
        LinkedHashMap<String, Object> category = list.get(0);

        assertNotNull(category.get("title"));
        assertNotNull(category.get("id"));
    }

    @Test
    public void HeadValidTodoTasksOfTest() {
        given().when().head("/todos/1/tasksof").then().assertThat().statusCode(200);
    }

    @Test
    public void PostTasksOfTodoTest() {
        int tasksLength = getTasksOfSize(1);
        int idNewProject = createProject();

        JSONObject paramsMap = new JSONObject();
        paramsMap.put("id", String.valueOf(idNewProject));

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap.toJSONString());

        request.post("/todos/"+1+"/tasksof")
                .then()
                .assertThat()
                .statusCode(201);

        int tasksLengthAfter = getTasksOfSize(1);

        assertEquals(tasksLength + 1, tasksLengthAfter);
    }



    @Test
    public void DeleteTasksOfTodoTest() {
        int tasksLengthBefore = getTasksOfSize(1);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        request.delete("/todos/"+1+"/tasksof/1")
                .then()
                .assertThat()
                .statusCode(200);

        int tasksLengthAfter = getTasksOfSize(1);

        assertEquals(tasksLengthBefore - 1, tasksLengthAfter);
    }

    @Test
    public void DeleteWrongTasksOfTodoTest() {
        given().when().delete("/todos/1/tasksof/10").then().
                assertThat().
                statusCode(404).
                extract().response();
    }

    @Test
    public void DeleteInvalidTasksOfTodoTest() {
        given().when().delete("/todos/100/tasksof/2").then().
                assertThat().
                statusCode(404).
                extract().response();
    }

    @Test
    public void PostAndDeleteTasksofTodoTest() {
        int beforeTasksLength = getTasksOfSize(1);
        int idNewProject = createProject();

        JSONObject paramsMap = new JSONObject();
        paramsMap.put("id", String.valueOf(idNewProject));

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap.toJSONString());

        request.post("/todos/"+1+"/tasksof")
                .then()
                .assertThat()
                .statusCode(201);

        int tasksLengthAfter = getTasksOfSize(1);

        assertEquals(beforeTasksLength + 1, tasksLengthAfter);

        int tasksLengthBefore = getTasksOfSize(1);

        request.delete("/todos/"+1+"/tasksof/" + idNewProject)
                .then()
                .assertThat()
                .statusCode(200);

        int tasksLengthFinal = getTasksOfSize(1);

        assertEquals(tasksLengthAfter - 1, tasksLengthFinal);
    }

    private int getLengthOfTodos() {
        Map<String, Object> responseMap = given().when()
                .get("/todos").then().assertThat().statusCode(200)
                .extract().jsonPath().getMap("$");

        ArrayList<Object> list = (ArrayList) responseMap.get("todos");

        return list.size();
    }

    private int getLengthOfProjects() {
        Map<String, Object> responseMap = given().when()
                .get("/projects").then().assertThat().statusCode(200)
                .extract().jsonPath().getMap("$");

        ArrayList<Object> list = (ArrayList) responseMap.get("projects");

        return list.size();
    }

    private int getTasksOfSize(int todoId) {
        Map<String, Object> responseMap = given().when().get("/todos/"+todoId+"/tasksof").then().assertThat()
                .statusCode(200).extract().jsonPath().getMap("$");

        ArrayList<LinkedHashMap<String, Object>> list = (ArrayList) responseMap.get("projects");
        return list.size();
    }

    private int getCategoriesSize(int todoId) {
        Map<String, Object> responseMap = given().when().get("/todos/"+todoId+"/categories").then().assertThat()
                .statusCode(200).extract().jsonPath().getMap("$");

        ArrayList<LinkedHashMap<String, Object>> list = (ArrayList) responseMap.get("categories");
        return list.size();
    }

    private int createProject() {
        JSONObject projectsMap = new JSONObject();
        projectsMap.put("title", "Library Work");
        projectsMap.put("completed", false);
        projectsMap.put("active", true);
        projectsMap.put("description", "Work in Library");

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(projectsMap.toJSONString());

        Map<String, Object> responseMap = request.post("/projects")
                .then()
                .assertThat()
                .statusCode(201)
                .extract().jsonPath().getMap("$");

        return Integer.valueOf((String) responseMap.get("id"));
    }
 }
