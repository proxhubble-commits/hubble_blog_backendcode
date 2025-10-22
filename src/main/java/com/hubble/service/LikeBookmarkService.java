package com.hubble.service;

import com.hubble.dto.*;
import com.hubble.entity.*;
import com.hubble.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeBookmarkService {
    
    private final NoteLikeRepository noteLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public void toggleLike(Long userId, Long noteId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        noteLikeRepository.findByNoteIdAndUserId(noteId, userId)
            .ifPresentOrElse(
                like -> noteLikeRepository.delete(like),
                () -> {
                    NoteLike like = new NoteLike();
                    like.setNote(note);
                    like.setUser(user);
                    noteLikeRepository.save(like);
                }
            );
    }

    public BookmarkResponse addOrUpdateBookmark(Long userId, Long noteId, BookmarkRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        Bookmark bookmark = bookmarkRepository.findByUserIdAndNoteId(userId, noteId)
            .orElseGet(() -> {
                Bookmark newBookmark = new Bookmark();
                newBookmark.setUser(user);
                newBookmark.setNote(note);
                return newBookmark;
            });
        
        bookmark.setFolder(request.getFolder());
        bookmark.setNotesText(request.getNotesText());
        bookmark = bookmarkRepository.save(bookmark);
        
        return toBookmarkResponse(bookmark);
    }

    public void removeBookmark(Long userId, Long noteId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndNoteId(userId, noteId)
            .orElseThrow(() -> new IllegalArgumentException("Bookmark not found"));
        
        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public LikeBookmarkStatusResponse getStatus(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        LikeBookmarkStatusResponse response = new LikeBookmarkStatusResponse();
        
        if (userId != null) {
            response.setIsLiked(noteLikeRepository.existsByNoteIdAndUserId(noteId, userId));
            response.setIsBookmarked(bookmarkRepository.existsByUserIdAndNoteId(userId, noteId));
        } else {
            response.setIsLiked(false);
            response.setIsBookmarked(false);
        }
        
        response.setLikesCount(note.getLikesCount());
        response.setBookmarksCount(note.getBookmarksCount());
        
        return response;
    }

    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getMyBookmarks(Long userId, String folder, Pageable pageable) {
        Page<Bookmark> bookmarks;
        
        if (folder != null && !folder.isEmpty()) {
            bookmarks = bookmarkRepository.findByUserIdAndFolderOrderByCreatedAtDesc(userId, folder, pageable);
        } else {
            bookmarks = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        return bookmarks.map(this::toBookmarkResponse);
    }

    @Transactional(readOnly = true)
    public Page<NoteListItemDto> getLikedNotes(Long userId, Pageable pageable) {
        Page<Note> notes = noteLikeRepository.findLikedNotesByUserId(userId, pageable);
        return notes.map(this::toNoteListItem);
    }

    private BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        BookmarkResponse response = new BookmarkResponse();
        response.setId(bookmark.getId());
        response.setNoteId(bookmark.getNote().getId());
        response.setNoteTitle(bookmark.getNote().getTitle());
        response.setNoteSlug(bookmark.getNote().getSlug());
        response.setNoteThumbnailUrl(bookmark.getNote().getThumbnailUrl());
        response.setFolder(bookmark.getFolder());
        response.setNotesText(bookmark.getNotesText());
        response.setCreatedAt(bookmark.getCreatedAt());
        response.setNoteAuthorId(bookmark.getNote().getUser().getId());
        response.setNoteAuthorNickname(bookmark.getNote().getUser().getNickname());
        return response;
    }
    
    private NoteListItemDto toNoteListItem(Note note) {
        NoteListItemDto dto = new NoteListItemDto();
        return dto;
    }
}
