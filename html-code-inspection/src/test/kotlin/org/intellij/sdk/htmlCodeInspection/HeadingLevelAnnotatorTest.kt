package org.intellij.sdk.htmlCodeInspection

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import org.junit.Test

class HeadingLevelAnnotatorTest : LightPlatformCodeInsightFixture4TestCase() {
    @Test
    fun `three h3 headings after h1 emit warnings`() {
        myFixture.configureByFile("h3-follows-h1.html")
        myFixture.checkHighlighting()
    }

    @Test
    fun `incorrect hierarchy emit warnings`() {
        myFixture.testHighlighting(true, false, false, "wrong-hierarchy.html")
    }

    @Test
    fun `h5 under h3 under h1 emits warning`() {
        myFixture.configureByFile("h1-h3-h5-hierarchy.html")
        val highlights = myFixture.doHighlighting()
        assertEquals(2, highlights.size)
        with(highlights[0]) {
            assertEquals("Incorrect heading structure: <h3> cannot follow <h1> (previous heading: 'Heading 1')", description)
            assertEquals(HighlightSeverity.WARNING, severity)
        }
        with(highlights[1]) {
            assertEquals("Incorrect heading structure: <h5> cannot follow <h3> (previous heading: 'Heading 3')", description)
            assertEquals(HighlightSeverity.WARNING, severity)
        }
    }

    override fun getTestDataPath() = "src/test/testData"
}