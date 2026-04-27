package org.intellij.sdk.reactJsxStructure

import com.intellij.lang.javascript.JSXFileType
import com.intellij.lang.javascript.psi.JSXmlLiteralExpression
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.descendantsOfType
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.concurrency.annotations.RequiresReadLock

@RequiresReadLock
@RequiresBackgroundThread
fun getJsXmlLiteralExpressions(project: Project, virtualFile: VirtualFile): List<String> {
    if (!virtualFile.isValid) return emptyList()
    if (virtualFile.fileType != JSXFileType) return emptyList()
    val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return emptyList()
    return getJsXmlLiteralExpressions(psiFile)
}

@RequiresReadLock
private fun getJsXmlLiteralExpressions(psiFile: PsiFile): List<String> {
    return psiFile.descendantsOfType<JSXmlLiteralExpression>()
        .map { it.name }
        .toList()
}
