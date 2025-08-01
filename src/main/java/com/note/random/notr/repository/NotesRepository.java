package com.note.random.notr.repository;

import com.note.random.notr.entity.Notes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {
    Optional<Notes> findByLinkId(String linkId);
}

