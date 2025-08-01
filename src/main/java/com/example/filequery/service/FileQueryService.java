package com.example.filequery.service;

import com.example.filequery.domain.FileInfo;
import com.example.filequery.infrastructure.FileSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 文件查询服务
 */
@Service
public class FileQueryService {

    private final FileSystemRepository fileSystemRepository;

    @Autowired
    public FileQueryService(FileSystemRepository fileSystemRepository) {
        this.fileSystemRepository = fileSystemRepository;
    }

    /**
     * 查询目录下所有文件信息
     * @param directoryPath 目录路径
     * @return 文件信息列表
     */
    public List<FileInfo> listDirectoryFiles(String directoryPath) {
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            throw new IllegalArgumentException("目录路径不能为空");
        }
        
        return fileSystemRepository.listFiles(directoryPath.trim());
    }

    /**
     * 查询指定文件或目录的详细信息
     * @param filePath 文件或目录路径
     * @return 文件信息
     */
    public Optional<FileInfo> getFileDetails(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        
        return fileSystemRepository.getFileInfo(filePath.trim());
    }
}