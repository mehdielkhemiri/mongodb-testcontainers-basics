package org.mel.infrastructure.mongo;

import org.bson.Document;

public class PropertyTypeWithReview {
    private final PropertyType type;
    private final double review;

    public PropertyTypeWithReview(Document document) {
        this.type = PropertyType.of(document.getString("_id"));
        this.review = ((double) Math.round(document.getDouble("reviewAvg") * 100)) / 100;
    }

    public PropertyType getType() {
        return type;
    }

    public double getReview() {
        return review;
    }
}
