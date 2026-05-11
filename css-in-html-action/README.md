# CSS in HTML Action Sample [![JetBrains IntelliJ Platform SDK Docs](https://jb.gg/badges/docs.svg)][docs]
* Reference: [Action System in IntelliJ SDK Docs][docs:actions]
* Reference: [Threading Model in IntelliJ SDK Docs][docs:threading]
* Reference: [UI Freezes and the Dangers of Non-Cancellable Read Actions in Background Threads][blog:cancellable-read-actions]

## Quickstart

CSS in HTML Action Sample demonstrates how to combine the Action System with HTML and CSS PSI.
The plugin registers the [`ShowSelectorsInInternalCssAction`][file:ShowSelectorsInInternalCssAction] action in the `Tools` menu. 
When invoked from an open HTML editor, it locates `<style>` tags, resolves the nested `CssStylesheet`, collects selectors from each ruleset, and shows them in a dialog.

The sample depends on the bundled `com.intellij.css` plugin and enables the action only when the current file is HTML.

## Overview

The action starts from the current HTML `PsiFile` and traverses its PSI with a custom `PsiRecursiveElementWalkingVisitor`.

For each matching `<style>` tag, the visitor retrieves the embedded CSS PSI with `getChildOfType(element, CssStylesheet::class.java)`.
Then, it navigates the type-safe stylesheet properties to collect every selector.

### Coroutines and locks
`actionPerformed()` runs the PSI work inside `e.coroutineScope.launch { ... }` that is executed under `Dispatchers.Default`.
Within that coroutine, PSI access is wrapped in `readAction { }`, which executes traversal under the [read lock][docs:threading-read-lock] while adhering to the [rules of cancellation][blog:cancellable-read-actions].

After collection finishes, the coroutine switches to the UI thread with `withContext(Dispatchers.EDT)` to show the modal dialog.


### CSS PSI

| CSS PSI Element                       | Description                                                   |
|---------------------------------------|---------------------------------------------------------------|
| `com.intellij.psi.css.CssStylesheet`  | CSS stylesheet.                                               |
| `com.intellij.psi.css.CssRulesetList` | A wrapper over rulesets.                                      |
| `com.intellij.psi.css.CssRuleset`     | [CSS rule set](https://drafts.csswg.org/css2/#rule-sets-dfn). |
| `com.intellij.psi.css.CssSelector`    | [CSS selector](https://drafts.csswg.org/css2/#selector).      |



### Actions

| ID | Implementation | Base Action Class |
|----|----------------|-------------------|
| `org.intellij.sdk.cssInHtml.ShowSelectorsInInternalCssAction` | [ShowSelectorsInInternalCssAction][file:ShowSelectorsInInternalCssAction] | `AnAction` |

[docs]: https://plugins.jetbrains.com/docs/intellij/
[docs:actions]: https://plugins.jetbrains.com/docs/intellij/action-system.html
[docs:threading]: https://plugins.jetbrains.com/docs/intellij/threading-model.html
[docs:threading-read-lock]: https://plugins.jetbrains.com/docs/intellij/threading-model.html#read-write-lock

[blog:cancellable-read-actions]: https://blog.jetbrains.com/platform/2026/03/ui-freezes-and-the-dangers-of-non-cancellable-read-actions-in-background-threads/

[file:ShowSelectorsInInternalCssAction]: ./src/main/kotlin/org/intellij/sdk/cssInHtml/ShowSelectorsInInternalCssAction.kt
