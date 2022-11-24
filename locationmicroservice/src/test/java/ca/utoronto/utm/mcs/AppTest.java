package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {
    // final static String API_URL = "http://localhost:8000";
    // public Neo4jDAO dao = new Neo4jDAO();

    // private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
    //     HttpClient client = HttpClient.newHttpClient();
    //     HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create(API_URL + endpoint))
    //             .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
    //             .build();

    //     return client.send(request, HttpResponse.BodyHandlers.ofString());
    // }

    // @Test
    // public void getNavigationPass()  throws JSONException, IOException, InterruptedException {
    //     this.dao.addUser("123123", true);
    //     this.dao.addUser("234234", false);
    //     this.dao.createRoad("Queen St.", false);
    //     this.dao.createRoad("King st.", false);
    //     this.dao.updateUserLocation("123123", 0, 0, "Queen St.");
    //     this.dao.updateUserLocation("234234", 0, 0, "King st.");
    //     this.dao.createRoute("Queen St.", "King st.", 4, false);
        
    //     JSONObject req = new JSONObject();
    //     HttpResponse<String> confirmRes = sendRequest("/location/navigation/123123?passengerUid=234234", "GET", req.toString());
    //     assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    // }
}
