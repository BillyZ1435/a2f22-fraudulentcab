package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        params = params[3].split("\\?|=");
        if (params.length != 3 || params[0].isEmpty() || !params[1].equals("passengerUid") || params[2].isEmpty()){
            this.sendStatus(r, 400);
            return;
        }
        
        
        try {
            JSONObject res = new JSONObject();
            JSONObject data = new JSONObject();

            String driverUid = params[0];
            String passengerUid = params[2];
            Result result1 = this.dao.getUserByUid(driverUid);
            Result result2 = this.dao.getUserByUid(passengerUid);
            //Check if users exist
            if (result1.hasNext() && result2.hasNext()) {
                Record driver = result1.next();
                Record passenger = result2.next();

                //Check if users are driver and passenger 
                boolean is_driver = driver.get("n").get("is_driver").asBoolean();
                boolean is_driver2 = passenger.get("n").get("is_driver").asBoolean();

                //Check if users' locations are valid(in DB)
                String driverStreet = driver.get("n").get("street").asString();
                String passengerStreet = passenger.get("n").get("street").asString();
                Result location1 = this.dao.getRoad(driverStreet);
                Result location2 = this.dao.getRoad(passengerStreet);

                if(!(is_driver && !is_driver2) || !location1.hasNext() || !location2.hasNext()){
                    System.out.println(is_driver+", "+is_driver2+", "+location1.hasNext()+", "+location2.hasNext());
                    this.sendStatus(r, 404);
                    return;
                }

                //If they are in the same place
                if(driverStreet.equals(passengerStreet)){
                    data.put("total_time", 0);
                    JSONArray temp = new JSONArray();
                    data.put("route", temp);
                    res.put("status", "OK");
                    res.put("data", data);
                    this.sendResponse(r, res, 200);
                    return;
                }

                //Look for route between them
                Result pathExist = this.dao.getPath(driverStreet, passengerStreet);
                if (!pathExist.hasNext()){
                    System.out.println("No path");
                    this.sendStatus(r, 404);
                    return;
                }
                JSONArray path = new JSONArray();
                Record pathNodes = pathExist.next();
                int totalTime = 0;
                for(int i=0; i<pathNodes.get(0).size(); i++){
                    JSONObject street = new JSONObject();
                    street.put("street", pathNodes.get(0).get(i).get("name").asString());
                    if(i == 0){
                        street.put("time", 0);
                    }else{ 
                        street.put("time", pathNodes.get(1).get(i-1).get("travel_time").asInt());
                        totalTime += pathNodes.get(1).get(i-1).get("travel_time").asInt();
                    }
                    street.put("is_traffic", pathNodes.get(0).get(i).get("has_traffic").asBoolean());
                    
                    path.put(street);
                }

                data.put("total_time", totalTime);
                data.put("route", path);
                res.put("status", "OK");
                res.put("data", data);
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
