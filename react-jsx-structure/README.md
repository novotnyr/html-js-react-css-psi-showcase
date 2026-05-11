# React JSX Structure Sample [![JetBrains IntelliJ Platform SDK Docs](https://jb.gg/badges/docs.svg)][docs]
*Reference: [Tool Windows in IntelliJ SDK Docs][docs:tool_windows]*

## Quickstart

This sample demonstrates a tool window backed by React JSX PSI.
The plugin registers the [`ReactJsxToolWindowFactory`][file:ReactJsxToolWindowFactory] tool window factory. Its UI is implemented by [`JsXmlLiteralsPanel`][file:JsXmlLiteralsPanel], which subscribes to [`SelectedFileEditorJsXmlService`][file:SelectedFileEditorJsXmlService] and renders `JSXmlLiteralExpression` names from the currently selected JSX file.

## Example

Consider a minimalistic JSX:

```jsx
import ReactDOM from 'react-dom/client'

function CurrentDate() {
    const date = new Date().toLocaleDateString()
    return <p>{date}</p>
}

function App() {
    const message = "Hello World"
    return (
        <div>
            <h1>{message}</h1>
            <CurrentDate />
        </div>
    )
}

ReactDOM
    .createRoot(document.getElementById('root'))
    .render(<App />)
```

The following elements are displayed in the tool window:

- `p`
- `div`
- `h1`
- `CurrentDate`
- `App`


### Dependencies

- `JavaScript` plugin. It covers PSI for JavaScript and React.

### JavaScript and React PSI

| PSI Element | Description                                                               |
|-------------|---------------------------------------------------------------------------|
| `com.intellij.lang.javascript.JSXFileType`            | JSX file with JavaScript syntax extension that produces React "elements". |
| `com.intellij.lang.javascript.psi.JSXmlLiteralExpression`            | JSX XML element as per the [JSX specification][spec:jsx].                |


### Extension Points

| Name | Implementation | Extension Point Class |
|------|----------------|-----------------------|
| `com.intellij.toolWindow` | [ReactJsxToolWindowFactory][file:ReactJsxToolWindowFactory] | `ToolWindowFactory` |

### Main Components

| Component | Responsibility |
|-----------|----------------|
| [JsXmlLiteralsPanel][file:JsXmlLiteralsPanel] | Renders the collected JSX literal names in the tool window. |
| [SelectedFileEditorJsXmlService][file:SelectedFileEditorJsXmlService] | Tracks the selected editor file and exposes JSX literal names as a `StateFlow`. |
| [JsxPsiUtils][file:JsxPsiUtils] | Extracts `JSXmlLiteralExpression` names from the current JSX PSI file. |

## Data Flow

### Service

The [`SelectedFileEditorJsXmlService`][file:SelectedFileEditorJsXmlService] service is effectively a pipeline.
It turns file editor events into a collectible state flow.

It owns two flows: `selectedFile`, which tracks the currently active `VirtualFile` in the editor, and `expressionNames`, which holds the latest list of JSX literal names.

The service registers with the project message bus by calling `project.messageBus.connect(coroutineScope).subscribe(...)` with a listener.
That listener is the bridge from IDE editor events to the service's `selectedFile` state flow, and the `connect(coroutineScope)` call ties the listener lifetime to the injected service scope.

The service also receives an injected `CoroutineScope`, and it uses that scope to launch a collector for `selectedFile`.
Inside `collectLatest`, each selected file change triggers a PSI read phase, and a newer file selection cancels any in-flight extraction work for the previous file.

Such a phase resolves the `VirtualFile` to a `PsiFile`, filters out non-JSX files, traverses the PSI while discovering JSX literals and maps the resulting PSI elements to their names.
This collection reads PSI under `readAction`, so the file resolution and PSI traversal execute under the IntelliJ Platform read lock with a proper cancellation put in place.

The collected list is then published to the `expressionNames` flow to be consumed by the UI.

### UI Panel

[`JsXmlLiteralsPanel`][file:JsXmlLiteralsPanel] is the downstream consumer of the flow exposed by the service.
In its initializer, it subscribes to `expressionNames`, and updates the `JBList` model for every emission. As a result, each flow emission immediately refreshes the visible UI.

The panel creates its own UI collection scope manually with a supervisor job on the `UI` dispatcher.

That is important for two reasons. First, `Dispatchers.UI` keeps list model updates on the UI thread. Second, the manually created `SupervisorJob` gives the panel a lifecycle-bound parent job that can be canceled in `dispose()`, without tying the collector to the service scope or the whole project lifetime. Using a supervisor job also prevents one failing child coroutine from automatically cancelling the whole UI scope, which is a better default for a tool window panel that may grow more UI-bound coroutines over time.


[docs]: https://plugins.jetbrains.com/docs/intellij/
[docs:tool_windows]: https://plugins.jetbrains.com/docs/intellij/tool-windows.html

[spec:jsx]: https://facebook.github.io/jsx/#sec-jsx-PrimaryExpression

[file:ReactJsxToolWindowFactory]: ./src/main/kotlin/org/intellij/sdk/reactJsxStructure/ReactJsxToolWindowFactory.kt
[file:JsXmlLiteralsPanel]: ./src/main/kotlin/org/intellij/sdk/reactJsxStructure/JsXmlLiteralsPanel.kt
[file:SelectedFileEditorJsXmlService]: ./src/main/kotlin/org/intellij/sdk/reactJsxStructure/SelectedFileEditorJsXmlService.kt
[file:JsxPsiUtils]: ./src/main/kotlin/org/intellij/sdk/reactJsxStructure/JsxPsiUtils.kt
