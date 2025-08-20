package com.demo.ast;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import java.io.*;
import java.nio.file.*;

public class SpringbootDemoAstApplication{
    public static void main(String[] args) throws Exception {
        Path projectPath = Paths.get("E:/Ruo-Yi/RuoYi-Cloud");
        Files.walk(projectPath)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(SpringbootDemoAstApplication::parseFile);
    }

    private static void parseFile(Path path) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(path);
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                System.out.println("类名: " + clazz.getName());
                clazz.getMethods().forEach(method -> {
                    System.out.println("  方法: " + method.getName());
                    System.out.println("    注解: " + method.getAnnotations());
                });
            });
        } catch (IOException | ParseProblemException e) {
            System.err.println("无法解析: " + path + " 错误: " + e.getMessage());
        }
    }
}
