package com.chiu.sgsingle.page;

import lombok.Data;
import org.springframework.data.domain.Page;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-11-29 12:58 am
 */
@Data
public class PageAdapter<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3720998571176536865L;
    private List<T> content = new ArrayList<>();
    private long totalElements;
    private int number;
    private int pageSize;
    private boolean first;
    private boolean last;
    private boolean empty;
    private int totalPages;
    private int numberOfElements;

    public PageAdapter() {
    }
    //只用把原来的page类放进来即可
    public PageAdapter(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.number = page.getPageable().getPageNumber();
        this.pageSize = page.getPageable().getPageSize();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
        this.totalPages = page.getTotalPages();
        this.numberOfElements = page.getNumberOfElements();
    }

}
