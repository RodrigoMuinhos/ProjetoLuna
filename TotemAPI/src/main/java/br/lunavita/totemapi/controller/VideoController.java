package br.lunavita.totemapi.controller;

import br.lunavita.totemapi.model.Video;
import br.lunavita.totemapi.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Video management controller for admin operations
 */
@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * Upload a new video (admin only)
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMINISTRACAO')")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {

        try {
            logger.info("[VIDEO-CTRL] POST /upload - Recebendo vídeo: {}", file.getOriginalFilename());

            Video video = videoService.uploadVideo(file, title, description);

            logger.info("[VIDEO-CTRL] ✅ Vídeo enviado com sucesso: {}", video.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vídeo enviado com sucesso",
                    "video", video));
        } catch (IllegalArgumentException e) {
            logger.warn("[VIDEO-CTRL] ❌ Validação falhou: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("[VIDEO-CTRL] ❌ Limite atingido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        } catch (IOException e) {
            logger.error("[VIDEO-CTRL] ❌ Erro ao salvar arquivo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Erro ao salvar arquivo"));
        }
    }

    /**
     * Get all videos (admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRACAO')")
    public ResponseEntity<?> getAllVideos() {
        logger.info("[VIDEO-CTRL] GET / - Listando todos os vídeos");
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", videos.size(),
                "videos", videos));
    }

    /**
     * Get active videos for carousel (public)
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveVideos() {
        logger.info("[VIDEO-CTRL] GET /active - Listando vídeos ativos");
        List<Video> videos = videoService.getActiveVideos();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", videos.size(),
                "videos", videos));
    }

    /**
     * Get video by ID (admin)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRACAO')")
    public ResponseEntity<?> getVideoById(@PathVariable UUID id) {
        logger.info("[VIDEO-CTRL] GET /{} - Obtendo vídeo", id);

        Optional<Video> video = videoService.getVideoById(id);
        if (video.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "video", video.get()));
    }

    /**
     * Update video metadata (admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRACAO')")
    public ResponseEntity<?> updateVideo(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> payload) {

        try {
            logger.info("[VIDEO-CTRL] PUT /{} - Atualizando vídeo", id);

            String title = (String) payload.get("title");
            String description = (String) payload.get("description");
            Integer displayOrder = payload.get("displayOrder") != null
                    ? ((Number) payload.get("displayOrder")).intValue()
                    : null;
            Boolean isActive = (Boolean) payload.get("isActive");

            Video updated = videoService.updateVideo(id, title, description, displayOrder, isActive);

            logger.info("[VIDEO-CTRL] ✅ Vídeo atualizado: {}", id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vídeo atualizado com sucesso",
                    "video", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        } catch (IOException e) {
            logger.error("[VIDEO-CTRL] ❌ Erro ao atualizar vídeo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Erro ao atualizar vídeo"));
        }
    }

    /**
     * Delete video (admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRACAO')")
    public ResponseEntity<?> deleteVideo(@PathVariable UUID id) {
        try {
            logger.info("[VIDEO-CTRL] DELETE /{} - Deletando vídeo", id);

            videoService.deleteVideo(id);

            logger.info("[VIDEO-CTRL] ✅ Vídeo deletado: {}", id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vídeo deletado com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        } catch (IOException e) {
            logger.error("[VIDEO-CTRL] ❌ Erro ao deletar vídeo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Erro ao deletar arquivo"));
        }
    }

    /**
     * Reorder videos (admin)
     */
    @PostMapping("/reorder")
    @PreAuthorize("hasRole('ADMINISTRACAO')")
    public ResponseEntity<?> reorderVideos(@RequestBody List<UUID> videoIds) {
        try {
            logger.info("[VIDEO-CTRL] POST /reorder - Reordenando {} vídeos", videoIds.size());

            videoService.reorderVideos(videoIds);

            logger.info("[VIDEO-CTRL] ✅ Vídeos reordenados");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Vídeos reordenados com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));
        }
    }
}
