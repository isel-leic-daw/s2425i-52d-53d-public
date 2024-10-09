package pt.isel

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import pt.isel.mem.TransactionManagerInMem

@SpringBootApplication
class TestApp

@Component
class TestConfig {

    private val jdbiContext = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(Environment.getDbUrl())
        },
    ).configureWithAppRequirements()

    @Bean
    fun makeJdbi(): Jdbi {
        return jdbiContext
    }

    @Bean
    @Profile("jdbi")
    fun trxManagerJdbi(jdbi: Jdbi): TransactionManager {
        return TransactionManagerJdbi(jdbi)
    }

    @Bean
    @Profile("inMem")
    fun trxManagerInMem(): TransactionManager {
        return TransactionManagerInMem()
    }
}
