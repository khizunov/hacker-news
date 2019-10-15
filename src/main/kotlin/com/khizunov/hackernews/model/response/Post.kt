package com.khizunov.hackernews.model.response

import java.time.Instant

data class Post(val id: String,
                val points: Int = 0,
                val createdAt: Instant,
                val content: String,
                val sourceUrl: String)
