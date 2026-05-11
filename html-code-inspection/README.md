# HTML Heading Hierarchy Inspection Sample [![JetBrains IntelliJ Platform SDK Docs](https://jb.gg/badges/docs.svg)][docs]
*Reference: [Plugin Extension Points in IntelliJ SDK Docs][docs:ep]*

## Quickstart

This sample demonstrates validating heading structure order with HTML PSI.
The plugin registers the [`HeadingLevelAnnotator`][file:HeadingLevelAnnotator] annotator for the `HTML` language. It highlights `<h2>` through `<h6>` tags when no previous heading establishes their level or when a heading skips the expected hierarchy.

### Dependencies

- None. HTML support is available out of the box in the IntelliJ Platform core.

### HTML PSI and XML PSI

| PSI Element                         | Description                                                              |
|-------------------------------------|--------------------------------------------------------------------------|
| `com.intellij.psi.html.HtmlTag`     | [HTML tag][spec:html5-element], such as `<h1>` or `<title>`.             |
| `com.intellij.psi.xml.XmlToken`     | Syntax-level token with a specific [XML token type][api:xml-token-type]. |
| `com.intellij.psi.xml.XmlTokenType` | XML token type, such as `XML_START_TAG` or `XML_END_TAG`.                |

### Extension Points

| Name | Implementation | Extension Point Class |
|------|----------------|-----------------------|
| `com.intellij.annotator` | [HeadingLevelAnnotator][file:HeadingLevelAnnotator] | `Annotator` |

[docs]: https://plugins.jetbrains.com/docs/intellij/
[docs:ep]: https://plugins.jetbrains.com/docs/intellij/plugin-extensions.html

[spec:html5-element]: https://html.spec.whatwg.org/multipage/syntax.html#elements-2
[api:xml-token-type]: https://github.com/JetBrains/intellij-community/blob/master/xml/xml-parser/src/com/intellij/psi/xml/XmlTokenType.kt

[file:HeadingLevelAnnotator]: ./src/main/kotlin/org/intellij/sdk/htmlCodeInspection/HeadingLevelAnnotator.kt
[file:HeadingLevelAnnotatorTest]: ./src/test/kotlin/org/intellij/sdk/htmlCodeInspection/HeadingLevelAnnotatorTest.kt
