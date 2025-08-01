package com.note.random.notr.controller;

import com.note.random.notr.entity.Notes;
import com.note.random.notr.repository.NotesRepository;
import com.note.random.notr.util.Base62Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NotesController {

    private final NotesRepository repo;

    @PostMapping
    public ResponseEntity<Map<String, String>> createNote(@RequestBody Map<String, Object> body) {
        String content = (String) body.get("content");
        String expiryMinutes = Optional.ofNullable(body.get("expireInMinutes"))
                .map(Object::toString)
                .orElse(null);

        String linkId;
        do {
            linkId = Base62Utils.generateRandomBase62(8);
        } while (repo.findByLinkId(linkId).isPresent());

        Notes note = Notes.builder()
            .linkId(linkId)
            .content(content)
            .createdAt(LocalDateTime.now())
            .build();

        if (expiryMinutes != null) {
            try {
                int minutes = Integer.parseInt(expiryMinutes);
                note.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));
            } catch (NumberFormatException ignored) {
            }
        }

        repo.save(note);
        return ResponseEntity.ok(Map.of("link", linkId));
    }

    @GetMapping("/{linkId}")
    public ResponseEntity<Map<String, String>> getNote(@PathVariable String linkId) {
        Optional<Notes> opt = repo.findByLinkId(linkId);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Note not found"));
        }

        Notes note = opt.get();
        if (note.getExpiresAt() != null && note.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.ok(Map.of("message", "Note expired"));
        }

        return ResponseEntity.ok(Map.of("content", note.getContent()));
    }

    @PutMapping("/{linkId}")
    public ResponseEntity<?> updateNote(@PathVariable String linkId, @RequestBody Map<String, Object> body) {
        Optional<Notes> opt = repo.findByLinkId(linkId);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Note not found"));
        }

        Notes note = opt.get();
        if (note.getExpiresAt() != null && note.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.ok(Map.of("message", "Note expired and cannot be updated"));
        }

        String updatedContent = (String) body.get("content");
        String expiryMinutes = Optional.ofNullable(body.get("expireInMinutes"))
                .map(Object::toString)
                .orElse(null);
        note.setContent(updatedContent);
        if (expiryMinutes != null) {
            try {
                int minutes = Integer.parseInt(expiryMinutes);
                note.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));
            } catch (NumberFormatException ignored) {
            }
        }
        repo.save(note);

        return ResponseEntity.ok(Map.of("message", "Note updated successfully"));
    }
}

