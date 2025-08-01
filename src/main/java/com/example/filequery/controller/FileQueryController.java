package com.example.filequery.controller;

import com.example.filequery.domain.FileInfo;
import com.example.filequery.service.FileQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 文件查询REST API控制器
 */
@RestController
@RequestMapping("/api/files")
public class FileQueryController {

    private final FileQueryService fileQueryService;

    @Autowired
    public FileQueryController(FileQueryService fileQueryService) {
        this.fileQueryService = fileQueryService;
    }

    /**
     * 本地文件列表查询接口
     * @param directoryPath 目录路径
     * @return 文件信息列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<FileInfo>> listFiles(@RequestParam String directoryPath) {
        try {
            List<FileInfo> fileInfoList = fileQueryService.listDirectoryFiles(directoryPath);
            return ResponseEntity.ok(fileInfoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 本地文件具体信息查询接口
     * @param filePath 文件或目录路径
     * @return 文件详细信息
     */
    @GetMapping("/info")
    public ResponseEntity<FileInfo> getFileInfo(@RequestParam String filePath) {
        try {
            Optional<FileInfo> fileInfo = fileQueryService.getFileDetails(filePath);
            if (fileInfo.isPresent()) {
                return ResponseEntity.ok(fileInfo.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}