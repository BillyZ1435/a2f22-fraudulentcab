package ca.utoronto.utm.mcs;

import java.io.IOException;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Nearby extends Endpoint {

    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     *
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        int radius;
        String[] params = r.getRequestURI().toString().split("/|\\?|=");
        // params[3] is UID params[5] is radius
        if (params.length != 6
                || params[3].isEmpty()
                || !params[4].equals("radius")
        ) {
            this.sendStatus(r, 400);
            return;
        }
        // Check if radius parameter is an integer that is positive
        try {
            radius = Integer.parseInt(params[5]);
            if (radius < 1) {
                this.sendStatus(r, 400);
                return;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            this.sendStatus(r, 400);
            return;
        }
        // Get nearby
        try {
            String uid = params[3];
            Result result = this.dao.getUserLocationByUid(uid);
            if (result.hasNext()) {
                Record user = result.next();
                Double longitude = user.get("n.longitude").asDouble();
                Double latitude = user.get("n.latitude").asDouble();

                // Case where user does not have location set up.
                if(longitude == 0 && latitude == 0){
                    this.sendStatus(r, 404);
                    return;
                }
                // Get Nearby drivers based on long/lat
                JSONObject res = new JSONObject();
                result = this.dao.getNearby(longitude, latitude, radius);
                String street, driverUID;
                Double dist;
                JSONObject data = new JSONObject();
                while (result.hasNext()){
                    Record driver = result.next();
                    longitude = driver.get("longitude").asDouble();
                    latitude = driver.get("latitude").asDouble();
                    street = driver.get("street").asString();
                    driverUID = driver.get("uid").asString();
                    //debug
//                    dist = driver.get("dist").asDouble();

                    JSONObject entry = new JSONObject();
                    entry.put("longitude", longitude);
                    entry.put("latitude", latitude);
                    entry.put("street", street);

                    //debug
//                    entry.put("dist", dist);
                    data.put(driverUID, entry);
                }
                res.put("status", "OK");
                res.put("data", data);
                if(data.length()!=0)
                    this.sendResponse(r, res, 200);
                else{
                    this.sendStatus(r, 404);
                }
            } else {
                this.sendStatus(r, 404);
            }
        }catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
