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
import java.util.Iterator;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Request extends Endpoint {
    final static String API_URL = "http://locationmicroservice:8000";
    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException,JSONException{
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 3) {
            this.sendStatus(r, 400);
            return;
        }

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"uid", "radius"};
        Class<?> fieldClasses[] = {String.class, Integer.class};
        if (!validateFields(body, fields, fieldClasses)) {
            this.sendStatus(r, 400);
            return;
        }

        String uid = body.getString("uid");
        String radius = body.getString("radius");

        try {
            System.out.println("Sent res");
            HttpResponse<String> res = sendRequest("/location/nearbyDriver/"+uid+"?radius="+radius, "GET", new JSONObject().toString());
            System.out.println("Got res");
            if(res.statusCode() == 200){
                JSONObject resBody = new JSONObject(res.body());
                JSONObject data = new JSONObject(resBody.get("data").toString());
                Iterator<String> keys = data.keys();

                JSONArray uids = new JSONArray();
                while(keys.hasNext()){
                    String key = keys.next();
                    uids.put(key);
                }

                JSONObject returnBody = new JSONObject();
                returnBody.put("data", uids);
                returnBody.put("status", "OK");
                this.sendResponse(r, returnBody, 200);
            }else{
                this.sendStatus(r, res.statusCode());
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
