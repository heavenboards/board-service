package heavenboards.board.service.board.controller;

import heavenboards.board.service.board.service.BoardCreateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.board.BoardOperationResultTo;
import transfer.contract.domain.board.BoardTo;

/**
 * Контроллер для взаимодействия с проектами.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
@Tag(name = "BoardController", description = "Контроллер для взаимодействия с досками")
public class BoardController {
    /**
     * Use case создания доски.
     */
    private final BoardCreateUseCase boardCreateUseCase;

    /**
     * Создание доски.
     *
     * @param board - to-модель создаваемой доски
     * @return результат операции создания
     */
    @PostMapping
    @Operation(summary = "Создать доску в проекте")
    public BoardOperationResultTo createBoard(final @Valid @RequestBody BoardTo board) {
        return boardCreateUseCase.createBoard(board);
    }
}
