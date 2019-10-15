package com.khizunov.hackernews.controller

import com.khizunov.hackernews.model.response.Post
import com.khizunov.hackernews.request.CreatePostRequest
import com.khizunov.hackernews.request.UpdatePostRequest
import com.khizunov.hackernews.request.VotePostRequest
import com.khizunov.hackernews.service.PostsService
import org.springframework.web.bind.annotation.*
import java.security.InvalidParameterException

@RestController
class PostsController(private val postsService: PostsService) {

    @GetMapping("/posts")
    fun posts(): List<Post> {
        return postsService.getAllPosts().map { p ->
            getPostResponse(p)
        }
    }

    @PostMapping("/posts")
    fun posts(@RequestBody request: CreatePostRequest): String {
        val (content, sourceUrl) = request
        return postsService.createPost(content, sourceUrl)
    }

    @PostMapping("/posts/{id}/votes")
    fun posts(@PathVariable id: String, @RequestBody request: VotePostRequest) {
        postsService.updateVotes(id, request.vote)
    }

    @PatchMapping("/posts/{id}")
    fun posts(@PathVariable id: String, @RequestBody request: UpdatePostRequest) {
        validateId(id)
        val (content, sourceUrl) = request
        postsService.updatePost(id, content, sourceUrl)
    }

    @DeleteMapping("/posts/{id}")
    fun posts(@PathVariable id: String) {
        validateId(id)
        postsService.deletePost(id)
    }

    private fun validateId(id: String) {
        if (id.isBlank()) {
            throw InvalidParameterException("id")
        }
    }

    @GetMapping("/posts/top")
    fun topList() : List<Post> {
        return postsService.getTopList().map { p ->
            getPostResponse(p)
        }
    }

    private fun getPostResponse(p: com.khizunov.hackernews.model.mongo.Post): Post {
        return Post(
                p.id.toString(),
                p.points,
                p.createdAt,
                p.content,
                p.sourceUrl)
    }
}
