package pt.isel

import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main() {
    val context = AnnotationConfigApplicationContext()
    context.scan("pt.isel") // lookup for classes in given package
    context.refresh()       // look for compatible Components
    val lister = context.getBean(MovieLister::class.java) // <=> loadInstanceOf(Klass)
    println(lister)

    // Returns the same instance for Components with Scope singleton (by default)
    // different objects for Scope prototype.
    println(context.getBean(MovieLister::class.java))

//    lister
//        .moviesDirectedBy("tarantino")
//        .forEach { println(it) }
//    lister
//        .moviesDirectedBy("kubrick")
//        .forEach { println(it) }
}