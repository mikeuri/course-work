package automation.app.api;

import automation.Config;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KanboardAPI {

    private static Response baseGetRequest(JSONObject requestParams, Boolean useAppCredentials, Boolean useAuth) {
        String username;
        String password;

        if (useAppCredentials) {
            username = Config.API_AUTH_APP_USERNAME.value;
            password = Config.API_AUTH_APP_PASSWORD.value;
        } else {
            username = Config.API_AUTH_USER_USERNAME.value;
            password = Config.API_AUTH_USER_PASSWORD.value;
        }

        if (useAuth) {
            return
                RestAssured
                    .given()
                        .body(requestParams.toString())
                        .header(new Header("x-api-key", "xApiAuthToken"))
                        .auth().basic(username, password)
                        .contentType("application/json")
                        .accept("application/json")
                    .when()
                        .get("/jsonrpc.php");
        } else {
            return
                RestAssured
                    .given()
                        .body(requestParams.toString())
                        .header(new Header("x-api-key", "xApiAuthToken"))
                        .contentType("application/json")
                        .accept("application/json")
                    .when()
                        .get("/jsonrpc.php");
        }
    }

//    private static Response baseGetRequestNoAuth(JSONObject requestParams) {
//        return
//            RestAssured
//                .given()
//                    .body(requestParams.toString())
//                    .header(new Header("x-api-key", "xApiAuthToken"))
//                    .contentType("application/json")
//                    .accept("application/json")
//                .when()
//                    .get("/jsonrpc.php");
//    }

    private static JSONObject baseRequestParams(String methodName, Integer requestID){
        JSONObject requestParams = new JSONObject();
        requestParams.put("jsonrpc", "2.0");
        requestParams.put("method", methodName);
        requestParams.put("id", requestID);
        
        return requestParams;
    }

    private static JSONObject getRequestParamsForCreateUser(String username, String password, Integer requestID) {
        JSONObject requestParams = baseRequestParams("createUser", requestID);

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        requestParams.put("params", params);

        return requestParams;
    }

    public static Response createUser(String username, String password, Integer requestID, Boolean useAuth) {
        JSONObject requestParams = getRequestParamsForCreateUser(username, password, requestID);

        return baseGetRequest(requestParams, false, useAuth);
    }

    public static Response createUserWithNoPassword(String username, Integer requestID) {
        JSONObject requestParams = baseRequestParams("createUser", requestID);
        
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        requestParams.put("params", params);

        return baseGetRequest(requestParams, false, true);
    }

    public static List<HashMap> getAllUsers() {
        JSONObject requestParams = baseRequestParams("getAllUsers", 1000);

        return baseGetRequest(requestParams, false, true).jsonPath().getList("result");
    }

    private static JSONObject getRequestParamsForRemoveUser(Integer userID, Integer requestID) {
        JSONObject requestParams = baseRequestParams("removeUser", requestID);
        
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userID.toString());
        requestParams.put("params", params);

        return requestParams;
    }

    public static Response removeUser(Integer userID, Integer requestID, Boolean useAuth) {
        JSONObject requestParams = getRequestParamsForRemoveUser(userID, requestID);

        return baseGetRequest(requestParams, false, useAuth);

    }

    private static JSONObject getRequestParamsForCreateProject(String projectName, Integer requestID, Integer ownerID) {
        JSONObject requestParams = baseRequestParams("createProject", requestID);

        Map<String, String> params = new HashMap<>();
        params.put("name", projectName);
        params.put("owner_id", ownerID.toString());
        requestParams.put("params", params);

        return requestParams;
    }

    public static Response createProject(String projectName, Integer requestID, Integer ownerID, Boolean useAuth){
        JSONObject requestParams = getRequestParamsForCreateProject(projectName, requestID, ownerID);

        return baseGetRequest(requestParams, false, useAuth);
    }

    public static Response createProjectWithWrongRequestBody(Integer requestID) {
        JSONObject requestParams = baseRequestParams("createProject", requestID);

        return baseGetRequest(requestParams, false, true);
    }

//    public static Response createProjectWithNoAuth(String projectName, Integer requestID, Integer ownerID) {
//        JSONObject requestParams = getRequestParamsForCreateProject(projectName, requestID, ownerID);
//
//        return baseGetRequestNoAuth(requestParams);
//    }


    public static List<HashMap> getAllProjects() {
        JSONObject requestParams = baseRequestParams("getAllProjects", 1000);

        return baseGetRequest(requestParams, false, true).jsonPath().getList("result");
    }

    public static Response removeProject(Integer projectID, Integer requestID, Boolean useAuth) {
        JSONObject requestParams = baseRequestParams("removeProject", requestID);

        Map<String, String> params = new HashMap<>();
        params.put("project_id", projectID.toString());
        requestParams.put("params", params);

        return baseGetRequest(requestParams, true, useAuth);
    }


//    public static Response removeProjectWithNoAuth(Integer projectID, Integer requestID) {
//        JSONObject requestParams = baseRequestParams("removeProject", requestID);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("project_id", projectID.toString());
//        requestParams.put("params", params);
//
//        return baseGetRequestNoAuth(requestParams);
//    }


    private static JSONObject getRequestParamsForCreateTask(String title, Integer projectID, Integer ownerID,
                                                            Integer requestID) {
        JSONObject requestParams = baseRequestParams("createTask", requestID);

        Map<String, String> params = new HashMap<>();
        params.put("title", title);
        params.put("project_id", projectID.toString());
        params.put("owner_id", ownerID.toString());
        requestParams.put("params", params);

        return requestParams;
    }

    public static Response createTask(String title, Integer projectID, Integer ownerID,
                                      Integer requestID, Boolean useAuth, Boolean useAppCreds) {
        JSONObject requestParams = getRequestParamsForCreateTask(title, projectID, ownerID, requestID);

        return baseGetRequest(requestParams, useAppCreds, useAuth);
    }

    public static List<HashMap> getAllTasks(Integer projectID, Integer statusID, Integer requestID) {
        JSONObject requestParams = baseRequestParams("getAllTasks", requestID);

        Map<String, String> params = new HashMap<>();
        params.put("project_id", projectID.toString());
        params.put("status_id", statusID.toString());
        requestParams.put("params", params);

        return baseGetRequest(requestParams, true, true).jsonPath().getList("result");
    }

    public static Response removeTask(Integer requestID, Integer taskID, Boolean useAuth) {
        JSONObject requestParams = baseRequestParams("removeTask", requestID);

        Map<String, String> params = new HashMap<>();
        params.put("task_id", taskID.toString());
        requestParams.put("params", params);

        return baseGetRequest(requestParams, false, useAuth);
    }

    public static Integer getTaskIDByName(String taskTitleToAdd, Integer projectID) {

        List<HashMap> allTasks = getAllTasks(projectID, 1, 1);
        HashMap<String, String> allTasksTitles = new HashMap<>();
        for (HashMap entry : allTasks) {
            allTasksTitles.put(entry.get("title").toString(), entry.get("id").toString());
        }

        return Integer.valueOf(allTasksTitles.get(taskTitleToAdd));
    }
}
