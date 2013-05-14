
db.getCollection('regulation').ensureIndex({'chromosome': 1, "start": 1, "end": 1})
db.getCollection('regulation').ensureIndex({'chromosome': 1, "chunkId": 1})
db.getCollection('regulation').ensureIndex({'features.chromosome': 1, 'features.start': 1, 'features.end': 1})
db.getCollection('regulation').ensureIndex({'features.featuresType': 1})
db.getCollection('regulation').ensureIndex({'features.name': 1})