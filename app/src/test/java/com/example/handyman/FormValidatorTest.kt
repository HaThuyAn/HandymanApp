package com.example.handyman

import org.junit.Assert.*
import org.junit.Test

class FormValidatorTest {

    @Test
    fun emptyFields_returnError() {
        val result = FormValidator.validate("", "a@b.com", "sub", "msg", "General Inquiry")
        assertEquals("Please fill out all fields.", result)
    }

    @Test
    fun invalidEmail_returnError() {
        val result = FormValidator.validate("Name", "invalidemail", "sub", "msg", "Technical Issue")
        assertEquals("Please enter a valid email.", result)
    }

    @Test
    fun validInput_returnNull() {
        val result = FormValidator.validate("Name", "valid@email.com", "Subject", "Message", "Billing & Payments")
        assertNull(result)
    }
}
