# How to contribute to the Fabric port of SecurityCraft
(If what you want to do isn't described here, please open an issue and describe what you want to do)
This is a work in progress, so there may not be many details.
## You found a bug? You found some inconsistency with the original mod? Have a suggestion to improve the mod? Have any questions?
- If you have found an issue or bug related to the mod, or you have found an inconsistency with the original mod, you have two options:
  - Opening an issue for me to read it and trying solving it. Read more on [Opening an issue](#opening-an-issue).
  - Solving the problem by yourself and creating a pull request. Read more on [Creating a pull request](#creating-a-pull-request).

- If you have a suggestion or question related to the mod itself, please open an issue on the [official issue tracker](https://github.com/Geforce132/SecurityCraft/issues/).
- If you have a suggestion or question related to the port rather than the mod, please open an issue on the [issue tracker](https://github.com/ByMartrixx/SecurityCraft/issues/). More about it on [Opening an issue](#opening-an-issue).

## Opening an issue
Use the provided templates when creating your issue, and fill it with the information accordingly to the template.
Please be as descriptive as possible. I may not be able to solve an issue if the author has not provided enough information.

**If the issue is related with the original mod rather than with the port, I won't be able to help you, so the issue will be closed and you will be addressed to the [official issue tracker](https://github.com/Geforce132/SecurityCraft/issues/).**

## Creating a pull request
You have done something, and you want it to be on the port? Or you have fixed a problem that isn't being worked on? This is the right place.
If you want to do some changes, but you don't know how to do it, please head on to [Coding](#coding) and come back to here once you are done.

When creating a pull request, please describe which problem it solves, if any, and what you have done. Please follow the [style guides](#style-guides)

**If what you've done is related to the original mod rather than the port, the pull request will be closed and you will be addressed to the [official issue tracker](https://github.com/Geforce132/SecurityCraft/issues/).**

## Coding
This mod is coded in [Java](https://java.com/) and uses [Gradle](https://gradle.org/) alongside with [fabric-loom](https://github.com/FabricMC/fabric-loom/) as the build tool.
To get started, you will need:
 - A Java Development Kit (JDK) for Java 8 or newer. https://adoptopenjdk.net/
 - Any Java IDE
 
Making the changes: (You can skip forking and creating a pull request)
 - Fork and clone the repository
 - If you want the minecraft source code, run `./gradlew genSources` on the project folder
 - Make the changes you wish to do
 - Once you are done, you can push the changes to your fork on Github and open a pull request. See [Creating a pull request](#creating-a-pull-request).
 
 ### Style guides
 The only style guides you have to follow are, being consistent with the current code, and using spaces for indents. Please use same-line braces.
