package pt.isel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.pt.isel.MsgConverterUriToQrcode

@SpringBootApplication
class App(val uriToQrCodeConverter: MsgConverterUriToQrcode) : WebMvcConfigurer {

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        super.configureMessageConverters(converters)
        converters.add(uriToQrCodeConverter)
    }
}

fun main() {
    runApplication<App>()
}