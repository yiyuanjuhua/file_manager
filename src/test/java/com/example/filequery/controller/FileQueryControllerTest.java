package com.example.filequery.controller;

import com.example.filequery.domain.FileInfo;
import com.example.filequery.service.FileQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 文件查询控制器测试类
 */
class FileQueryControllerTest {

    @Mock
    private FileQueryService fileQueryService;

    @InjectMocks
    private FileQueryController fileQueryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试场景1：正常查询目录文件列表
    @Test
    void testListFiles_Success() {
        // 准备测试数据
        String directoryPath = "/test/directory";
        List<FileInfo> mockFileList = Arrays.asList(
            new FileInfo("file1.txt", "txt", false, 1024, LocalDateTime.now(), "/test/directory/file1.txt"),
            new FileInfo("file2.java", "java", false, 2048, LocalDateTime.now(), "/test/directory/file2.java")
        );
        
        when(fileQueryService.listDirectoryFiles(directoryPath)).thenReturn(mockFileList);

        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(directoryPath);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(fileQueryService, times(1)).listDirectoryFiles(directoryPath);
    }

    // 测试场景2：查询空目录
    @Test
    void testListFiles_EmptyDirectory() {
        // 准备测试数据
        String directoryPath = "/empty/directory";
        
        when(fileQueryService.listDirectoryFiles(directoryPath)).thenReturn(Collections.emptyList());

        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(directoryPath);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        verify(fileQueryService, times(1)).listDirectoryFiles(directoryPath);
    }

    // 测试场景3：查询目录参数无效
    @Test
    void testListFiles_InvalidParameter() {
        // 准备测试数据
        String invalidPath = "";
        
        when(fileQueryService.listDirectoryFiles(invalidPath))
            .thenThrow(new IllegalArgumentException("目录路径不能为空"));

        // 执行测试
        ResponseEntity<List<FileInfo>> response = fileQueryController.listFiles(invalidPath);

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(fileQueryService, times(1)).listDirectoryFiles(invalidPath);
    }

    // 测试场景4：正常查询文件信息
    @Test
    void testGetFileInfo_Success() {
        // 准备测试数据
        String filePath = "/test/file.txt";
        FileInfo mockFileInfo = new FileInfo("file.txt", "txt", false, 1024, LocalDateTime.now(), filePath);
        
        when(fileQueryService.getFileDetails(filePath)).thenReturn(Optional.of(mockFileInfo));

        // 执行测试
        ResponseEntity<FileInfo> response = fileQueryController.getFileInfo(filePath);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("file.txt", response.getBody().getName());
        verify(fileQueryService, times(1)).getFileDetails(filePath);
    }

    // 测试场景5：查询不存在的文件
    @Test
    void testGetFileInfo_FileNotFound() {
        // 准备测试数据
        String nonExistentPath = "/non/existent/file.txt";
        
        when(fileQueryService.getFileDetails(nonExistentPath)).thenReturn(Optional.empty());

        // 执行测试
        ResponseEntity<FileInfo> response = fileQueryController.getFileInfo(nonExistentPath);

        // 验证结果
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(fileQueryService, times(1)).getFileDetails(nonExistentPath);
    }

    // 测试场景6：查询文件参数无效
    @Test
    void testGetFileInfo_InvalidParameter() {
        // 准备测试数据
        String invalidPath = null;
        
        when(fileQueryService.getFileDetails(invalidPath))
            .thenThrow(new IllegalArgumentException("文件路径不能为空"));

        // 执行测试
        ResponseEntity<FileInfo> response = fileQueryController.getFileInfo(invalidPath);

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(fileQueryService, times(1)).getFileDetails(invalidPath);
    }
}