package by.language.platform.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

/**
 * Универсальный DTO для постраничного ответа.
 *
 * @param <T> тип элементов страницы
 */
@Schema(description = "Постраничный ответ")
@JsonPropertyOrder({
        "content", "totalElements", "totalPages",
        "currentPage", "pageSize", "first", "last", "empty"
})
public final class PageDto<T> {

    @NotNull
    @Schema(description = "Содержимое страницы", requiredMode = Schema.RequiredMode.REQUIRED)
    private final List<T> content;

    @Min(0)
    @Schema(description = "Общее количество элементов", example = "100")
    private final long totalElements;

    @Min(0)
    @Schema(description = "Общее количество страниц", example = "10")
    private final int totalPages;

    @Min(0)
    @Schema(description = "Текущий индекс страницы (0-based)", example = "0")
    private final int currentPage;

    @Min(1)
    @Schema(description = "Размер страницы", example = "10")
    private final int pageSize;

    /* ---------- конструкторы ---------- */

    public PageDto(List<T> content,
                   long totalElements,
                   int totalPages,
                   int currentPage,
                   int pageSize) {
        this.content = List.copyOf(content);  // защита от внешнего изменения
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public static <T> PageDto<T> of(Page<T> page) {
        return new PageDto<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    /* ---------- геттеры ---------- */

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    /* ---------- удобные derived-флаги ---------- */

    @JsonProperty("first")
    public boolean isFirst() {
        return currentPage == 0;
    }

    @JsonProperty("last")
    public boolean isLast() {
        return currentPage + 1 >= totalPages;
    }

    @JsonProperty("empty")
    public boolean isEmpty() {
        return content.isEmpty();
    }

    /* ---------- equals / hashCode / toString ---------- */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageDto<?> pageDto)) return false;
        return totalElements == pageDto.totalElements &&
                totalPages == pageDto.totalPages &&
                currentPage == pageDto.currentPage &&
                pageSize == pageDto.pageSize &&
                Objects.equals(content, pageDto.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, totalElements, totalPages, currentPage, pageSize);
    }

    @Override
    public String toString() {
        return "PageDto{" +
                "content=" + content +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}
