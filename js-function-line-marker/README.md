# JavaScript Top-Level Function Line Marker Sample [![JetBrains IntelliJ Platform SDK Docs](https://jb.gg/badges/docs.svg)][docs]
*Reference: [Line Marker Provider in IntelliJ SDK Docs][docs:line_markers]*

## Quickstart

This sample demonstrates adding gutter markers for top-level JavaScript functions.

The plugin registers the [`TopLevelFunctionLineMarkerProvider`][file:TopLevelFunctionLineMarkerProvider] line marker provider for the `JavaScript` language.
It adds the standard function icon and a tooltip for `JSFunction` elements whose direct parent is a `JSFile`.

For example, in a JavaScript file containing the two functions below, both top-level declarations are detected.

```javascript
function underline(text) {
    return text + "\n" + "-".repeat(text.length);
}

function heading(text, level) {
    return "#".repeat(level) + " " + text;
}
```

### Dependencies

- `JavaScript` bundled plugin

### PSI Elements

| Element                                       | Description                                   |
|-----------------------------------------------|-----------------------------------------------|
| `com.intellij.lang.javascript.psi.JSFile`     | Represents a JavaScript source file.          |
| `com.intellij.lang.javascript.psi.JSFunction` | Represents a JavaScript function declaration. |

### Extension Points

| Name | Implementation | Extension Point Class |
|------|----------------|-----------------------|
| `com.intellij.codeInsight.lineMarkerProvider` | [TopLevelFunctionLineMarkerProvider][file:TopLevelFunctionLineMarkerProvider] | `LineMarkerProvider` |

[docs]: https://plugins.jetbrains.com/docs/intellij/
[docs:line_markers]: https://plugins.jetbrains.com/docs/intellij/line-marker-provider.html

[file:TopLevelFunctionLineMarkerProvider]: ./src/main/kotlin/org/intellij/sdk/jsFunctionLineMarker/TopLevelFunctionLineMarkerProvider.kt
