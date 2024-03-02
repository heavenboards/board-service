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

    /**
     * Маппинг из entity в to.
     *
     * @param entity - сущность доски
     * @return to-модель доски с проставленными полями
     */
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "groups", ignore = true)
    public abstract BoardTo mapFromEntity(BoardEntity entity);
}
