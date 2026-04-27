package org.intellij.sdk.reactJsxStructure

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

internal class ReactJsxToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val toolWindowPanel = SimpleToolWindowPanel(/* vertical = */ true)
        toolWindow.contentManager.apply {
            val toolWindowContent = factory.createContent(
                toolWindowPanel,
                /* displayName = */ "",
                /* isLockable = */ false
            )
            addContent(toolWindowContent)
        }
    }
}