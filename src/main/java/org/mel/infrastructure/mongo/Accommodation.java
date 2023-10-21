package org.mel.infrastructure.mongo;

import org.bson.Document;

public class Accommodation {
    private final String name;
    private final PropertyType type;
    private final double review;

    public Accommodation(Document document) {
        this.name = document.getString("name");
        this.type = PropertyType.of(document.getString("property_type"));
        this.review = document.getDouble("review");
    }

    public String getName() {
        return name;
    }

    public PropertyType getType() {
        return type;
    }

    public double getReview() {
        return review;
    }
}
