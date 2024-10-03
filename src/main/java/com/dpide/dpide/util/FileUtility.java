package com.dpide.dpide.util;

import com.dpide.dpide.exception.FileOperationException;

import java.io.File;
import java.io.IOException;

public class FileUtility {
    public static String BASE_PATH = "/tmp/user_files"; // Docker 컨테이너의 유저 파일 임시 저장 경로

    // 프로젝트 경로 생성 메서드
    public static String generateProjectPath(Long userId, Long projectId) {
        return BASE_PATH + "/" + userId + "/" + projectId;
    }

    // 폴더 경로 생성 메서드
    public static String generateFolderPath(Long userId, Long projectId, String folderName) {
        return generateProjectPath(userId, projectId) + "/" + folderName;
    }

    // 파일 경로 생성 메서드
    public static String generateFilePath(Long userId, Long projectId, String fileName, String extension) {
        return generateProjectPath(userId, projectId) + "/" + fileName + "." + extension;
    }

    // 디렉터리 생성 여부 확인 메서드
    public static void createDirectory(String projectPath) {
        File directory = new File(projectPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // 파일 생성 로직
    public static void createNewFile(String projectPath, String fileName, String extension) {
        String filePath = projectPath + "/" + fileName + "." + extension;
        File newFile = new File(filePath);

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
    public static void createNewFolder(String projectPath, String folderName) {
        String folderPath = projectPath + "/" + folderName;
        File newFolder = new File(folderPath);

        if (newFolder.exists()) {
            throw new FileOperationException(folderName);
        }

        newFolder.mkdirs();
    }

    // 확장자가 "folder"가 아닌 경우 파일로 처리
    public static boolean isFile(String extension) {
        return !"folder".equals(extension);
    }

    public static void deleteFileOrDirectory(String filePath) {
        File file = new File(filePath);
        // 실제 파일/폴더가 존재하면 삭제
        if (file.exists()) {
            if (file.isDirectory()) {
                // 폴더일 경우 내부의 모든 파일 및 디렉토리 삭제
                deleteDirectoryRecursively(file);
            } else {
                // 파일일 경우 단일 파일 삭제
                if (!file.delete()) {
                    throw new FileOperationException(filePath);
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
