package com.khizunov.hackernews.service

import com.khizunov.hackernews.enums.Vote
import com.khizunov.hackernews.model.TopList
import com.khizunov.hackernews.model.mongo.Post
import com.khizunov.hackernews.repository.PostRepository
import com.khizunov.hackernews.repository.TopListRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PostsService(private val postRepository: PostRepository,
                   private val topListRepository: TopListRepository) {

    fun createPost(content: String, sourceUrl: String): String {
        val createdAt = Instant.now()
        val post = Post(createdAt, 0, content, sourceUrl, createdAt.toEpochMilli())

        updateTop(post)
        return postRepository.save(post).id.toString()
    }

    fun getAllPosts() : List<Post> {
        return postRepository.findAll().toList()
    }

    fun deletePost(id: String) {
        val objectId = ObjectId(id)
        postRepository.deleteById(objectId)

        val topList = topListFromRepo()!!
        val posts = topList.posts.toMutableList()

        posts.firstOrNull { p -> p.id != null && p.id!! == objectId }?.run {
            val maxByScore = postRepository.findAll().maxBy { p -> p.score }!!
            posts.remove(this)
            posts.add(maxByScore)
            topList.posts = posts
            topListRepository.save(topList)
        }
    }

    fun getTopList(): List<Post> {
        return topListFromRepo().posts.sortedByDescending { p -> p.score }
    }



    fun updatePost(id: String, newContent: String?, newSourceUrl: String?) {
        postRepository.findById(ObjectId(id)).ifPresent { post ->
            var (createdAt, points, content, sourceUrl) = post
            newContent?.run { content = newContent }
            newSourceUrl?.run { sourceUrl = newSourceUrl }

            updatePost(createdAt, points, content, sourceUrl, post.id, post.score)
        }
    }

    fun updateVotes(id: String, vote: Vote) {
        postRepository.findById(ObjectId(id)).ifPresent { post ->
            var (createdAt, points, content, sourceUrl) = post

            when (vote) {
                Vote.Down -> points--
                Vote.Up -> points++
            }

            val newScore = calcScore(points, createdAt)
            val updatedPost = updatePost(createdAt, points, content, sourceUrl, post.id, newScore)
            updateTop(updatedPost)
        }
    }

    private fun updatePost(createdAt: Instant, points: Int, content: String, sourceUrl: String, id: ObjectId?, score: Long): Post {
        val updatedPost = Post(createdAt, points, content, sourceUrl, score)
        updatedPost.id = id
        return postRepository.save(updatedPost)
    }

    private fun updateTop(post: Post) {
        val topList = topListFromRepo()

        var posts = topList
                .posts
                .toMutableList()

        val first = posts.firstOrNull { p -> Objects.equals(p.id, post.id) }

        if (first != null) {
            posts.remove(first)
            posts.add(post)
        } else {
            if (posts.size < 30) {
                posts.add(post)
            } else {
                val minScore = posts.minBy { i -> i.score }!!.score

                if (minScore < post.score) {
                    posts = insertToTop(posts, post)
                }
            }
        }

        topList.posts = posts
        topListRepository.save(topList)
    }

    private fun topListFromRepo(): TopList {
        return topListRepository
                .findAll()
                .firstOrNull() ?: topListRepository.save(TopList())
    }

    private fun insertToTop(topList: MutableList<Post>, post: Post): MutableList<Post> {
        topList.sortBy { p -> p.score }
        val postToReplace = topList.findLast { p -> p.score < post.score }
        topList.remove(postToReplace)
        topList.add(post)

        return topList
    }

    private fun calcScore(points: Int, createdAt: Instant): Long {
        return createdAt.toEpochMilli() + points * 10
    }
}
