package pt.isel

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

/**
 * A message converter that converts a URI to an image with a QR code to given URI.
 */
@Component
class MsgConverterUrlToQrCode : AbstractGenericHttpMessageConverter<URI>(
    MediaType("image", "png")
) {
    override fun supports(clazz: Class<*>): Boolean {
        return clazz.kotlin.isSubclassOf(URI::class)
    }
    /**
     * Writes into the outputMessage an image with a QR Code for the URI parameter.
     */
    override fun writeInternal(output: URI, type: Type?, outputMessage: HttpOutputMessage) {
        QRCode
            .from(output.toASCIIString())
            .to(ImageType.PNG)
            .withSize(250, 250)
            .stream()
            .writeTo(outputMessage.body)
    }
    override fun read(type: Type, contextClass: Class<*>?, inputMessage: HttpInputMessage): URI {
        throw NotImplementedException("Does not support conversion of Image to URI")
    }

    override fun readInternal(clazz: Class<out URI>, inputMessage: HttpInputMessage): URI {
        throw NotImplementedException("Does not support conversion of Image to URI")
    }
}