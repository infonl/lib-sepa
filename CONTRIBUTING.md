# Contribution guidelines

If you want to contribute, we ask you to follow these guidelines.
These instructions are currently mainly intended for our own INFO developers, but
we do also welcome contributions from the open source community.
Please reach out to us if you have questions or wish to contribute.

## License

The license information for this project can be found in [LICENSE.md](LICENSE.md).
We use [SPDX](https://spdx.dev/) license identifiers in source code files.

When adding a new source code file or modifying an existing one as a INFO developer, please update the `SPDX` license identifier accordingly:

### Adding a new source code file

For most source code files (e.g. `.ts`, `.js`, `.kt` and `.java` files) please add the following SPDX license identifier to the top of the file:

```
/*
 * SPDX-FileCopyrightText: <YYYY> INFO
 * SPDX-License-Identifier: EUPL-1.2+
 */
```

Where `<YYYY>` is the current year. E.g. `2024`.

For other file types (e.g. `.html` and `.xml` files) please add the following SPDX license identifier to the top of the file:

```
 <!--
  ~ SPDX-FileCopyrightText: <YYYY> INFO
  ~ SPDX-License-Identifier: EUPL-1.2+
  -->
```

Finally, for e.g. `.sh` files please add:

```
#
# SPDX-FileCopyrightText: <YYYY> INFO
# SPDX-License-Identifier: EUPL-1.2+
#
```

Tip: configure your IDE to automatically add these headers to new source code files.
For example, in IntelliJ IDEA please follow the instructions on https://www.jetbrains.com/help/idea/copyright.html.

### Modifying an existing source code file

If the file does not already include `INFO` in the copyright text, please update the SPDX license identifier 
on the top of the file by adding a `<YYYY> INFO` to the `SPDX-FileCopyrightText` identifier where `<YYYY>` is the current year. E.g.:

```
/*
 * SPDX-FileCopyrightText: 2024 INFO
 * SPDX-License-Identifier: EUPL-1.2+
 */
```

Note that each contributor should only be mentioned once in an SPDX header, where we use the convention that the year 
indicates the _initial_ year when a contribution was made by that contributor.

## Conventional Commits

We use [Conventional Commits](https://www.conventionalcommits.org) for our commit messages.
Specifically we use the following format for our commit messages:

```
<type>[optional scope]: <description>

[body]

[footer]
```

When you create a Pull Request (PR) please follow the following instructions to comply to our Conventional Commits guidelines:
1. Make sure the PR title complies to: `<type>[optional scope]: <description>`. E.g. `feat: add new feature`.
2. In the PR body fill in the body consisting of one line of text. E.g. `Added new exciting feature.`
3. In the footer reference the JIRA issue (typically a subtask) number that this commit solves as follows:
    `Solves XY-XXX`, where `XY-XXX` is the issue key.
