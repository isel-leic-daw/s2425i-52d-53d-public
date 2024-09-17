package pt.isel

interface MovieFinder {
    fun findAll(path: String): Sequence<Movie>
}
