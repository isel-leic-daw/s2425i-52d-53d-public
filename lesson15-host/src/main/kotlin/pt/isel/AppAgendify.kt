package pt.isel

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@SpringBootApplication
class AppAgendify {
    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(Environment.getDbUrl())
        },
    ).configureWithAppRequirements()

    @Bean
    @Profile("jdbi")
    fun trxManagerJdbi(jdbi: Jdbi): TransactionManagerJdbi {
        return TransactionManagerJdbi(jdbi)
    }
}

fun main() {
    runApplication<AppAgendify>()
}
