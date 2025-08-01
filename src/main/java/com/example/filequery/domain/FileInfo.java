package com.example.filequery.domain;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 */
public class FileInfo {
    
    private String name;
    private String type;
    private boolean isDirectory;
    private long size;
    private LocalDateTime lastModified;
    private String path;

    public FileInfo() {}

    public FileInfo(String name, String type, boolean isDirectory, long size, LocalDateTime lastModified, String path) {
        this.name = name;
        this.type = type;
        this.isDirectory = isDirectory;
        this.size = size;
        this.lastModified = lastModified;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}