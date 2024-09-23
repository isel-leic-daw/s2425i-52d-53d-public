package pt.isel

import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import kotlin.streams.asSequence

@Component
class DataSourceClientViaHttpClient(
    val reqBuilder: HttpRequest.Builder,
    val client: HttpClient,
) : DataSourceClient {

    override fun load(path: String): Sequence<String> {
        val request: HttpRequest = reqBuilder
            .uri(URI.create(path))
            .build()
        return client
            .send(request, BodyHandlers.ofLines())
            .body()        // Stream<String>
            .asSequence()  // Sequence<String>
    }
}