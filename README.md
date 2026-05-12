# HTML, CSS, JavaScript, and React PSI Showcase [![JetBrains IntelliJ Platform SDK Docs](https://jb.gg/badges/docs.svg)][docs]

This repository collects five standalone IntelliJ Platform plugin samples focused on PSI-driven features for HTML, CSS, JavaScript, and React JSX.

## Plugins

| Plugin                                                                 | Plugin Dependencies     | Description                                                                                          |
|------------------------------------------------------------------------|-------------------------|------------------------------------------------------------------------------------------------------|
| [CSS in HTML Action](./css-in-html-action)                             | CSS                     | Action sample that inspects `<style>` blocks inside HTML files and lists the embedded CSS selectors. |
| [CSS PSI Action](./css-psi-action)                                     | CSS                     | Action sample that traverses the PSI of the current CSS file and shows all selector nodes.           |
| [HTML Heading Hierarchy Inspection](./html-code-inspection)            | none (part of the core) | HTML annotator sample that warns about orphaned headings and skipped heading levels.                 |
| [React JSX Structure](./react-jsx-structure)                           | JavaScript              | Tool window sample that tracks the selected JSX file and lists JSX literal expression names.         |
| [JavaScript Top-Level Function Line Marker](./js-function-line-marker) | JavaScript              | Line marker sample that adds gutter icons for top-level JavaScript functions.                        |

## Target Platform

All plugins target IntelliJ IDEA `2026.1`.
In this Platform version, the demonstrated features are [available in the free tier](https://blog.jetbrains.com/idea/2026/03/js-ts-free-support/), without subscription.

Samples that work with CSS or JavaScript declare the `com.intellij.css` or `JavaScript` plugin dependencies in their own build files.

## Structure
Each sample is a self-contained Gradle project with its own `README.md`, `plugin.xml`, and Gradle wrapper. The repository root groups them with a Gradle composite build for convenient browsing.

Each standalone plugin follows the standard IntelliJ Platform Gradle layout:
- `build.gradle.kts` and `gradle.properties` for build configuration
- `src/main/kotlin` for implementation
- `src/main/resources/META-INF/plugin.xml` for plugin registration
- local Gradle wrapper files for running `runIde`, `test`, and related tasks


## Running a Sample

Choose a standalone plugin directory and use its Gradle wrapper:

```bash
cd css-in-html-action
./gradlew runIde
```

Use `./gradlew test` in the same directory to run that sample's tests.

## Useful Links

- [IntelliJ Platform SDK Docs][docs]
- [IntelliJ SDK Code Samples][gh:code-samples]

[docs]: https://plugins.jetbrains.com/docs/intellij/
[gh:code-samples]: https://github.com/JetBrains/intellij-sdk-code-samples
