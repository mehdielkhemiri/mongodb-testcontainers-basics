package org.mel.infrastructure.mongo;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.List;
import java.util.function.Function;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.mql.MqlValues.current;

public class Accommodations {
    private static final String DATABASE_NAME = "sample_db";
    private static final String COLLECTION_NAME = "accommodations";
    private final MongodbQueryExecutor mongodbQueryExecutor;

    public Accommodations(MongodbQueryExecutor mongodbQueryExecutor) {
        this.mongodbQueryExecutor = mongodbQueryExecutor;
    }

    public List<Accommodation> findByName(String name) {
        Function<MongoCollection<Document>, MongoIterable<Document>> query = collection -> collection.find(eq("name", name));
        return mongodbQueryExecutor.connectAndExecute(DATABASE_NAME, COLLECTION_NAME, query)
                .stream()
                .map(Accommodation::new)
                .toList();


    }

    public List<Accommodation> findByType(PropertyType propertyType) {
        Function<MongoCollection<Document>, MongoIterable<Document>> query = collection -> collection.find(eq("property_type", propertyType.getValue()))
                .sort(Sorts.ascending("name"));
        return mongodbQueryExecutor.connectAndExecute(DATABASE_NAME, COLLECTION_NAME, query)
                .stream()
                .map(Accommodation::new)
                .toList();
    }

    public List<Accommodation> findByReviewGte(double review) {
        Function<MongoCollection<Document>, MongoIterable<Document>> query = collection -> collection.find(gte("review", review))
                .sort(Sorts.ascending("name"));
        return mongodbQueryExecutor.connectAndExecute(DATABASE_NAME, COLLECTION_NAME, query)
                .stream()
                .map(Accommodation::new)
                .toList();
    }

    public List<PropertyTypeWithReview> sortTypesByReviewAvgInDescendingOrder() {
        Function<MongoCollection<Document>, AggregateIterable<Document>> query = collection -> collection.aggregate(
                List.of(
                        group(
                                current().getString("property_type"),
                                avg("reviewAvg", current().getNumber("review"))
                        ),
                        sort(Sorts.descending("reviewAvg"))
                )
        );
        return mongodbQueryExecutor.connectAndExecute(DATABASE_NAME, COLLECTION_NAME, query)
                .stream()
                .map(PropertyTypeWithReview::new)
                .toList();

    }
}
