package heavenboards.board.service.board.service;

import heavenboards.board.service.board.domain.BoardRepository;
import heavenboards.board.service.board.mapping.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import transfer.contract.domain.board.BoardTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.UUID;

/**
 * Use case поиска досок.
 */
@Service
@RequiredArgsConstructor
public class BoardFindUseCase {
    /**
     * Репозиторий для досок.
     */
    private final BoardRepository boardRepository;

    /**
     * Маппер для досок.
     */
    private final BoardMapper boardMapper;

    /**
     * Поиск доски по идентификатору.
     *
     * @param boardId - идентификатор доски
     * @return данные доски
     */
    public BoardTo findBoardById(final UUID boardId) {
        return boardRepository.findById(boardId)
            .map(boardMapper::mapFromEntity)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Доска с идентификатором %s не найдена!", boardId)));
    }
}
