package com.hubble.dto;

public class BookmarkRequest {
    
    private String folder = "default";
    private String notesText;

    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
    public String getNotesText() { return notesText; }
    public void setNotesText(String notesText) { this.notesText = notesText; }
}
