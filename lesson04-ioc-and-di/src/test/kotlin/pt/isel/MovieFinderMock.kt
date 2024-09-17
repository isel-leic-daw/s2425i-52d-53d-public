package pt.isel

class MovieFinderMock : MovieFinder {
    val top100movies= sequenceOf(
        Movie("The Shawshank Redemption",1994,"Frank Darabont"),
        Movie("The Godfather",1972,"Francis Ford Coppola"),
        Movie("The Godfather: Part II",1974,"Francis Ford Coppola"),
        Movie("The Dark Knight",2008,"Christopher Nolan"),
        Movie("12 Angry Men",1957,"Sidney Lumet")
    )
    override fun findAll(path: String) = top100movies
}
