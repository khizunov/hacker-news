package com.khizunov.hackernews.repository

import com.khizunov.hackernews.model.mongo.Post
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : MongoRepository<Post, ObjectId>
