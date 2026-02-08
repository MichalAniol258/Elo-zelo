package com.example.elozelo.Controller;



import com.example.elozelo.DTO.BoardDTO;
import com.example.elozelo.DTO.TaskColumnDTO;
import com.example.elozelo.DTO.TaskDTO;
import com.example.elozelo.Entity.Board;
import com.example.elozelo.Entity.Task;
import com.example.elozelo.Entity.TaskColumn;
import com.example.elozelo.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardRepository repository;

    @GetMapping()
    public List<BoardDTO> getAllBoards() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoardById(@PathVariable Long id) {
        Board entity = repository.findById(id).orElse(null);
        if (entity != null) {
            return ResponseEntity.ok(mapToDTO(entity));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardDTO> updateBoard(@PathVariable Long id, @RequestBody Board dto) {
        Board entity = repository.findById(id).orElse(null);

        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        if (entity.getName() != null) {
            entity.setName(dto.getName());
        }



        Board savedEntity = repository.save(entity);

        return ResponseEntity.ok(mapToDTO(savedEntity));

    }

    @PostMapping
    public ResponseEntity<BoardDTO> createBoard(@RequestBody BoardDTO dto) {
        Board entity = new Board();
        entity.setName(dto.getName());
        Board savedEntity = repository.save(entity);
        return ResponseEntity.ok(mapToDTO(savedEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Usunięto");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Błąd: Nie znaleziono id");

    }

    private BoardDTO mapToDTO(Board entity) {
        BoardDTO dto = new BoardDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        if (entity.getColumns() != null) {
            List<TaskColumnDTO> taskColumnDTOS = entity.getColumns().stream()
                    .map(this::mapTaskColumnToDTO)
                    .collect(Collectors.toList());
            dto.setColumns(taskColumnDTOS);
        }


        return dto;
    }

    private TaskColumnDTO mapTaskColumnToDTO(TaskColumn entity) {
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
