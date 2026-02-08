package com.example.elozelo.Controller;

import com.example.elozelo.DTO.TaskDTO;

import com.example.elozelo.Entity.Task;
import com.example.elozelo.Entity.TaskColumn;

import com.example.elozelo.Repository.TaskColumnRepository;
import com.example.elozelo.Repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository repository;
    private final TaskColumnRepository taskColumnRepository;

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        Task task = repository.findById(id).orElse(null);
        if (task != null) {
            return ResponseEntity.ok(mapToDTO(task));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO dto) {
        Task entity = repository.findById(id).orElse(null);

        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }


        if (dto.getColumnId() != null) {
            TaskColumn column = taskColumnRepository.findById(dto.getColumnId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono kolumny"));
            entity.setColumn(column);
        }

        Task savedEntity = repository.save(entity);
        return ResponseEntity.ok(mapToDTO(savedEntity));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO dto) {
        Task entity = new Task();
        entity.setName(dto.getName());

        if (dto.getColumnId() != null) {
            TaskColumn column = taskColumnRepository.findById(dto.getColumnId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono kolumny"));
            entity.setColumn(column);
        }

        Task savedEntity = repository.save(entity);
        return ResponseEntity.ok(mapToDTO(savedEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Usunięto");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Błąd: Nie znaleziono id");

    }

    private TaskDTO mapToDTO(Task entity) {
        TaskDTO dto = new TaskDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        if (entity.getColumn() != null) {
            dto.setColumnId(entity.getColumn().getId());
        }

        return dto;
    }



}
