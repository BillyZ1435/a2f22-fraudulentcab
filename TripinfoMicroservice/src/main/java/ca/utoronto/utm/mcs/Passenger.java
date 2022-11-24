package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException,JSONException{
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            String uid = params[3];
            JSONArray result = this.dao.getDriverTrip(uid);
            if (result != null) {
                JSONObject res = new JSONObject();
                res.put("status", "OK");
                JSONObject trips = new JSONObject();
                trips.put("trips", result);
                res.put("data", trips);
                this.sendResponse(r, res, 200);
            } else {
                this.sendStatus(r, 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
    }
}
