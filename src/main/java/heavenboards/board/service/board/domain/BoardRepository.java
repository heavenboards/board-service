package heavenboards.board.service.board.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для досок.
 */
public interface BoardRepository extends JpaRepository<BoardEntity, UUID> {
    /**
     * Найти доску по названию и идентификатору проекта.
     *
     * @param name      - название доски
     * @param projectId - идентификатор проекта
     * @return доска или пустота
     */
    Optional<BoardEntity> findByNameAndProjectId(String name, UUID projectId);
}
