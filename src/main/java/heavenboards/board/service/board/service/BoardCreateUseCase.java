package heavenboards.board.service.board.service;

import heavenboards.board.service.board.domain.BoardEntity;
import heavenboards.board.service.board.domain.BoardRepository;
import heavenboards.board.service.board.mapping.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transfer.contract.api.ProjectApi;
import transfer.contract.domain.board.BoardOperationErrorCode;
import transfer.contract.domain.board.BoardOperationResultTo;
import transfer.contract.domain.board.BoardTo;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.project.ProjectTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case создания доски.
 */
@Service
@RequiredArgsConstructor
public class BoardCreateUseCase {
    /**
     * Маппер для досок.
     */
    private final BoardMapper boardMapper;

    /**
     * Репозиторий для досок.
     */
    private final BoardRepository boardRepository;

    /**
     * Api-клиент для сервиса проектов.
     */
    private final ProjectApi projectApi;

    /**
     * Создание доски.
     *
     * @param board - to-модель создаваемой доски
     * @return результат операции создания
     */
    public BoardOperationResultTo createBoard(final BoardTo board) {
        Optional<BoardEntity> boardByNameAndProjectId = boardRepository
            .findByNameAndProjectId(board.getName(), board.getProject().getId());
        if (boardByNameAndProjectId.isPresent()) {
            return BoardOperationResultTo.builder()
                .status(OperationStatus.FAILED)
                .errors(List.of(BoardOperationResultTo.BoardOperationErrorTo.builder()
                    .failedBoardId(boardByNameAndProjectId.get().getId())
                    .errorCode(BoardOperationErrorCode.BOARD_NAME_ALREADY_EXIST_IN_PROJECT)
                    .build()))
                .build();
        }

        checkProjectExist(board.getProject().getId());

        BoardEntity boardEntity = boardRepository.save(boardMapper.mapFromTo(board));
        return BoardOperationResultTo.builder()
            .boardId(boardEntity.getId())
            .build();
    }

    /**
     * Проверка существования проекта по идентификатору.
     *
     * @param projectId - идентификатор проверяемого проекта
     */
    private void checkProjectExist(UUID projectId) {
        try {
            ProjectTo project = projectApi.findProjectById(projectId);
            if (!Objects.equals(project.getId(), projectId)) {
                throw new RuntimeException();
            }
        } catch (Exception ignored) {
            throw new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Проект с идентификатором %s не найден", projectId));
        }
    }
}
