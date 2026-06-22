# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A minimal Java 8 Maven project that uses JavaParser AST to walk a target project's `.java` files and print class/method names with their annotations. The hardcoded target path is `E:/Ruo-Yi/RuoYi-Cloud`.

## Commands

- **Build**: `mvn compile`
- **Package**: `mvn package -DskipTests` (no tests exist)
- **Run**: `mvn compile exec:java -Dexec.mainClass="com.demo.ast.SpringbootDemoAstApplication"` or compile then run the class directly with `java`
- **IDE**: Open as a standard Maven project in IntelliJ (`.idea` files present, JDK 8)

## Architecture

Single file: `src/main/java/com/demo/ast/SpringbootDemoAstApplication.java`

- `main()` walks a directory tree (currently hardcoded to `E:/Ruo-Yi/RuoYi-Cloud`), filters `.java` files, and passes each to `parseFile()`.
- `parseFile()` uses JavaParser's `StaticJavaParser.parse()` to produce a `CompilationUnit`, then finds all `ClassOrInterfaceDeclaration` nodes. For each class it prints the class name, iterates methods, and prints method name + annotations.

Dependency: `com.github.javaparser:javaparser-core:3.25.8`

No tests, no Spring Boot framework — despite the artifact name `springboot-demo-ast`, this is a standalone JavaParser utility, not a Spring application.
