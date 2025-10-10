package com.everton.FinTrack.service;

import com.everton.FinTrack.model.Transaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ReceiptService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptService.class);
    private static final String UPLOAD_DIR = "uploads/";

    @Async
    public void saveReceiptAsync(MultipartFile file, Transaction transaction) {
        try {
            if (file == null || file.isEmpty()) {
                log.warn("Nenhum arquivo enviado para a transação ID: {}", 
                         transaction != null ? transaction.getId() : "desconhecido");
                return;
            }

            // Cria diretório se não existir
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Diretório de upload criado em: {}", uploadPath.toAbsolutePath());
            }

            // Define o nome do arquivo
            String fileName = "receipt_" + transaction.getId() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Salva o arquivo fisicamente
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Comprovante salvo com sucesso: {}", filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error(" Erro ao salvar comprovante: {}", e.getMessage(), e);
        }
    }
}
