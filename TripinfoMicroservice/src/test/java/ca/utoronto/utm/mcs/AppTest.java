package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {
    final static String API_URL = "http://localhost:8004";
    public static MongoDao dao = new MongoDao();
    public static ObjectId _id;

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeAll
    public static void init() throws IOException, JSONException, InterruptedException{

        /**
         * PUT /location/user/
         * @body uid, is_driver
         * @return 200, 400, 404, 500 
         * Add a user into the database with attributes longitude and latitude 
         * initialized as 0, the “street” attribute must be initially set as 
         * an empty string.
         */
        JSONObject req = new JSONObject();
        req.put("uid", "123123");
        req.put("is_driver", true);
        HttpResponse<String> confirmRes = sendRequest("/location/user", "PUT", req.toString());

        req = new JSONObject();
        req.put("uid", "234234");
        req.put("is_driver", false);
        confirmRes = sendRequest("/location/user", "PUT", req.toString());
        
        /**
         * PUT /location/road/
         * @body roadName, hasTraffic
         * @return 200, 400, 404, 500 
         * Add a road into the database. If the road name
         * already exists in the database, update the rest of the info in the
         * database with the road name.
         */
        req = new JSONObject();
        req.put("roadName", "Queens");
        req.put("hasTraffic", false);
        confirmRes = sendRequest("/location/road", "PUT", req.toString());

        req = new JSONObject();
        req.put("roadName", "Kings");
        req.put("hasTraffic", false);
        confirmRes = sendRequest("/location/road", "PUT", req.toString());

        /**
         * PATCH /location/:uid
         * @param uid
         * @body longitude, latitude, street
         * @return 200, 400, 404, 500
         * Update the user’s location information
         */
        req = new JSONObject();
        req.put("longitude", 43.6532);
        req.put("latitude", 79.3832);
        req.put("street", "Queens");
        confirmRes = sendRequest("/location/123123", "PATCH", req.toString());

        req = new JSONObject();
        req.put("longitude", 43.6532);
        req.put("latitude", 79.3835);
        req.put("street", "Kings");
        confirmRes = sendRequest("/location/234234", "PATCH", req.toString());

        /**
         * POST /location/hasRoute/
         * @body roadName1, roadName2, hasTraffic, time
         * @return 200, 400, 404, 500 
         * Create a connection from a road to another; making
         * a relationship in Neo4j.
         */
        req = new JSONObject();
        req.put("roadName1", "Queens");
        req.put("roadName2", "Kings");
        req.put("hasTraffic", false);
        req.put("time", 5);
        confirmRes = sendRequest("/location/hasRoute", "POST", req.toString());

        // Document doc = new Document();
		// doc.put("driver", 123123);
		// doc.put("passenger", 234234);
		// doc.put("startTime", 1669339818);
		// doc.put("distance", 7);
		// doc.put("endTime", 1615855949);
		// doc.put("timeElapsed", "00:15:00");
		// doc.put("discount", 0);
		// doc.put("totalCost", 14.59);
		// doc.put("driverPayout", 10.02);
        _id = dao.addTrip("123123", "234234", 1669339818);
        dao.addExtraInfo(_id, 7, 1615855949, "00:15:00", 0, 14.59, 10.02);
    }

    @Test
    public void tripRequestPass()  throws JSONException, IOException, InterruptedException {
        String uid = "234234";
        int radius = 1;
        JSONObject req = new JSONObject();
        req.put("uid", uid);
        req.put("radius", radius);
        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void tripRequestFail()  throws JSONException, IOException, InterruptedException {
        int radius = 1;
        JSONObject req = new JSONObject();
        req.put("radius", radius);
        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Test
    public void tripConfirmPass()  throws JSONException, IOException, InterruptedException {
        String driver = "123123";
        String passenger = "234234";
        int startTime = 123456789;
        JSONObject req = new JSONObject();
        req.put("driver", driver);
        req.put("passenger", passenger);
        req.put("startTime", startTime);
        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }

    @Test
    public void tripConfirmFail()  throws JSONException, IOException, InterruptedException {
        String driver = "123123";
        JSONObject req = new JSONObject();
        req.put("driver", driver);
        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", req.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

     
    @Test
    public void patchTripPass()  throws JSONException, IOException, InterruptedException {
        JSONObject req = new JSONObject();
        req.put("distance", 7);
        req.put("endTime", 123123132);
        req.put("timeElapsed", "00:15:00");
        req.put("totalCost", 14.02);
        req.put("discount", 0.2);
        req.put("driverPayout", 10.2);
        HttpResponse<String> confirmRes = sendRequest("/trip/"+_id, "PATCH", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
    @Test
    public void patchTripFail()  throws JSONException, IOException, InterruptedException {
        JSONObject req = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/trip/", "PATCH", req.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmRes.statusCode());
    }

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Test
    public void tripsForPassengerPass()  throws JSONException, IOException, InterruptedException {
        JSONObject req = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/trip/passenger/234234", "GET", req.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmRes.statusCode());
    }
}
