package pt.isel

fun main() {
    // resolveDependenciesManuallyByPropertyInjection()
    // resolveDependenciesManuallyByConstructorInjection()
    resolveWithIocContainer()
}

fun resolveWithIocContainer() {
    loadInstanceOf(DataSourceClient::class)
        .also { println(it) }
    loadInstanceOf(MovieFinder::class)
        .also { println(it) }
    loadInstanceOf(MovieFinder::class)
        .also { println(it) }
    val lister = loadInstanceOf(MovieLister::class)
    lister
        .moviesDirectedBy("tarantino")
        .forEach { println(it) }
    lister
        .moviesDirectedBy("kubrick")
        .forEach { println(it) }
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
