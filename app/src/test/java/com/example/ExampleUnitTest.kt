package com.example

import com.example.util.BanglaUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBanglaDigitConversion() {
        assertEquals("০", BanglaUtils.toBanglaDigits(0))
        assertEquals("১", BanglaUtils.toBanglaDigits(1))
        assertEquals("১০০০", BanglaUtils.toBanglaDigits(1000))
        assertEquals("৫০", BanglaUtils.toBanglaDigits(50))
    }

    @Test
    fun testPointToBDTConversion() {
        // 1000 points = 50 BDT (20 points = 1 BDT)
        assertEquals(50L, BanglaUtils.pointsToBDT(1000L))
        assertEquals(1L, BanglaUtils.pointsToBDT(20L))
        assertEquals(5L, BanglaUtils.pointsToBDT(100L))
        assertEquals(250L, BanglaUtils.pointsToBDT(5000L))
    }

    @Test
    fun testBDTToPointsConversion() {
        assertEquals(1000L, BanglaUtils.bdtToPoints(50L))
        assertEquals(2000L, BanglaUtils.bdtToPoints(100L))
    }

    @Test
    fun testFormatStrings() {
        assertEquals("১০০০ পয়েন্ট", BanglaUtils.formatPoints(1000L))
        assertEquals("৫০ ৳", BanglaUtils.formatBDT(50L))
    }
}
