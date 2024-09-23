package pt.isel

import org.springframework.stereotype.Component
import java.net.URI

// @Component
class DataSourceClientViaUrl : DataSourceClient {
    override fun load(path: String): Sequence<String> {
        return URI(path)
            .toURL()
            .openStream()
            .bufferedReader()
            .lineSequence()
    }
}