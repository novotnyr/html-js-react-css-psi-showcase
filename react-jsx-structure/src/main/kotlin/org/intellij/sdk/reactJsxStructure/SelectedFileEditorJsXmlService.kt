package org.intellij.sdk.reactJsxStructure

import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level.PROJECT
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Service(PROJECT)
class SelectedFileEditorJsXmlService(private val project: Project, coroutineScope: CoroutineScope) {
    private val _expressionNames = MutableStateFlow<List<String>>(emptyList())
    val expressionNames: StateFlow<List<String>> = _expressionNames.asStateFlow()

    private val selectedFile = MutableStateFlow(getSelectedFile())

    init {
        project.messageBus.connect(coroutineScope).subscribe(
            FileEditorManagerListener.FILE_EDITOR_MANAGER,
            object : FileEditorManagerListener {
                override fun selectionChanged(event: FileEditorManagerEvent) {
                    onCurrentFileChanged(event.newFile)
                }

                override fun fileOpened(source: FileEditorManager, file: VirtualFile) = onCurrentFileChanged(file)
            })

        coroutineScope.launch {
            selectedFile.collectLatest { file ->
                val expressions = file?.let {
                    readAction {
                        getJsXmlLiteralExpressions(project, it)
                    }
                } ?: emptyList()

                _expressionNames.value = expressions
            }
        }
    }

    private fun getSelectedFile(): VirtualFile? = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()

    private fun onCurrentFileChanged(file: VirtualFile?) {
        selectedFile.value = file
    }
}
