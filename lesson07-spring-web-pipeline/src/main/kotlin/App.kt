package pt.isel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@SpringBootApplication
class App(val convUriToQrCode: MsgConverterUrlToQrCode) : WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(convUriToQrCode)
    }

}


fun main() {
    runApplication<App>()
}