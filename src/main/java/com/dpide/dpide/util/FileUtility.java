package com.dpide.dpide.util;

import com.dpide.dpide.exception.FileOperationException;

import java.io.File;
import java.io.IOException;

public class FileUtility {
    public static String BASE_PATH = "/user_files"; // Docker 컨테이너의 유저 파일 저장 경로

    // 프로젝트 경로 생성 메서드
    public static String generatePath(Long userId, Long projectId, String path) {
        return BASE_PATH + "/" + userId + "/" + projectId + path;
    }

    // 폴더 경로 생성 메서드
    public static String generateFolderPath(String basePath, String folderName) {
        return basePath + "/" + folderName;
    }

    // 파일 경로 생성 메서드
    public static String generateFilePath(String basePath, String fileName, String extension) {
        return basePath + "/" + fileName + "." + extension;
    }

    // 디렉터리 생성 여부 확인 메서드
    public static void createDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // 파일 생성 로직
    public static void createNewFile(String path, String fileName) {
        File newFile = new File(path);

        if (newFile.exists()) {
            throw new FileOperationException(fileName);
        }

        try {
            newFile.createNewFile();
        } catch (IOException e) {
            throw new FileOperationException(fileName);
        }
    }

    // 폴더 생성 로직
    public static void createNewFolder(String path, String folderName) {
        File newFolder = new File(path);

        if (newFolder.exists()) {
            throw new FileOperationException(folderName);
        }

        newFolder.mkdirs();
    }

    // 확장자가 "folder"가 아닌 경우 파일로 처리
    public static boolean isFile(String extension) {
        return !"folder".equals(extension);
    }

    public static void deleteFileOrDirectory(String path) {
        File file = new File(path);
        // 실제 파일/폴더가 존재하면 삭제
        if (file.exists()) {
            if (file.isDirectory()) {
                // 폴더일 경우 내부의 모든 파일 및 디렉토리 삭제
                deleteDirectoryRecursively(file);
            } else {
                // 파일일 경우 단일 파일 삭제
                if (!file.delete()) {
                    throw new FileOperationException(path);
                }
            }
        }
    }

    private static void deleteDirectoryRecursively(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        if (!directory.delete()) {
            throw new FileOperationException(directory.getPath());
        }
    }
}
