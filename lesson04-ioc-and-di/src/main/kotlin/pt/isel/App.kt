package pt.isel

fun main() {
    // resolveDependenciesManuallyByPropertyInjection()
    resolveDependenciesManuallyByConstructorInjection()
}

fun resolveDependenciesManuallyByConstructorInjection() {
    // val lister = MovieLister(MovieFinderCsv(DataSourceClientViaUrl()))
    val lister = MovieLister(MovieFinderCsv(DataSourceClientViaFile()))
    lister
        .moviesDirectedBy("tarantino")
        .forEach { println(it) }
    lister
        .moviesDirectedBy("kubrick")
        .forEach { println(it) }
}

fun resolveDependenciesManuallyByPropertyInjection() {
//    val lister = MovieLister()
//    val finder = MovieFinderCsv()
//    lister.finder = finder
//    lister.moviesDirectedBy("coppola")
}
