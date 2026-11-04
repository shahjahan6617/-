package com.example.util

object BanglaUtils {
    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun toBanglaDigits(number: Long): String {
        return toBanglaDigits(number.toString())
    }

    fun toBanglaDigits(number: Int): String {
        return toBanglaDigits(number.toString())
    }

    fun toBanglaDigits(numberStr: String): String {
        val sb = java.lang.StringBuilder()
        for (char in numberStr) {
            if (char in '0'..'9') {
                sb.append(bengaliDigits[char - '0'])
            } else {
                sb.append(char)
            }
        }
        return sb.toString()
    }

    fun formatPoints(points: Long): String {
        return "${toBanglaDigits(points)} পয়েন্ট"
    }

    fun formatBDT(bdt: Long): String {
        return "${toBanglaDigits(bdt)} ৳"
    }

    // Official conversion rate: 1000 Points = 50 BDT (20 Points = 1 BDT)
    fun pointsToBDT(points: Long): Long {
        return points / 20
    }

    fun bdtToPoints(bdt: Long): Long {
        return bdt * 20
    }
}
