package com.example.financetracker.utils

fun cleanMoneyInput(input: String): String {
    return buildString {
        var dotAlreadyUsed = false
        input.forEach { char ->
            when {
                char.isDigit() -> append(char)
                char == '.' && !dotAlreadyUsed -> {
                    append(char)
                    dotAlreadyUsed = true
                }
            }
        }
    }
}

fun Double.toCleanMoneyString(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        this.toString()
    }
}