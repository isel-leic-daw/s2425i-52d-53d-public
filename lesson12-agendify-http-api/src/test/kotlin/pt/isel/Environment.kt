package pt.isel

object Environment {
    fun getDbUrl() = System.getenv(KEY_DB_URL) ?: DEFAULT_DB_URL

    private const val DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit"
    private const val KEY_DB_URL = "DB_URL"
}