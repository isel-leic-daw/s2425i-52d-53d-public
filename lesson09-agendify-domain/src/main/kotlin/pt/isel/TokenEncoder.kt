package pt.isel

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
}
