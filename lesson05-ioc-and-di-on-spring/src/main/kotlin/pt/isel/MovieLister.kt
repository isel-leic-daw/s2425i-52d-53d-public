package pt.isel

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

const val top100Movies = "https://gist.githubusercontent.com/fmcarvalho/6d966b2d97d7b268102efa56dc00692c/raw/ffb6ebff59a1862eedf6b9856b0c92a7573d4cda/top_100_movies.csv"

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class MovieLister(private val finder: MovieFinder) {
    /**
     * IoC => Inversion of Control:
     * - This class does NOT instantiate its dependencies
     * - The client is responsible for instantiating the dependency
     */
    // lateinit var finder: MovieFinder

    fun moviesDirectedBy(arg: String): Sequence<Movie> {
        check(finder != null) {"MovieLister requires an instance of MovieFinder!"}
        return finder
            .findAll(top100Movies)
            .filter { it.director.lowercase().contains(arg.lowercase()) }
    }
}