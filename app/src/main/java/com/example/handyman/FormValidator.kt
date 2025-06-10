package com.example.handyman

object FormValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

    fun validate(
        name: String,
        email: String,
        subject: String,
        message: String,
        category: String
    ): String? {
        if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty() || category.isEmpty()) {
            return "Please fill out all fields."
        }

        if (!email.matches(emailRegex)) {
            return "Please enter a valid email."
        }

        return null
    }
}
