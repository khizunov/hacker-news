package com.khizunov.hackernews.repository

import com.khizunov.hackernews.model.TopList
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface TopListRepository : MongoRepository<TopList, ObjectId>
