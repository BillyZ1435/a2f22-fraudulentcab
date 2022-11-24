package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class RequestRouter implements HttpHandler {

    /**
     * You may add and/or initialize attributes here if you
     * need.
     */
    public HashMap<Integer, String> errorMap;

    public RequestRouter() {
        errorMap = new HashMap<>();
        errorMap.put(200, "OK");
        errorMap.put(400, "BAD REQUEST");
        errorMap.put(401, "UNAUTHORIZED");
        errorMap.put(404, "NOT FOUND");
        errorMap.put(405, "METHOD NOT ALLOWED");
        errorMap.put(409, "CONFLICT");
        errorMap.put(500, "INTERNAL SERVER ERROR");
    }

    @Override
    public void handle(HttpExchange r) throws IOException {
        r.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // For CORS
        String[] params = r.getRequestURI().toString().split("/");
        try {
            switch (params[1]) {
                case "location":
                    this.forwardToLocation(r);
                    break;
                case "trip":
                    this.forwardToTripinfo(r);
                    break;
                case "user":
                    this.forwardToUser(r);
                    break;
                default:
                    this.sendStatus(r, 500);//should not happen if reached this context so any status works.
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void forwardToUser(HttpExchange r) throws IOException, InterruptedException, JSONException {
        System.out.println("forwarding to user...");
        String method = r.getRequestMethod();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://usermicroservice:8000"+r.getRequestURI()))
                .method(method, HttpRequest.BodyPublishers.ofString(Utils.convert(r.getRequestBody())))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        sendResponse(r, response.body(), response.statusCode());

    }

    private void forwardToTripinfo(HttpExchange r) throws IOException, InterruptedException, JSONException {
        System.out.println("forwarding to tripinfo...");
        String method = r.getRequestMethod();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://tripinfomicroservice:8000"+r.getRequestURI()))
                .method(method, HttpRequest.BodyPublishers.ofString(Utils.convert(r.getRequestBody())))
                .build();
                System.out.println(r.getRequestURI());
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        sendResponse(r, response.body(), response.statusCode());
    }

    private void forwardToLocation(HttpExchange r) throws IOException, InterruptedException, JSONException {
		System.out.println("forwarding to location...");
		String method = r.getRequestMethod();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://locationmicroservice:8000"+r.getRequestURI()))
				.method(method, HttpRequest.BodyPublishers.ofString(Utils.convert(r.getRequestBody())))
				.build();
                System.out.println(r.getRequestURI());
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        sendResponse(r, response.body(), response.statusCode());
    }

    public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }
    public void sendResponse(HttpExchange r, String response, int statusCode) throws JSONException, IOException {
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }
    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}