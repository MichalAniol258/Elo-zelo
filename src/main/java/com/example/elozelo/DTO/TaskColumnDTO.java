package com.example.elozelo.DTO;

import lombok.Data;

import java.util.List;

@Data
public class TaskColumnDTO {
    private Long id;
    private String name;
    private Long boardId;
    private List<TaskDTO> tasks;
}
