package com.taskmanager.dto;

import java.util.List;

/**
 * Generic paginated response wrapper.
 *
 * @param <T> the type of content items
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
