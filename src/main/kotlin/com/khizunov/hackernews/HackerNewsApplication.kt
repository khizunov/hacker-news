package com.khizunov.hackernews

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HackerNewsApplication

fun main(args: Array<String>) {
	runApplication<HackerNewsApplication>(*args)
}
