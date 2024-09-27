package com.dpide.dpide.dockerTest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public Long uploadFile(@RequestParam String name, @RequestParam String extension) {
        log.info("Controller call: uploadFile");
        return fileService.saveFile(name, extension);
    }

    @GetMapping("/files")
    public String getAllFile() {
        log.info("Controller call: getAllFile");
        return fileService.getFiles();
    }
}

