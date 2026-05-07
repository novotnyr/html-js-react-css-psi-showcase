package com.intellij.sdk.cssPsi

import com.intellij.lang.css.CSSLanguage
import com.intellij.openapi.actionSystem.ActionUpdateThread.BGT
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.css.CssFile
import com.intellij.psi.css.CssFileType
import com.intellij.psi.css.CssSelector
import com.intellij.psi.util.descendantsOfType
import com.intellij.util.concurrency.annotations.RequiresReadLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language

class CssPsiAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val cssFile = e.getData(CommonDataKeys.PSI_FILE) as? CssFile?: return
        e.getData(CommonDataKeys.EDITOR) ?: return

        e.coroutineScope.launch {
            val selectors = readAction {
                getCssSelectors(cssFile)
            }
            val message = selectors.joinToString("\n")
            withContext(Dispatchers.EDT) {
                // As of 2026.1, showInfoMessage uses read-write lock.
                // Such locks are prohibited on Dispatchers.UI.
                Messages.showInfoMessage(message, "CSS Selector List");
            }
        }
    }

    @RequiresReadLock
    private fun getCssSelectors(cssFile: CssFile): List<String> =
        when {
            cssFile.isValid -> cssFile
                .descendantsOfType<CssSelector>()
                .map { it.presentableText }
                .toList()
            else -> emptyList()
        }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        e.presentation.isEnabled = editor != null && psiFile != null && psiFile.fileType == CssFileType.INSTANCE
    }

    override fun getActionUpdateThread() = BGT
}