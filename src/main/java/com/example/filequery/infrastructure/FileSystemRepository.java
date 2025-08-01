package com.example.filequery.infrastructure;

import com.example.filequery.domain.FileInfo;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 文件系统访问仓储实现
 */
@Repository
public class FileSystemRepository {

    /**
     * 获取目录下所有文件信息
     * @param directoryPath 目录路径
     * @return 文件信息列表
     */
    public List<FileInfo> listFiles(String directoryPath) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            return fileInfoList;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                FileInfo fileInfo = createFileInfo(file);
                fileInfoList.add(fileInfo);
            }
        }
        
        return fileInfoList;
    }

    /**
     * 获取指定文件或目录的信息
     * @param filePath 文件或目录路径
     * @return 文件信息
     */
    public Optional<FileInfo> getFileInfo(String filePath) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            return Optional.empty();
        }
        
        return Optional.of(createFileInfo(file));
    }

    /**
     * 创建文件信息对象
     * @param file 文件对象
     * @return 文件信息
     */
    private FileInfo createFileInfo(File file) {
        String name = file.getName();
        String type = file.isDirectory() ? "directory" : getFileExtension(name);
        boolean isDirectory = file.isDirectory();
        long size = file.isDirectory() ? 0 : file.length();
        LocalDateTime lastModified = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(file.lastModified()),
            ZoneId.systemDefault()
        );
        String path = file.getAbsolutePath();

        return new FileInfo(name, type, isDirectory, size, lastModified, path);
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "unknown";
    }
}