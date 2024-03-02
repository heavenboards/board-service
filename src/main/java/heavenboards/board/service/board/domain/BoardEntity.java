package heavenboards.board.service.board.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;
import java.util.UUID;

/**
 * Сущность доски.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
@Entity
@Table(name = "board_entity")
public class BoardEntity {
    /**
     * Идентификатор.
     */
    @Id
    @UuidGenerator
    private UUID id;

    /**
     * Название.
     */
    private String name;

    /**
     * Идентификатор проекта, в котором находится доска.
     */
    private UUID projectId;

    /**
     * Вес позиции доски в проекте.
     * Нужен для определения порядка отображения досок в проекте на UI.
     */
    private Integer positionWeight;

    /**
     * Сравнение двух объектов через id.
     *
     * @param another - объект для сравнения
     * @return равны ли объекты
     */
    @Override
    public boolean equals(final Object another) {
        if (this == another) {
            return true;
        }

        if (another == null || getClass() != another.getClass()) {
            return false;
        }

        BoardEntity that = (BoardEntity) another;
        return Objects.equals(id, that.id);
    }

    /**
     * Хеш код идентификатора.
     *
     * @return хеш код идентификатора
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Строковое отображение объекта.
     *
     * @return строковое отображение объекта
     */
    @Override
    public String toString() {
        return "BoardEntity{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", projectId=" + projectId
            + ", positionWeight=" + positionWeight
            + '}';
    }
}
