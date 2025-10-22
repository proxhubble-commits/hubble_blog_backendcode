package com.hubble.service;

import com.hubble.dto.NoteListItemDto;
import com.hubble.entity.Note;
import com.hubble.repository.NoteRepository;
import com.hubble.search.NoteDocument;
import com.hubble.search.NoteSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    
    private final NoteSearchRepository searchRepository;
    private final NoteRepository noteRepository;

    @Transactional(readOnly = true)
    public Page<NoteListItemDto> searchNotes(String keyword, Pageable pageable) {
        Page<NoteDocument> documents = searchRepository.searchByKeyword(keyword, pageable);
        return documents.map(this::toNoteListItem);
    }

    @Transactional(readOnly = true)
    public Page<NoteListItemDto> searchByTag(String tag, Pageable pageable) {
        Page<NoteDocument> documents = searchRepository.searchByTag(tag, pageable);
        return documents.map(this::toNoteListItem);
    }

    @Transactional
    public void indexNote(Note note) {
        if (!"PUBLISHED".equals(note.getStatus()) || !"PUBLIC".equals(note.getVisibility())) {
            return;
        }
        
        NoteDocument document = toNoteDocument(note);
        searchRepository.save(document);
    }

    @Transactional
    public void deleteNoteFromIndex(Long noteId) {
        searchRepository.deleteById(noteId);
    }

    @Transactional
    public void reindexAllNotes() {
        List<Note> notes = noteRepository.findAll().stream()
            .filter(note -> "PUBLISHED".equals(note.getStatus()) && "PUBLIC".equals(note.getVisibility()))
            .collect(Collectors.toList());
        
        List<NoteDocument> documents = notes.stream()
            .map(this::toNoteDocument)
            .collect(Collectors.toList());
        
        searchRepository.saveAll(documents);
    }

    private NoteDocument toNoteDocument(Note note) {
        NoteDocument document = new NoteDocument();
        document.setId(note.getId());
        document.setUserId(note.getUser().getId());
        document.setUserNickname(note.getUser().getNickname());
        document.setTitle(note.getTitle());
        document.setSlug(note.getSlug());
        document.setSummary(note.getSummary());
        document.setContent(note.getContent());
        document.setStatus(note.getStatus());
        document.setVisibility(note.getVisibility());
        document.setViewsCount(note.getViewsCount());
        document.setLikesCount(note.getLikesCount());
        document.setCommentsCount(note.getCommentsCount());
        document.setPublishedAt(note.getPublishedAt());
        document.setCreatedAt(note.getCreatedAt());
        
        List<String> tags = note.getNoteTags().stream()
            .map(nt -> nt.getTag().getName())
            .collect(Collectors.toList());
        document.setTags(tags);
        
        if (note.getSeries() != null) {
            document.setSeriesId(note.getSeries().getId());
            document.setSeriesTitle(note.getSeries().getTitle());
        }
        
        return document;
    }
    
    private NoteListItemDto toNoteListItem(NoteDocument document) {
        NoteListItemDto dto = new NoteListItemDto();
        return dto;
    }
}
