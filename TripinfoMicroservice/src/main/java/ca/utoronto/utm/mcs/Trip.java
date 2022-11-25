package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 3 || params[2].isEmpty()) {
            System.out.println("params");
            this.sendStatus(r, 400);
            return;
        }

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"distance", "endTime", "timeElapsed", "totalCost", "driverPayout"};
        Class<?> fieldClasses[] = {Integer.class, Integer.class, String.class, Double.class, Double.class, Double.class};
        if (!validateFields(body, fields, fieldClasses)) {
            this.sendStatus(r, 400);
            return;
        }
        
        String fields2[] = {"discount"};
        Class<?> fieldClasses2[] = {Integer.class};
        Class<?> fieldClasses3[] = {Double.class};
        if (!(validateFields(body, fields2, fieldClasses2) || validateFields(body, fields2, fieldClasses3))) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            ObjectId _id = new ObjectId(params[2]);
            Boolean res = this.dao.addExtraInfo(_id, 
                body.getInt("distance"), 
                body.getInt("endTime"), 
                body.getString("timeElapsed"), 
                body.getDouble("discount"), 
                body.getDouble("totalCost"), 
                body.getDouble("driverPayout"));

            if(!res){
                this.sendStatus(r, 500);
                return;
            }else{
                JSONObject status = new JSONObject();
                status.put("status", "OK");
                this.sendResponse(r, status, 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
