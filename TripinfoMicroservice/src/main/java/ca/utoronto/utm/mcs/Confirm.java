package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"driver", "passenger", "startTime"};
        Class<?> fieldClasses[] = {String.class, String.class, Integer.class};
        if (!validateFields(body, fields, fieldClasses)) {
            this.sendStatus(r, 400);
            return;
        }

        String driver = body.getString("driver");
        String passenger = body.getString("passenger");
        int startTime = body.getInt("startTime");

        ObjectId result = this.dao.addTrip(driver, passenger, startTime);
        if(result == null){
            this.sendStatus(r, 500);
            return;
        }
        JSONObject res = new JSONObject();
        res.put("status", "OK");
        res.put("data", result);
        this.sendResponse(r, res, 200);
    }
}
