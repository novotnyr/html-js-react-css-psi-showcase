# CSS PSI Action Sample [![JetBrains IntelliJ Platform SDK Docs](https://jb.gg/badges/docs.svg)][docs]
*Reference: [Action System in IntelliJ SDK Docs][docs:actions]*

## Quickstart

This sample demonstrates how to traverse the PSI of a CSS file from an IDE action.
The plugin registers the [`CssPsiAction`][file:CssPsiAction] action in the `Tools` menu. 
When invoked from an open CSS editor, it reads the current `CssFile`, collects all `CssSelector` elements, and shows their presentable text in a dialog.

### Dependencies

- `com.intellij.css`: CSS language support and PSI API

### Coroutines and locks
`actionPerformed()` runs the PSI work inside `e.coroutineScope.launch { ... }` that is executed under `Dispatchers.Default`.
Within that coroutine, PSI access is wrapped in `readAction { }`, which executes traversal under the [read lock](https://plugins.jetbrains.com/docs/intellij/threading-model.html#read-write-lock) while adhering to the [rules of cancellation](https://blog.jetbrains.com/platform/2026/03/ui-freezes-and-the-dangers-of-non-cancellable-read-actions-in-background-threads/).

After collection finishes, the coroutine switches to the UI thread with `withContext(Dispatchers.EDT)` to show the modal dialog.

### CSS PSI

| CSS PSI Element | Description |
|-----------------|-------------|
| `com.intellij.psi.css.CssFile` | A top-level CSS PSI file, usually open in an editor. |
| `com.intellij.psi.css.CssSelector` | [CSS selector](https://drafts.csswg.org/css2/#selector). |


### Actions

| ID | Implementation | Base Action Class |
|----|----------------|-------------------|
| `com.intellij.sdk.cssPsi.CssPsiAction` | [CssPsiAction][file:CssPsiAction] | `AnAction` |

[docs]: https://plugins.jetbrains.com/docs/intellij/
[docs:actions]: https://plugins.jetbrains.com/docs/intellij/action-system.html

[file:CssPsiAction]: ./src/main/kotlin/com/intellij/sdk/cssPsi/CssPsiAction.kt
