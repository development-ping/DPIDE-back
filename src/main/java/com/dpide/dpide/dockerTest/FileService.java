package com.dpide.dpide.dockerTest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public Long saveFile(String name, String extension) {
        fileRepository.save(File.builder()
                .name(name)
                .extension(extension)
                .build());
        return 0L;
    }

    public String getFiles() {
        return fileRepository.findAll().toString();
    }
}
