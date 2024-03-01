package heavenboards.board.service.board.mapping;

import heavenboards.board.service.board.domain.BoardEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import transfer.contract.domain.board.BoardTo;

/**
 * Маппер для досок.
 */
@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class BoardMapper {
    /**
     * Маппинг из to в entity.
     *
     * @param to - to-модель доски
     * @return entity с проставленными полями
     */
    @Mapping(target = "projectId", source = "project.id")
    public abstract BoardEntity mapFromTo(BoardTo to);
}
