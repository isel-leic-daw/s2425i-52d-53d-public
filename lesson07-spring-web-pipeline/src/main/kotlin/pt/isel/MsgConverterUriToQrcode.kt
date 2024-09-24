package pt.isel.pt.isel

import jdk.jshell.spi.ExecutionControl.NotImplementedException
import net.glxn.qrgen.QRCode
import net.glxn.qrgen.image.ImageType
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractGenericHttpMessageConverter
import org.springframework.stereotype.Component
import java.lang.reflect.Type
import java.net.URI
import kotlin.reflect.full.isSubclassOf

@Component
class MsgConverterUriToQrcode : AbstractGenericHttpMessageConverter<URI>(
    MediaType.IMAGE_PNG
) {
    override fun supports(clazz: Class<*>): Boolean {
        // <=> clazz.isAssignableFrom(URI::class.java)
        return clazz.kotlin.isSubclassOf(URI::class)
    }

    override fun writeInternal(inputUri: URI, type: Type?, outputMessage: HttpOutputMessage) {
        QRCode
            .from(inputUri.toASCIIString())
            .to(ImageType.PNG)
            .withSize(250, 250)
            .stream()
            .writeTo(outputMessage.body)
    }

    override fun readInternal(clazz: Class<out URI>, inputMessage: HttpInputMessage): URI {
        throw NotImplementedException("Not supported read")
    }
    override fun read(type: Type, contextClass: Class<*>?, inputMessage: HttpInputMessage): URI {
        throw NotImplementedException("Not supported read")
    }

}
