package com.example.elozelo.Controller;

import com.example.elozelo.DTO.TaskColumnDTO;
import com.example.elozelo.DTO.TaskDTO;
import com.example.elozelo.Entity.Board;
import com.example.elozelo.Entity.Task;
import com.example.elozelo.Entity.TaskColumn;
import com.example.elozelo.Repository.BoardRepository;
import com.example.elozelo.Repository.TaskColumnRepository;
import com.example.elozelo.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/columns")
@RequiredArgsConstructor
public class TaskColumnController {
    private final TaskColumnRepository repository;
    private final BoardRepository boardRepository;
    private final TaskRepository taskRepository;

    @GetMapping
    public List<TaskColumnDTO> getAllTaskColumns() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskColumnDTO> getTaskColumnById(@PathVariable Long id) {
        TaskColumn taskColumn = repository.findById(id).orElse(null);
        if (taskColumn != null) {
            return ResponseEntity.ok(mapToDTO(taskColumn));
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<TaskColumnDTO> createTaskColumn(@RequestBody TaskColumnDTO dto) {
        TaskColumn entity = new TaskColumn();
        entity.setName(dto.getName());

        if (dto.getBoardId() != null) {
            Board board = boardRepository.findById(dto.getBoardId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono tablicy"));
            entity.setBoard(board);
        }


        TaskColumn savedEntity = repository.save(entity);
        return ResponseEntity.ok(mapToDTO(savedEntity));

    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskColumnDTO> updateTaskColumn(@PathVariable Long id, @RequestBody TaskColumnDTO dto) {
        TaskColumn entity = repository.findById(id).orElse(null);

        if (entity == null) {
            return  ResponseEntity.notFound().build();
        }

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }

        if (dto.getBoardId() != null) {
            Board board = boardRepository.findById(dto.getBoardId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono tablicy"));
            entity.setBoard(board);
        }



        TaskColumn savedEntity = repository.save(entity);

        return ResponseEntity.ok(mapToDTO(savedEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTaskColumn(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Usunięto");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Błąd: Nie znaleziono id");

    }

    private TaskColumnDTO mapToDTO(TaskColumn entity) {
        TaskColumnDTO dto = new TaskColumnDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        if (entity.getBoard() != null) {
            dto.setBoardId(entity.getBoard().getId());
        }

        if (entity.getTasks() != null) {
            List<TaskDTO> taskDTOS = entity.getTasks().stream()
                    .map(this::mapTaskToDTO)
                    .collect(Collectors.toList());
            dto.setTasks(taskDTOS);
        }
        return dto;
    }

    private TaskDTO mapTaskToDTO(Task entity) {
        TaskDTO dto = new TaskDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        if (entity.getColumn() != null) {
            dto.setColumnId(entity.getColumn().getId());
        }

        return dto;
    }

}
