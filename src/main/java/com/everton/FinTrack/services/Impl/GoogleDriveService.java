package com.everton.FinTrack.services.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class GoogleDriveService {

    private final OAuth2AuthorizedClientService clientService;

    @Value("${google.drive.folder-id:1iLzI61N8Zxj_ys2BxNs6CLa8qSEkc7bu}")
    private String folderId;

    @Async
    public CompletableFuture<String> uploadFileAsync(MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                String registrationId = oauthToken.getAuthorizedClientRegistrationId();
                OAuth2AuthorizedClient client =
                        clientService.loadAuthorizedClient(registrationId, oauthToken.getName());

                if (client == null) {
                    throw new IllegalStateException("Cliente OAuth2 não encontrado. Faça login com Google.");
                }

                String accessToken = client.getAccessToken().getTokenValue();
                String fileId = sendFileToGoogleDrive(file, accessToken);
                return CompletableFuture.completedFuture(fileId);
            } else {
                throw new IllegalStateException("Usuário não está autenticado via Google.");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro no upload assíncrono: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    private String sendFileToGoogleDrive(MultipartFile file, String accessToken) throws IOException {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();

        String metadataJson = "{"
                + "\"name\": \"" + file.getOriginalFilename() + "\","
                + "\"mimeType\": \"" + file.getContentType() + "\","
                + "\"parents\": [\"" + folderId + "\"]"
                + "}";

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("metadata", metadataJson)
                .header("Content-Type", "application/json; charset=UTF-8");
        builder.part("file", file.getResource())
                .header("Content-Type", file.getContentType());

        String response = webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response);

        return node.get("id").asText();
    }

    public String generateDriveFileLink(String fileId) {
        return "https://drive.google.com/file/d/" + fileId + "/view?usp=sharing";
    }

    // 🔧 Método compatível com as chamadas antigas (controllers/services)
    public Map<String, String> uploadFile(MultipartFile file) throws IOException {
        try {
            // Executa o upload de forma síncrona, usando o método assíncrono internamente
            String fileId = uploadFileAsync(file).join();
            String fileUrl = generateDriveFileLink(fileId);

            Map<String, String> response = new HashMap<>();
            response.put("fileId", fileId);
            response.put("fileUrl", fileUrl);
            return response;
        } catch (Exception e) {
            throw new IOException("Erro ao enviar arquivo para o Google Drive: " + e.getMessage(), e);
        }
    }
}
