package org.intellij.sdk.jsFunctionLineMarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement

class TopLevelFunctionLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val function = element as? JSFunction ?: return null
        if (function.parent !is JSFile) return null

        return LineMarkerInfo(
            function,
            function.textRange,
            AllIcons.Nodes.Function,
            this::getTooltip,
            null,
            GutterIconRenderer.Alignment.RIGHT,
            this::iconAccessibleName
        )
    }

    private fun getTooltip(function: JSFunction): String {
        val name = function.name ?: "<anonymous>"
        return "Top-level function '$name'"
    }

    private val iconAccessibleName = "Top-level function marker"
}