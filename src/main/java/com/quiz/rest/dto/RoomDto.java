package com.quiz.rest.dto;

import com.quiz.model.Room;

public class RoomDto {
    public Long id;
    public String code;
    public String status;

    public static RoomDto depuis(Room room) {
        RoomDto dto = new RoomDto();
        dto.id = room.getId();
        dto.code = room.getCode();
        dto.status = room.getStatut().name();
        return dto;
        
    }
    
}
