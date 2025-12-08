package com.example.ScallingApi.Mapper;

import java.util.List;

public class CursorResponse<T> {
    private List<T> data;
    private Long nextCursor;

    public CursorResponse(List<T> data, Long nextCursor) {
        this.data = data;
        this.nextCursor = nextCursor;
    }

    public List<T> getData() {
        return data;
    }

    public Long getNextCursor() {
        return nextCursor;
    }
}
