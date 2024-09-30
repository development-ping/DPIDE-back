package com.dpide.dpide.controller;

import com.dpide.dpide.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/projects/{projectId}")
@RequiredArgsConstructor
@RestController
public class FileController {
    private final FileService fileService;

}