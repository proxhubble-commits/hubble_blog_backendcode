package com.hubble.dto;

import java.time.OffsetDateTime;

public class BookmarkResponse {
    
    private Long id;
    private Long noteId;
    private String noteTitle;
    private String noteSlug;
    private String noteThumbnailUrl;
    private String folder;
    private String notesText;
    private OffsetDateTime createdAt;
    
    private Long noteAuthorId;
    private String noteAuthorNickname;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public String getNoteTitle() { return noteTitle; }
    public void setNoteTitle(String noteTitle) { this.noteTitle = noteTitle; }
    public String getNoteSlug() { return noteSlug; }
    public void setNoteSlug(String noteSlug) { this.noteSlug = noteSlug; }
    public String getNoteThumbnailUrl() { return noteThumbnailUrl; }
    public void setNoteThumbnailUrl(String noteThumbnailUrl) { this.noteThumbnailUrl = noteThumbnailUrl; }
    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
    public String getNotesText() { return notesText; }
    public void setNotesText(String notesText) { this.notesText = notesText; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public Long getNoteAuthorId() { return noteAuthorId; }
    public void setNoteAuthorId(Long noteAuthorId) { this.noteAuthorId = noteAuthorId; }
    public String getNoteAuthorNickname() { return noteAuthorNickname; }
    public void setNoteAuthorNickname(String noteAuthorNickname) { this.noteAuthorNickname = noteAuthorNickname; }
}
