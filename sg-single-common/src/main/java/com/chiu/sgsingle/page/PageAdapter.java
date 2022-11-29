package com.chiu.sgsingle.page;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-11-29 12:58 am
 */
@Data
@NoArgsConstructor
public class PageAdapter<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3720998571176536865L;
    private List<T> content;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private boolean first;
    private boolean last;
    private boolean empty;
    private int totalPages;

    //只用把原来的page类放进来即可
    public PageAdapter(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getPageable().getPageNumber() + 1;
        this.pageSize = page.getPageable().getPageSize();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
        this.totalPages = page.getTotalPages();
    }

}
