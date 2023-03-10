package ca.utoronto.utm.mcs;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class MongoDao {

	private final String username = "root";
	private final String password = "123456";
	private final String uriDb;
	private final String dbName = "tripInfo";
	private final String collectionName = "trips";
	private final int port = 27017;
	public MongoCollection<Document> collection;

	public MongoDao() {
		Dotenv dotenv = Dotenv.load();
    String addr = dotenv.get("MONGODB_ADDR");
		uriDb = String.format("mongodb://%s:%s@%s:%s/%s?authSource=admin", username, password, addr, port, dbName);
		MongoClient mongoClient = MongoClients.create(this.uriDb);
		MongoDatabase database = mongoClient.getDatabase(this.dbName);
		this.collection = database.getCollection(this.collectionName);
	}

	// *** implement database operations here *** //
	public FindIterable<Document> getTrip() {
		try {
				return this.collection.find();
		} catch (Exception e) {
				System.out.println("Error occurred");
		}
		return null;
	}

	public ObjectId addTrip(String dUid, String pUid, int startTime) {
		Document doc = new Document();
		doc.put("driver", dUid);
		doc.put("passenger", pUid);
		doc.put("startTime", startTime);
		doc.put("distance", null);
		doc.put("endTime", null);
		doc.put("timeElapsed", null);
		doc.put("discount", null);
		doc.put("totalCost", null);
		doc.put("driverPayout", null);

		try {
				this.collection.insertOne(doc);
				return (ObjectId)doc.get( "_id" );
		} catch (Exception e) {
				System.out.println("Error occurred");
		}
		return null;
	}

	public JSONObject getDriverAndPass(ObjectId _id){
		try {
			FindIterable<Document> docs = this.collection.find(eq("_id", _id));
			if(docs == null){
				return null;
			}
			JSONObject res = new JSONObject();
			for(Document doc : docs){
				res.put("driver", doc.get("driver"));
				res.put("passenger", doc.get("passenger"));
				break;
			}
			return res;
		} catch (Exception e) {
			System.out.println("Error occurred");
		}
		return null;
	}

	public JSONArray getDriverTrip(String uid) {
		try {
				FindIterable<Document> docs = this.collection.find(eq("driver", uid));
				JSONArray trips = new JSONArray();
				int size = 0;
				for(Document doc : docs){
					size++;
					JSONObject trip = new JSONObject();
					trip.put("_id", doc.get("_id"));
					trip.put("distance", doc.get("distance"));
					trip.put("startTime", doc.get("startTime"));
					trip.put("endTime", doc.get("endTime"));
					trip.put("timeElapsed", doc.get("timeElapsed"));
					trip.put("passenger", doc.get("passenger"));
					trip.put("driverPayout", doc.get("driverPayout"));
					trips.put(trip);
				}
				if(size == 0){
					return null;
				}
				return trips;
		} catch (Exception e) {
				System.out.println("Error occurred");
		}
		return null;
	}

	//public String getTripId(String dUid, String pUid, int startTime )
	public boolean addExtraInfo(ObjectId _id, int dist, int endTime, String timeElapsed, double discount, double totalCost, double driverPayout){
		Document query = new Document().append("_id", _id);
		
		Bson updates = Updates.combine(
			Updates.set("distance", dist),
			Updates.set("endTime", endTime),
			Updates.set("timeElapsed", timeElapsed),
			Updates.set("discount", discount),
			Updates.set("totalCost", totalCost),
			Updates.set("driverPayout", driverPayout)
		);

		try{
			UpdateResult result = this.collection.updateOne(query, updates);
			if(result.getModifiedCount() == 1){
				return true;
			}
		}catch (Exception e){
			System.out.println("Error occurred");
		}
		return false;
	}

	public JSONArray getPassengerTrip(String uid) {
		try {
				FindIterable<Document> docs = this.collection.find(eq("passenger", uid));
				JSONArray trips = new JSONArray();
				int size = 0;
				for(Document doc : docs){
					size++;
					JSONObject trip = new JSONObject();
					trip.put("_id", doc.get("_id"));
					trip.put("distance", doc.get("distance"));
					trip.put("totalCost", doc.get("totalCost"));
					trip.put("discount", doc.get("discount"));
					trip.put("startTime", doc.get("startTime"));
					trip.put("endTime", doc.get("endTime"));
					trip.put("timeElapsed", doc.get("timeElapsed"));
					trip.put("driver", doc.get("driver"));
					trips.put(trip);
				}
				if(size == 0){
					return null;
				}
				return trips;
		} catch (Exception e) {
				System.out.println("Error occurred");
		}
		return null;
	}
}
