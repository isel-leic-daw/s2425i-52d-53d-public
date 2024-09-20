package pt.isel

import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.net.http.HttpClient
import java.net.http.HttpRequest

@Component
class AppConfiguration {
    @Bean
    fun createHttpRequestBuilder(): HttpRequest.Builder {
        return HttpRequest.newBuilder()
    }
    @Bean
    fun createHttpClient(): HttpClient {
        return  HttpClient.newBuilder().build()
    }
}

fun main() {
    val context = AnnotationConfigApplicationContext()
    context.scan("pt.isel") // lookup for classes in given package
    context.refresh()       // look for compatible Components
    val lister = context.getBean(MovieLister::class.java) // <=> loadInstanceOf(Klass)
    println(lister)

    // Returns the same instance for Components with Scope singleton (by default)
    // different objects for Scope prototype.
//    println(context.getBean(MovieLister::class.java))
//    println(context.getBean(MovieLister::class.java))

    lister
        .moviesDirectedBy("tarantino")
        .forEach { println(it) }
//    lister
//        .moviesDirectedBy("kubrick")
//        .forEach { println(it) }
}