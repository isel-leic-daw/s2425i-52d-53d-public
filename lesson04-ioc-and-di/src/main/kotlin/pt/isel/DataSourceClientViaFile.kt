package pt.isel

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