package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

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

		try {
				this.collection.insertOne(doc);
				return (ObjectId)doc.get( "_id" );
		} catch (Exception e) {
				System.out.println("Error occurred");
		}
		return null;
	}

	//public String getTripId(String dUid, String pUid, int startTime )

}
