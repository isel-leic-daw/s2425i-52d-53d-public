package pt.isel

import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

// @Component
// @Primary
class DataSourceClientViaFile : DataSourceClient {
    override fun load(path: String): Sequence<String> {
        val file = path.split("/").last()
        return ClassLoader
            .getSystemResource(file)
            .openStream()
            .bufferedReader()
            .lineSequence()
    }
}