package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {
    final static String API_URL = "http://apigateway:8000";

    public static PostgresDAO dao = new PostgresDAO();
    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    @BeforeAll
    public static void init() throws SQLException {
        dao.addUser("doe", "doe@gmail.com", "doe123");
    }
    @AfterAll
    public static void after() throws SQLException {
        dao.deleteUser("john@gmail.com");
        dao.deleteUser("doe@gmail.com");
    }
    @Test
    public void RegisterPass() throws IOException, InterruptedException, JSONException {
        JSONObject req = new JSONObject();
        req.put("name", "john");
        req.put("email", "john@gmail.com");
        req.put("password", "john123");
        HttpResponse<String> confirmRes = sendRequest("/user/register", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    public void RegisterFail() throws IOException, InterruptedException, JSONException {
        JSONObject req = new JSONObject();
        req.put("name", "doe");
        req.put("email", "doe@gmail.com");
        req.put("password", "doe123");
        HttpResponse<String> confirmRes = sendRequest("/user/register", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    @Test
    public void LoginPass() throws IOException, InterruptedException, JSONException {
        JSONObject req = new JSONObject();
        req.put("name", "doe");
        req.put("email", "doe@gmail.com");
        req.put("password", "doe123");
        HttpResponse<String> confirmRes = sendRequest("/user/login", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void LoginFail()  throws JSONException, IOException, InterruptedException {
        JSONObject req = new JSONObject();
        req.put("name", "doe");
        req.put("email", "doe@gmail.com");
        req.put("password", "worngpassword");
        HttpResponse<String> confirmRes = sendRequest("/user/login", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, confirmRes.statusCode());
    }


}
