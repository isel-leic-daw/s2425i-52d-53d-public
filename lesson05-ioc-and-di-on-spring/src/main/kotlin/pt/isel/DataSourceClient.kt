package pt.isel

interface DataSourceClient {
    fun load(path: String): Sequence<String>
}
