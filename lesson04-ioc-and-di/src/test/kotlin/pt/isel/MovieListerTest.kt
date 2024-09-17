package pt.isel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MovieListerTest {
//    @Test fun `MovieLister fails without a MovieFinder`() {
//        val lister = MovieLister()
//        assertFailsWith<UninitializedPropertyAccessException> {
//            lister.moviesDirectedBy("coppola")
//        }
//    }

    @Test fun `check movies directed by coppola`() {
        val lister = MovieLister(MovieFinderMock())
        val coppolaMovies = lister.moviesDirectedBy("coppola")
        assertEquals(2, coppolaMovies.count())
    }
}