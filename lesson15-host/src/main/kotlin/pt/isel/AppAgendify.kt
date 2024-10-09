package pt.isel

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
class AppAgendifyConfig {
    private val jdbiContext = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
        },
    ).configureWithAppRequirements()

    @Bean
    fun makeJdbi(): Jdbi {
        return jdbiContext
    }

    @Bean
    @Profile("jdbi")
    fun trxManagerJdbi(jdbi: Jdbi): TransactionManagerJdbi {
        return TransactionManagerJdbi(jdbi)
    }
}

@SpringBootApplication
class App

fun main() {
    runApplication<App>()
}
