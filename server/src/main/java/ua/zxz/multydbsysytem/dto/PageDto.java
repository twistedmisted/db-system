package ua.zxz.multydbsysytem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {

    private List<T> content;
    private int currentPage;
    private int totalPages;

    public PageDto(int currentPage, int totalPages) {
        this.content = new ArrayList<>();
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
}
