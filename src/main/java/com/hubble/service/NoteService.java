package com.hubble.service;

import com.hubble.dto.*;
import com.hubble.entity.*;
import com.hubble.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {
    
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;
    private final TagRepository tagRepository;
    private final NoteTagRepository noteTagRepository;
    private final SearchService searchService;

    public NoteResponse createNote(Long userId, CreateNoteRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Note note = new Note();
        note.setUser(user);
        note.setTitle(request.getTitle());
        note.setSlug(generateSlug(request.getTitle(), userId));
        note.setContent(request.getContent());
        note.setSummary(request.getSummary());
        note.setContentType(request.getContentType());
        note.setThumbnailUrl(request.getThumbnailUrl());
        note.setVisibility(request.getVisibility());
        note.setStatus("DRAFT");
        
        if (request.getSeriesId() != null) {
            Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new IllegalArgumentException("Series not found"));
            note.setSeries(series);
            note.setSeriesOrder(request.getSeriesOrder());
        }
        
        note.setMetaTitle(request.getMetaTitle());
        note.setMetaDescription(request.getMetaDescription());
        note.setMetaKeywords(request.getMetaKeywords());
        
        // note.setReadingTime(calculateReadingTime(request.getContent()));
        
        if (request.getScheduledAt() != null) {
            note.setStatus("SCHEDULED");
            note.setScheduledAt(request.getScheduledAt());
        }
        
        note = noteRepository.save(note);
        
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            addTagsToNote(note, request.getTags());
        }
        
        return toNoteResponse(note);
    }

    public NoteResponse updateNote(Long userId, Long noteId, UpdateNoteRequest request) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
            note.setSlug(generateSlug(request.getTitle(), userId));
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
            // note.setReadingTime(calculateReadingTime(request.getContent()));
        }
        if (request.getSummary() != null) note.setSummary(request.getSummary());
        if (request.getThumbnailUrl() != null) note.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getVisibility() != null) note.setVisibility(request.getVisibility());
        
        if (request.getSeriesId() != null) {
            Series series = seriesRepository.findById(request.getSeriesId())
                .orElseThrow(() -> new IllegalArgumentException("Series not found"));
            note.setSeries(series);
            note.setSeriesOrder(request.getSeriesOrder());
        }
        
        if (request.getMetaTitle() != null) note.setMetaTitle(request.getMetaTitle());
        if (request.getMetaDescription() != null) note.setMetaDescription(request.getMetaDescription());
        if (request.getMetaKeywords() != null) note.setMetaKeywords(request.getMetaKeywords());
        
        if (request.getTags() != null) {
            noteTagRepository.deleteAllByNoteId(noteId);
            addTagsToNote(note, request.getTags());
        }
        
        note = noteRepository.save(note);
        return toNoteResponse(note);
    }

    public NoteResponse publishNote(Long userId, Long noteId, PublishNoteRequest request) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        if (request.getScheduledAt() != null) {
            note.setStatus("SCHEDULED");
            note.setScheduledAt(request.getScheduledAt());
        } else {
            note.setStatus("PUBLISHED");
            note.setPublishedAt(OffsetDateTime.now());
        }
        
        note = noteRepository.save(note);
        
        if ("PUBLISHED".equals(note.getStatus())) {
            searchService.indexNote(note);
        }
        
        return toNoteResponse(note);
    }

    public void deleteNote(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        if (!note.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        noteRepository.delete(note);
        searchService.deleteNoteFromIndex(noteId);
    }

    @Transactional(readOnly = true)
    public NoteResponse getNote(Long noteId, Long currentUserId) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        if (note.getVisibility().equals("PRIVATE") && 
            !note.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        note.setViewsCount(note.getViewsCount() + 1);
        noteRepository.save(note);
        
        return toNoteResponse(note);
    }

    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getMyNotes(Long userId, String status, Pageable pageable) {
        Page<Note> notes = noteRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
            userId, status, pageable);
        return notes.map(this::toNoteListItem);
    }

    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getPublicNotes(Pageable pageable) {
        return noteRepository.findPublicNotes(pageable).map(this::toNoteListItem);
    }

    @Transactional(readOnly = true)
    public Page<NoteListItemDto> searchNotes(String keyword, Pageable pageable) {
        return noteRepository.searchNotes(keyword, pageable).map(this::toNoteListItem);
    }

    private String generateSlug(String title, Long userId) {
        String slug = title.toLowerCase()
            .replaceAll("[^a-z0-9가-힣]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        
        String finalSlug = slug;
        int counter = 1;
        while (noteRepository.findByUserIdAndSlug(userId, finalSlug).isPresent()) {
            finalSlug = slug + "-" + counter++;
        }
        
        return finalSlug;
    }
    
    /*
    private int calculateReadingTime(String content) {
        int koreanChars = content.replaceAll("[^가-힣]", "").length();
        int englishWords = content.split("\\s+").length;
        int minutes = (koreanChars / 500) + (englishWords / 200);
        return Math.max(1, minutes);
    }
    */
    
    private void addTagsToNote(Note note, List<String> tagNames) {
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    newTag.setSlug(tagName.toLowerCase().replaceAll("[^a-z0-9가-힣]", "-"));
                    return tagRepository.save(newTag);
                });
            
            NoteTag noteTag = new NoteTag();
            noteTag.setNote(note);
            noteTag.setTag(tag);
            noteTagRepository.save(noteTag);
        }
    }
    
    private NoteResponse toNoteResponse(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setUserId(note.getUser().getId());
        response.setUserNickname(note.getUser().getNickname());
        response.setTitle(note.getTitle());
        response.setSlug(note.getSlug());
        response.setSummary(note.getSummary());
        response.setContent(note.getContent());
        response.setContentType(note.getContentType());
        response.setThumbnailUrl(note.getThumbnailUrl());
        response.setStatus(note.getStatus());
        response.setVisibility(note.getVisibility());
        response.setViewsCount(note.getViewsCount());
        response.setLikesCount(note.getLikesCount());
        response.setCommentsCount(note.getCommentsCount());
        response.setBookmarksCount(note.getBookmarksCount());
        response.setReadingTime(note.getReadingTime());
        response.setPublishedAt(note.getPublishedAt());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        
        if (note.getSeries() != null) {
            SeriesSimpleDto seriesDto = new SeriesSimpleDto();
            seriesDto.setId(note.getSeries().getId());
            seriesDto.setTitle(note.getSeries().getTitle());
            seriesDto.setSlug(note.getSeries().getSlug());
            response.setSeries(seriesDto);
        }
        
        List<TagDto> tags = note.getNoteTags().stream()
            .map(nt -> {
                TagDto dto = new TagDto();
                dto.setId(nt.getTag().getId());
                dto.setName(nt.getTag().getName());
                dto.setSlug(nt.getTag().getSlug());
                return dto;
            })
            .collect(Collectors.toList());
        response.setTags(tags);
        
        return response;
    }
    
    private NoteListItemDto toNoteListItem(Note note) {
        NoteListItemDto dto = new NoteListItemDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setSlug(note.getSlug());
        dto.setSummary(note.getSummary());
        dto.setThumbnailUrl(note.getThumbnailUrl());
        dto.setUserId(note.getUser().getId());
        dto.setUserNickname(note.getUser().getNickname());
        dto.setViewsCount(note.getViewsCount());
        dto.setLikesCount(note.getLikesCount());
        dto.setCommentsCount(note.getCommentsCount());
        dto.setReadingTime(note.getReadingTime());
        dto.setPublishedAt(note.getPublishedAt());
        
        List<TagDto> tags = note.getNoteTags().stream()
            .map(nt -> {
                TagDto tagDto = new TagDto();
                tagDto.setId(nt.getTag().getId());
                tagDto.setName(nt.getTag().getName());
                tagDto.setSlug(nt.getTag().getSlug());
                return tagDto;
            })
            .collect(Collectors.toList());
        dto.setTags(tags);
        
        return dto;
    }
}
