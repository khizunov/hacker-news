package com.khizunov.hackernews.service

import com.khizunov.hackernews.enums.Vote
import com.khizunov.hackernews.model.mongo.Post
import com.khizunov.hackernews.repository.PostRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PostsService(private val postRepository: PostRepository) {

    fun createPost(content: String, sourceUrl: String): String {
        val post = Post(Instant.now(), 0, content, sourceUrl)
        return postRepository.save(post).id.toString()
    }

    fun getAllPosts() : List<Post> {
        return postRepository.findAll().toList()
    }

    fun deletePost(id: String) {
        postRepository.deleteById(ObjectId(id))
    }

    fun getTopList(): List<Post> {
        return postRepository
                .findAll(PageRequest.of(0, 30))
                .toList()
    }

    fun updatePost(id: String, newContent: String?, newSourceUrl: String?) {
        postRepository.findById(ObjectId(id)).ifPresent { post ->
            var (createdAt, points, content, sourceUrl) = post
            newContent?.run { content = newContent }
            newSourceUrl?.run { sourceUrl = newSourceUrl }

            updatePost(createdAt, points, content, sourceUrl, post.id)
        }
    }

    fun updateVotes(id: String, vote: Vote) {
        postRepository.findById(ObjectId(id)).ifPresent { post ->
            var (createdAt, points, content, sourceUrl) = post

            when (vote) {
                Vote.Down -> points--
                Vote.Up -> points++
            }

            updatePost(createdAt, points, content, sourceUrl, post.id)
        }
    }

    private fun updatePost(createdAt: Instant, points: Int, content: String, sourceUrl: String, id: ObjectId?) {
        val updatedPost = Post(createdAt, points, content, sourceUrl)
        updatedPost.id = id
        postRepository.save(updatedPost)
    }
}
