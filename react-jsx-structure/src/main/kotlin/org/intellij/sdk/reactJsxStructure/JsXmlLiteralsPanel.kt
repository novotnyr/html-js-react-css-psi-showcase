package org.intellij.sdk.reactJsxStructure

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.UI
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.CollectionListModel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class JsXmlLiteralsPanel(project: Project) : SimpleToolWindowPanel(/* vertical = */ true), Disposable {
    private val listModel = CollectionListModel<String>()

    private val uiScope = CoroutineScope(SupervisorJob() + Dispatchers.UI)

    init {
        setContent(JBList(listModel).apply {
            cellRenderer = newCellRenderer()
        })

        project.service<SelectedFileEditorJsXmlService>().expressionNames
            .onEach {
                listModel.replaceAll(it)
            }
            .launchIn(uiScope)
    }

    private fun newCellRenderer() = SimpleListCellRenderer.create<String> { label, value, _ ->
        label.icon = AllIcons.FileTypes.Xml
        label.text = value
    }

    override fun dispose() {
        uiScope.cancel()
    }
}
