package com.yugentech.sessions // Use your package name

import org.junit.Test
import org.junit.Assert.assertEquals

class ValidatorTest {

    @Test
    fun `when password is valid returns true`() {
        // 1. Arrange (Prepare data)
        val input = "password123"

        // 2. Act (Call the function)
        val result = Validator.validatePassword(input)

        // 3. Assert (Check if result is correct)
        // We expect true, and we got 'result'
        assertEquals(true, result)
    }

    @Test
    fun `when password is short returns false`() {
        val input = "123"
        val result = Validator.validatePassword(input)
        
        // We expect false because it is too short
        assertEquals(false, result)
    }
}