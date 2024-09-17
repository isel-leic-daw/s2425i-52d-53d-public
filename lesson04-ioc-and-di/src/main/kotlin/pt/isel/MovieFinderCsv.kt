package pt.isel

class MovieFinderCsv(private val client: DataSourceClient) : MovieFinder {
    /**
     * IoC => Inversion of Control:
     * * This class does NOT instantiate its dependencies
     * * The client is responsible for instantiating the dependency
     */
    // lateinit var client: DataSourceClient
    override fun findAll(path: String): Sequence<Movie> {
        val lines = client.load(path)
        return lines
            .drop(1)
            .map {
                val words = it.split(",")
                Movie(words[0], words[1].toInt(), words[2])
            }
    }
}