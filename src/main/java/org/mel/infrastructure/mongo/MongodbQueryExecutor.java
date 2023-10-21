package org.mel.infrastructure.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class MongodbQueryExecutor {
    private final String mongodbConnectionString;

    public MongodbQueryExecutor(String mongodbConnectionString) {
        this.mongodbConnectionString = mongodbConnectionString;
    }

    public List<Document> connectAndExecute(String db, String collectionName, Function<MongoCollection<Document>, ? extends MongoIterable<Document>> query) {
        try (MongoClient mongoClient = MongoClients.create(mongodbConnectionString)) {
            MongoCollection<Document> collection = mongoClient.getDatabase(db)
                    .getCollection(collectionName);
            return StreamSupport.stream(query.apply(collection).spliterator(), false)
                    .toList();
        }
    }
}
