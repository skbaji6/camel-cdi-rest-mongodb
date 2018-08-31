package org.apache.camel.example.cdi.rest.servlet;

import javax.annotation.PreDestroy;

import org.bson.Document;
import org.json.JSONArray;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoBean {

	static final String EMPLOYEE = "Employee";
	static final String EMP_ID = "EmpId";

	private MongoClient mongoClient;

	private MongoCollection employeeColl;

	public MongoBean() {
		super();
		setup();
	}

	public void setup() {
		String mongoURI = "mongodb://localhost:27017/camelartemis";
		String dbname = "camelartemis";

		// Connect to Mongo

		MongoClientURI connectionString = new MongoClientURI(mongoURI);
		mongoClient = new MongoClient(connectionString);

		MongoDatabase db = mongoClient.getDatabase(dbname);
		if (db == null) {
			System.out.println("Error: MongoDB handle is null");
		} else {
			this.employeeColl = db.getCollection(EMPLOYEE);
			if (employeeColl == null) {
				System.out.println("Error: EMPLOYEE Collection handle is null");
			}
		}

	}

	public MongoCollection getEmployeeColl() {
		setup();
		return this.employeeColl;
	}

	public void addEmployee(Document icm) {
		if (icm != null) {
			this.employeeColl.insertOne(icm);
		}
	}

	public JSONArray findAllEmployees() {
		JSONArray results = new JSONArray();
		FindIterable<Document> search = this.employeeColl.find();

		for (Document d : search) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}

	public JSONArray findEmployeeById(String msgId) {
		JSONArray results = new JSONArray();
		FindIterable<Document> search = this.employeeColl
				.find(new BasicDBObject(EMP_ID, msgId));

		for (Document d : search) {
			d.remove("_id");
			results.put(d);
		}
		return results;
	}

	@PreDestroy
	private void cleanup() {
		mongoClient.close();
	}

	public String insertEmployee(String id, String name) {
		Document employeeDoc = new Document();
		employeeDoc.put(EMP_ID, id);
		employeeDoc.put("test", name);
		addEmployee(employeeDoc);
		return "Success";
	}

}
