package com.khizunov.hackernews.model

import com.khizunov.hackernews.model.mongo.Post
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class TopList(var posts: List<Post> = ArrayList()) {
    @Id
    var id: ObjectId? = ObjectId()
}
