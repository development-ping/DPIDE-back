package com.dpide.dpide.util;

import com.dpide.dpide.exception.UnsupportedFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@Slf4j
@Component
public class FileExecutor {
    private static final long DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int THREAD_POOL_SIZE = 10;

    // 스레드 풀 생성
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static String executeCommand(String command, String userInput) {
        Future<String> future = executor.submit(() -> {
            ProcessBuilder builder = new ProcessBuilder(command.split(" "));
            builder.redirectErrorStream(true);
            Process process = builder.start();

            // 사용자 입력 전달
            if (userInput != null && !userInput.isEmpty()) {
                try (OutputStream outputStream = process.getOutputStream()) {
                    outputStream.write(userInput.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }
            }

            // 실행 결과 가져오기
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                process.waitFor(); // 실행 완료 대기
            }

            return output.toString();
        });

        try {
            // 타임아웃 적용
            return future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // 타임아웃 시 실행 중지
            return "Time Out: 파일 실행 시간이 초과되었습니다.";
        } catch (Exception e) {
            return "파일 실행 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
