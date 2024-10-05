package com.dpide.dpide.util;

import com.dpide.dpide.exception.UnsupportedFileTypeException;

public class CommandBuilder {
    public static String buildCommand(String extension, String filePath) {
        return switch (extension) {
            case "java" -> buildJavaCommand(filePath);
            case "py" -> buildPythonCommand(filePath);
            default -> throw new UnsupportedFileTypeException(extension);
        };
    }
    // Java 실행 명령어 생성
    private static String buildJavaCommand(String filePath) {
        return "java " + filePath;
    }

    // Python 실행 명령어 생성
    private static String buildPythonCommand(String filePath) {
        return "python3 " + filePath;
    }
}
