package pt.isel

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val passwordValidation: PasswordValidationInfo,
)
