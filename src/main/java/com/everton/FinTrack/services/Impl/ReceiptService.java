package com.seuprojeto.fintrack.service;

import com.seuprojeto.fintrack.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class ReceiptService {

    private static final String UPLOAD_DIR = "uploads/";

    @Async
    public void saveReceiptAsync(MultipartFile file, Transaction transaction) {
        try {
            if (file.isEmpty()) return;

            // Cria diretório se não existir
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Define o caminho do arquivo
            String fileName = "receipt_" + transaction.getId() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Salva o arquivo fisicamente
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info(" Comprovante salvo: {}", filePath);
        } catch (IOException e) {
            log.error(" Erro ao salvar comprovante: {}", e.getMessage());
        }
    }
}
