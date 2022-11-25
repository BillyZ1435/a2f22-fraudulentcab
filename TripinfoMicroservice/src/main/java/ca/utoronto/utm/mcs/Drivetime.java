package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Drivetime extends Endpoint {
    final static String API_URL = "http://locationmicroservice:8000";
    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        try{
            ObjectId _id = new ObjectId(params[3]);
            JSONObject result = this.dao.getDriverAndPass(_id);
            if(result != null){
                String driver = result.get("driver").toString();
                String passenger = result.get("passenger").toString();
                System.out.println("Sending req");
                HttpResponse<String> res = sendRequest("/location/navigation/"+driver+"?passengerUid="+passenger, "GET", new JSONObject().toString());
                System.out.println("Got res");
                if(res.statusCode() == 200){
                    JSONObject body = new JSONObject(res.body());
                    System.out.println(body.toString());
                    JSONObject data = new JSONObject(body.get("data").toString());
                    System.out.println(data.toString());
                    int time = Integer.parseInt(data.get("total_time").toString());

                    JSONObject returnData = new JSONObject();
                    returnData.put("arrival_time", time);
                    JSONObject returnBody = new JSONObject();
                    returnBody.put("data", returnData);
                    returnBody.put("status", "OK");
                    this.sendResponse(r, returnBody, 200);
                }else{
                    this.sendStatus(r, res.statusCode());
                }
            }else{
                this.sendStatus(r, 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }

    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();
        System.out.println(request.toString());
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
