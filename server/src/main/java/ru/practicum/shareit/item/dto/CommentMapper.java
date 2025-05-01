package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
@AllArgsConstructor
public class CommentMapper {
    public Comment fromDto(CommentDto dto) {
        Comment comment = new Comment();
        comment.setAuthorName(dto.getAuthorName());
        comment.setCreated(dto.getCreated());
        comment.setText(dto.getText());
        return comment;
    }

    public static CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setCreated(comment.getCreated());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem());
        return commentDto;
    }
}
