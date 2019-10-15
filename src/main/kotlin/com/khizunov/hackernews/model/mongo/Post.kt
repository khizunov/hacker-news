package com.khizunov.hackernews.model.mongo

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Post(@Indexed(name = "createdAt_index", direction = IndexDirection.ASCENDING)
                val createdAt: Instant,
                @Indexed(name = "points_index", direction = IndexDirection.DESCENDING)
                val points: Int = 0,
                val content: String,
                val sourceUrl: String) {
    @Id
    var id: ObjectId? = ObjectId()
}
