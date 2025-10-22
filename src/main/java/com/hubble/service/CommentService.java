package com.hubble.service;

import com.hubble.dto.*;
import com.hubble.entity.*;
import com.hubble.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    
    private final NoteCommentRepository commentRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentResponse createComment(Long userId, Long noteId, CreateCommentRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new IllegalArgumentException("Note not found"));
        
        NoteComment comment = new NoteComment();
        comment.setUser(user);
        comment.setNote(note);
        comment.setContent(request.getContent());
        
        if (request.getParentId() != null) {
            NoteComment parent = commentRepository.findById(request.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParent(parent);
        }
        
        if (note.getUser().getId().equals(userId)) {
            comment.setIsAuthorReply(true);
        }
        
        comment = commentRepository.save(comment);
        return toCommentResponse(comment, userId);
    }

    public CommentResponse updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        NoteComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
        
        return toCommentResponse(comment, userId);
    }

    public void deleteComment(Long userId, Long commentId) {
        NoteComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        comment.setIsDeleted(true);
        comment.setContent("삭제된 댓글입니다.");
        commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Long noteId, Long currentUserId, Pageable pageable) {
        Page<NoteComment> comments = commentRepository.findTopLevelComments(noteId, pageable);
        return comments.map(comment -> toCommentResponseWithReplies(comment, currentUserId));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getReplies(Long commentId, Long currentUserId) {
        List<NoteComment> replies = commentRepository.findReplies(commentId);
        return replies.stream()
            .map(reply -> toCommentResponse(reply, currentUserId))
            .collect(Collectors.toList());
    }

    public void toggleLike(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        NoteComment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
            .ifPresentOrElse(
                like -> commentLikeRepository.delete(like),
                () -> {
                    CommentLike like = new CommentLike();
                    like.setComment(comment);
                    like.setUser(user);
                    commentLikeRepository.save(like);
                }
            );
    }

    private CommentResponse toCommentResponse(NoteComment comment, Long currentUserId) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setNoteId(comment.getNote().getId());
        response.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        response.setUserId(comment.getUser().getId());
        response.setUserNickname(comment.getUser().getNickname());
        response.setContent(comment.getContent());
        response.setLikesCount(comment.getLikesCount());
        response.setIsDeleted(comment.getIsDeleted());
        response.setIsAuthorReply(comment.getIsAuthorReply());
        
        if (currentUserId != null) {
            boolean isLiked = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), currentUserId);
            response.setIsLikedByMe(isLiked);
        }
        
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        
        return response;
    }
    
    private CommentResponse toCommentResponseWithReplies(NoteComment comment, Long currentUserId) {
        CommentResponse response = toCommentResponse(comment, currentUserId);
        
        List<CommentResponse> replies = comment.getReplies().stream()
            .filter(reply -> !reply.getIsDeleted())
            .map(reply -> toCommentResponse(reply, currentUserId))
            .collect(Collectors.toList());
        response.setReplies(replies);
        
        return response;
    }
}
