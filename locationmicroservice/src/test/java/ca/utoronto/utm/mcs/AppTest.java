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
     final static String API_URL = "http://localhost:8004";
     public static Neo4jDAO dao = new Neo4jDAO();

     private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
         HttpClient client = HttpClient.newHttpClient();
         HttpRequest request = HttpRequest.newBuilder()
                 .uri(URI.create(API_URL + endpoint))
                 .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                 .build();

         return client.send(request, HttpResponse.BodyHandlers.ofString());
     }
     @BeforeAll
     public static void init(){
         dao.addUser("123123", true);
         dao.addUser("234234", false);
         dao.createRoad("Queen St.", false);
         dao.createRoad("King st.", false);
         dao.updateUserLocation("123123", 0, 0, "Queen St.");
         dao.updateUserLocation("234234", 0, 0, "King st.");
         dao.createRoute("Queen St.", "King st.", 4, false);
         dao.addUser("000", true);
         dao.addUser("111", false);
         dao.updateUserLocation("000", 79.3832, 43.6532, "Dundas St. W");
         dao.updateUserLocation("111", 79.0358, 42.0057, "Main st.");
     }

     @Test
     public void getNavigationPass()  throws JSONException, IOException, InterruptedException {
         JSONObject req = new JSONObject();
         HttpResponse<String> confirmRes = sendRequest("/location/navigation/123123?passengerUid=234234", "GET", req.toString());
         assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
     }

    @Test
    public void getNavigationFail()  throws JSONException, IOException, InterruptedException {
        JSONObject req = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/location/navigation/000?passengerUid=111", "GET", req.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }
    @Test
    public void getNearbyPass()  throws JSONException, IOException, InterruptedException {
        JSONObject req = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/location/nearbyDriver/111?radius=186", "GET", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void getNearbyFail()  throws JSONException, IOException, InterruptedException {
        JSONObject req = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/location/nearbyDriver/111?radius=10", "GET", req.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmRes.statusCode());
    }
}
