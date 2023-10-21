db = db.getSiblingDB('sample_db');
db.createCollection("accommodations", {
    validator: {$jsonSchema: {
            bsonType: "object",
            required: ["name", "property_type", "review"], // required fields
            properties: {
                name: {
                    bsonType: "string",
                    description: "must be a string and is required"
                },
                property_type: {
                    bsonType: "string",
                    enum : ["Apartment","House", "Guest suite"],
                    description: "must be a string and match one of the given values"
                },
                review: {
                    bsonType: "double",
                    description: "must be a double required"
                }
            }
        }}
})
db.accommodations.insertMany([
    {name: "Double Room en-suite (307)", property_type: "Apartment", review: 4.25},
    {name: "Ribeira Charming Duplex", property_type: "House", review: 3.36},
    {name: "Charming Spacious Park Slope Studio", property_type: "Guest suite", review: 2.85},
    {name: "Gorgeous Remodeled Modern Home w/ Beach Across St.", property_type: "House", review: 1.25},
    {name: "New York City - Upper West Side Apt", property_type: "Apartment", review: 4.25},
    {name: "Country Oasis Retreat", property_type: "Guest suite", review: 3.25},
    {name: "Nice room in Barcelona Center", property_type: "Apartment", review: 2.35},
    {name: "Comfy Studio with private entrance,  awesome views", property_type: "Guest suite", review: 4.85},
    {name: "The Paddington Cottage | Sydney Eastern Suburbs", property_type: "House", review: 3.27}
])