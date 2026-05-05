package org.intellij.sdk.htmlCodeInspection

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity.WARNING
import com.intellij.psi.PsiElement
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlToken
import com.intellij.psi.xml.XmlTokenType.XML_NAME

private const val NOT_A_HEADING = Int.MAX_VALUE

class HeadingLevelAnnotator : Annotator {

    override fun annotate(element: PsiElement, annotations: AnnotationHolder) {
        val tag = element as? HtmlTag ?: return
        val level = tag.headingLevel
        if (level == 1 || level == NOT_A_HEADING) return

        val previousHeading = findPreviousRelevantHeading(tag, level)

        if (previousHeading == null) {
            annotations.warn(tag, "Orphaned heading: <${tag.name}> has no previous heading")
            return
        }
        if (previousHeading.headingLevel != level - 1) {
            val previousText = previousHeading.value.text.trim().ifEmpty { "(empty)" }
            annotations.warn(tag, "Incorrect heading structure: <${tag.name}> cannot follow <${previousHeading.name}> "
                    + "(previous heading: '$previousText')")
        }
    }

    /**
     * Finds the previous heading in document order whose level is < current heading level.
     *
     * Examples:
     * - for `<h4>` after `<h1>`: returns `<h1>`
     * - for `<h3>` after `<h4>, <h1>`: skips `<h4>` and returns `<h1>`
     * - for top-level `<h2>`: returns `null`
     */
    private fun findPreviousRelevantHeading(tag: HtmlTag, headingLevel: Int): HtmlTag? {
        return generateSequence(PsiTreeUtil.prevVisibleLeaf(tag), PsiTreeUtil::prevVisibleLeaf)
            .filterIsInstance<XmlToken>()
            .filter { it.tokenType == XML_NAME }
            .mapNotNull {
                PsiTreeUtil.getParentOfType(it, HtmlTag::class.java, false)
            }
            .firstOrNull {
                it.headingLevel < headingLevel
            }
    }

    private fun AnnotationHolder.warn(tag: HtmlTag, message: String) {
        newAnnotation(WARNING, message)
            .range(tag)
            .create()
    }

    private val HtmlTag.headingLevel: Int
        get() = localName.lowercase()
            .takeIf { it.length == 2 && it[0] == 'h' && it[1] in '1'..'6' }
            ?.let { it[1].digitToInt() }
            ?: NOT_A_HEADING
}
