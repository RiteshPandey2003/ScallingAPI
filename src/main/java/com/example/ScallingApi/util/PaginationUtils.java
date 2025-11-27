package com.example.ScallingApi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationUtils {

    public static Pageable getPageable(PaginationRequest request) {
        int pageIndex = request.getPage() <= 0 ? 0 : request.getPage() - 1;
        return PageRequest.of(pageIndex, request.getSize(), request.getDirection(), request.getSortField());
    }

}

