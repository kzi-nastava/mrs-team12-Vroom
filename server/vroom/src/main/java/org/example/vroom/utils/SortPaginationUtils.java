package org.example.vroom.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortPaginationUtils {
    public Pageable getPageable(int pageNum, int pageSize, String sort) {
        Sort sortOrder = Sort.unsorted();

        if (sort != null && sort.contains(",")) {
            String[] split = sort.split(",");
            sortOrder = Sort.by(Sort.Direction.fromString(split[split.length - 1]), split[0]);
        }

        return PageRequest.of(pageNum, pageSize, sortOrder);
    }
}
