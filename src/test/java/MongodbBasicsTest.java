import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mel.infrastructure.mongo.Accommodation;
import org.mel.infrastructure.mongo.Accommodations;
import org.mel.infrastructure.mongo.MongodbQueryExecutor;
import org.mel.infrastructure.mongo.PropertyType;
import org.mel.infrastructure.mongo.PropertyTypeWithReview;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Testcontainers
public class MongodbBasicsTest {
    @Container
    private final static MongoDBContainer mongodb = new MongoDBContainer(DockerImageName.parse("mongo:latest"))
            .withClasspathResourceMapping(
                    "mongo-init.js",
                    "/docker-entrypoint-initdb.d/mongo-init.js"
                    , BindMode.READ_ONLY
            );
    private final MongoClient mongoClient = MongoClients.create(mongodb.getConnectionString());
    private final Accommodations accommodations = new Accommodations(new MongodbQueryExecutor(mongodb.getConnectionString()));

    @Test
    @DisplayName("show dbs")
    void listDatabases() {
        String[] expectedDatabases = {"admin", "config", "local", "sample_db"};
        MongoIterable<String> databases = mongoClient.listDatabaseNames();

        assertThat(databases).containsExactlyInAnyOrder(expectedDatabases);
    }


    @Test
    @DisplayName("db.accommodations.find({name : 'Ribeira Charming Duplex'})")
    void findByName() {
        List<Accommodation> actual = accommodations.findByName("Ribeira Charming Duplex");

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual).extracting(Accommodation::getName)
                        .containsOnly("Ribeira Charming Duplex"),
                () -> assertThat(actual).extracting(Accommodation::getType)
                        .containsOnly(PropertyType.House),
                () -> assertThat(actual).extracting(Accommodation::getReview)
                        .containsOnly(3.36)
        );
    }

    @Test
    @DisplayName("db.accommodations.find({property_type : 'Apartment'}).sort({name : 1})")
    void findByTypeAndSortByName() {
        List<Accommodation> actual = accommodations.findByType(PropertyType.Apartment);

        assertAll(
                () -> assertThat(actual).hasSize(3),
                () -> assertThat(actual).extracting(Accommodation::getName).containsExactly
                        ("Double Room en-suite (307)", "New York City - Upper West Side Apt", "Nice room in Barcelona Center"),
                () -> assertThat(actual).extracting(Accommodation::getType).containsExactly
                        (PropertyType.Apartment, PropertyType.Apartment, PropertyType.Apartment),
                () -> assertThat(actual).extracting(Accommodation::getReview).containsExactly
                        (4.25, 4.25, 2.35)
        );
    }

    @Test
    @DisplayName("db.accommodations.find({review: { $gte : 4}})")
    void comparisonGte() {
        List<Accommodation> actual = accommodations.findByReviewGte(4.0);

        assertAll(
                () -> assertThat(actual).hasSize(3),
                () -> assertThat(actual).extracting(Accommodation::getName).containsExactly
                        ("Comfy Studio with private entrance,  awesome views", "Double Room en-suite (307)", "New York City - Upper West Side Apt"),
                () -> assertThat(actual).extracting(Accommodation::getType).containsExactly
                        (PropertyType.GuestSuite, PropertyType.Apartment, PropertyType.Apartment),
                () -> assertThat(actual).extracting(Accommodation::getReview).containsExactly
                        (4.85, 4.25, 4.25)
        );
    }

    @Test
    void aggregationsAndSort() {
        List<PropertyTypeWithReview> actual = accommodations.sortTypesByReviewAvgInDescendingOrder();

        assertAll(
                () -> assertThat(actual).hasSize(3),
                () -> assertThat(actual).extracting(PropertyTypeWithReview::getType).containsExactly
                        (PropertyType.GuestSuite, PropertyType.Apartment, PropertyType.House),
                () -> assertThat(actual).extracting(PropertyTypeWithReview::getReview).containsExactly
                        (3.65, 3.62, 2.63)
        );

    }
}
