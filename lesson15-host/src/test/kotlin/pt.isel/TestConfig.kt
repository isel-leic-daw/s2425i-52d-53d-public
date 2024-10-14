package pt.isel

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import pt.isel.mem.TransactionManagerInMem

@Component
class TestConfig {
    @Bean
    @Profile("inMem")
    fun trxManagerInMem(): TransactionManager = TransactionManagerInMem()
}
