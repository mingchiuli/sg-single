package com.chiu.sgsingle.service.impl;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.chiu.sgsingle.document.BlogDocument;
import com.chiu.sgsingle.lang.Const;
import com.chiu.sgsingle.page.PageAdapter;
import com.chiu.sgsingle.service.BlogSearchService;
import com.chiu.sgsingle.vo.BlogDocumentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mingchiuli
 * @create 2022-11-30 9:00 pm
 */
@Service
public class BlogSearchServiceImpl implements BlogSearchService {

    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public void setElasticsearchTemplate(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public PageAdapter<BlogDocumentVo> selectBlogsByES(Integer currentPage, String keyword, Integer flag, Integer year) {

        HighlightParameters highlightParameters;
        HighlightParameters.HighlightParametersBuilder highlightParametersBuilder = new HighlightParameters.HighlightParametersBuilder().
                withPreTags("<b style='color:red'>").
                withPostTags("</b>");
        if (flag == 0) {
            highlightParametersBuilder
                    .withNumberOfFragments(1)
                    .withFragmentSize(5);
        }

        highlightParameters = highlightParametersBuilder.build();

        List<HighlightField> fields = Arrays.asList(
                new HighlightField("title"),
                new HighlightField("description"),
                new HighlightField("content"));

        Highlight highlight = new Highlight(highlightParameters, fields);

        NativeQuery matchQuery = NativeQuery.builder().
                withQuery(query -> query.
                        bool(boolQuery -> boolQuery.
                                must(mustQuery1 -> mustQuery1.
                                        multiMatch(multiQuery -> multiQuery.
                                                fields(Arrays.asList("title", "description", "content")).query(keyword))).
                                must(mustQuery2 -> mustQuery2.
                                        term(termQuery -> termQuery.
                                                field("status").value(0))).
                                must(mustQuery3 -> mustQuery3.
                                        range(rangeQuery -> rangeQuery.
                                                field("created").
                                                from(year == null ? null : year + "-01-01T00:00:00.000").
                                                to(year == null ? null : year + "-12-31T23:59:59.999")
                                        ))))
                .withSort(sort -> sort.
                        score(score -> score.
                                order(SortOrder.Desc)))
                .withPageable(PageRequest.of(currentPage - 1, Const.PAGE_SIZE))
                .withHighlightQuery(new HighlightQuery(highlight, null))
                .build();

        SearchHits<BlogDocument> search = elasticsearchTemplate.search(matchQuery, BlogDocument.class);

        ArrayList<BlogDocumentVo> vos = new ArrayList<>();

        search.getSearchHits().forEach(hit -> {
            BlogDocumentVo vo = new BlogDocumentVo();
            BeanUtils.copyProperties(hit.getContent(), vo);
            vo.setScore(hit.getScore());
            vo.setHighlight(hit.getHighlightFields().values().toString());
            vos.add(vo);
        });


        return PageAdapter.
                <BlogDocumentVo>builder().
                first(currentPage == 1).
                last(currentPage == (search.getTotalHits() % Const.PAGE_SIZE == 0 ? search.getTotalHits() / Const.PAGE_SIZE : search.getTotalHits() / Const.PAGE_SIZE + 1)).
                pageSize(Const.PAGE_SIZE).
                pageNumber(currentPage).
                empty(search.isEmpty()).
                totalElements(search.getTotalHits()).
                totalPages((int) (search.getTotalHits() % Const.PAGE_SIZE == 0 ? search.getTotalHits() / Const.PAGE_SIZE : (search.getTotalHits() / Const.PAGE_SIZE + 1))).
                content(vos).
                build();
    }

}
