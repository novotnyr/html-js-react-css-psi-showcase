package org.intellij.sdk.cssInHtml

import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.readAction
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.css.CssStylesheet
import com.intellij.psi.html.HtmlTag
import com.intellij.psi.util.PsiTreeUtil.getChildOfType
import com.intellij.util.concurrency.annotations.RequiresReadLock
import com.intellij.xml.util.HtmlUtil.STYLE_TAG_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowSelectorsInInternalCssAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val htmlFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        if (!htmlFile.isHtmlFile()) return
        e.getData(CommonDataKeys.EDITOR) ?: return

        e.coroutineScope.launch {
            val selectors = readAction {
                htmlFile.getCssSelectors()
            }
            val selectorsMessage = selectors.joinToString("\n")
            withContext(Dispatchers.EDT) {
                // As of 2026.1, showInfoMessage uses read-write lock.
                // Such locks are prohibited on Dispatchers.UI.
                Messages.showInfoMessage(selectorsMessage, "CSS Selector List");
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        e.presentation.isEnabled = editor != null && psiFile.isHtmlFile()
    }

    override fun getActionUpdateThread() = BGT

    private fun PsiFile?.isHtmlFile() = this?.fileType == HtmlFileType.INSTANCE

    @RequiresReadLock
    private fun PsiFile.getCssSelectors(): List<String> {
        if (!isValid) return emptyList()
        val visitor = CssSelectorVisitor()
        acceptChildren(visitor)
        return visitor.selectors
    }

    private class CssSelectorVisitor : PsiRecursiveElementWalkingVisitor() {
        private val _selectors = mutableListOf<String>()
        val selectors: List<String> get() = _selectors

        @RequiresReadLock
        override fun visitElement(element: PsiElement) {
            if (element.isStyleTag()) {
                ProgressManager.checkCanceled()
                val stylesheet = getChildOfType(element, CssStylesheet::class.java)
                if (stylesheet != null) {
                    for (ruleset in stylesheet.rulesetList.rulesets) {
                        for (selector in ruleset.selectors) {
                            ProgressManager.checkCanceled()
                            _selectors += selector.presentableText
                        }
                    }
                    // Intentionally skipping call to super.visitElement(element)
                    // Because there is no need to process children of <style> tag.
                    return
                }
            }
            // Continue traversal into other elements.
            super.visitElement(element)
        }

        private fun PsiElement.isStyleTag() = this is HtmlTag
                && STYLE_TAG_NAME.equals(name, ignoreCase = true)
    }
}