package com.learnforge.search.service.impl;

import com.learnforge.api.cache.CategoryCache;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.*;
import com.learnforge.search.config.InterestsProperties;
import com.learnforge.search.constants.SearchErrorInfo;
import com.learnforge.search.domain.po.Course;
import com.learnforge.search.domain.query.CoursePageQuery;
import com.learnforge.search.domain.vo.CourseVO;
import com.learnforge.search.repository.CourseRepository;
import com.learnforge.search.service.IInterestsService;
import com.learnforge.search.service.ISearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.learnforge.search.repository.CourseRepository.PUBLISH_TIME;

@Service
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private RestHighLevelClient restClient;

    @Autowired
    private IInterestsService interestsService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private CategoryCache categoryCache;

    @Autowired
    private InterestsProperties interestsProperties;

    @Override
    public List<CourseVO> queryCourseByCateId(Long cateLv2Id) {
        return queryTopNByCategoryIdLv2sAndFree(
                CollUtils.singletonList(cateLv2Id), null, PUBLISH_TIME, false, 10);
    }

    @Override
    public List<CourseVO> queryBestTopN() {
        // 1. Get current user
        return queryTopNCourseOnMarketByFree(false, CourseRepository.SOLD);
    }

    @Override
    public List<CourseVO> queryNewTopN() {
        return queryTopNCourseOnMarketByFree(false, PUBLISH_TIME);
    }

    @Override
    public List<CourseVO> queryFreeTopN() {
        return queryTopNCourseOnMarketByFree(true, CourseRepository.SOLD);
    }

    private List<CourseVO> queryTopNCourseOnMarketByFree(boolean isFree, String sortBy) {
        // 1. Get current user
        Long id = UserContext.getUser();
        // 2. Query course
        List<CourseVO> courses = null;
        if (id == null) {
            // 3. Not logged in, directly query the most enrolled courses
            courses = queryTopNByCategoryIdLv2sAndFree(
                    null, isFree, sortBy, false, interestsProperties.getTopNumber());
        } else {
            // 4. Logged in, query based on interest
            List<Long> categoryIds = interestsService.queryMyInterestsIds();
            if (CollUtils.isEmpty(categoryIds)) {
                // 4.1. No interest, directly query the most enrolled courses
                courses = queryTopNByCategoryIdLv2sAndFree(
                        null, isFree, sortBy, false, interestsProperties.getTopNumber());
            } else {
                // 4.2. With interest, query the most enrolled courses in the interest
                courses = queryTopNByCategoryIdLv2sAndFree(
                        categoryIds, isFree, sortBy, false, interestsProperties.getTopNumber());
            }
        }
        return courses;
    }

    private List<CourseVO> queryTopNByCategoryIdLv2sAndFree(
            List<Long> categoryIds, Boolean isFree, String sortBy, boolean isASC, int n) {
        // 1. Prepare Request
        SearchRequest request = new SearchRequest(CourseRepository.INDEX_NAME);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 1.1. Is it free
        if(isFree != null) {
            queryBuilder.filter(QueryBuilders.termQuery(CourseRepository.FREE, isFree));
        }
        // 1.2. Category id
        if (categoryIds != null) {
            if (categoryIds.size() == 1) {
                queryBuilder.filter(QueryBuilders.termQuery(CourseRepository.CATEGORY_ID_LV2, categoryIds.get(0)));
            } else {
                queryBuilder.filter(QueryBuilders.termsQuery(CourseRepository.CATEGORY_ID_LV2, categoryIds));
            }
        }
        if(isFree != null || categoryIds != null) {
            request.source().query(queryBuilder);
        }
        // 1.3.TopN
        request.source().size(n).sort(sortBy, isASC ? SortOrder.ASC : SortOrder.DESC);
        // 2. Send request
        SearchResponse response = null;
        try {
            response = restClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new CommonException(SearchErrorInfo.QUERY_COURSE_ERROR, e);
        }
        // 3. Parse
        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        if (hits == null || hits.length == 0) {
            return CollUtils.emptyList();
        }
        List<CourseVO> courses = new ArrayList<>(hits.length);
        Set<Long> teacherIds = new HashSet<>(hits.length);
        for (SearchHit hit : hits) {
            // 3.1. Data conversion
            CourseVO vo = JsonUtils.toBean(hit.getSourceAsString(), CourseVO.class);
            // 3.2. Get category id
            teacherIds.add(Long.valueOf(vo.getTeacher()));
            // 3.3. Save
            courses.add(vo);
        }
        teacherIds.remove(0L);
        if (teacherIds.size() == 0) {
            return courses;
        }
        // 4. Query teacher
        List<UserDTO> teachers = userClient.queryUserByIds(teacherIds);
        AssertUtils.isNotEmpty(teachers, SearchErrorInfo.TEACHER_NOT_EXISTS);
        Map<String, String> tMap = teachers.stream()
                .collect(Collectors.toMap(t -> t.getId().toString(), UserDTO::getName));
        for (CourseVO c : courses) {
            c.setTeacher(tMap.getOrDefault(c.getTeacher(), "Anonymous"));
        }
        return courses;
    }

    @Override
    public PageDTO<CourseVO> queryCoursesForPortal(CoursePageQuery query) {
        // 1. Search data
        SearchResponse response = searchForResponse(query, CourseVO.EXCLUDE_FIELDS);
        // 2. Parse Response
        PageDTO<Course> result = handleSearchResponse(response, query.getPageSize());
        // 3. Process VO
        List<Course> list = result.getList();
        if (CollUtils.isEmpty(list)) {
            return PageDTO.empty(result.getTotal(), result.getPages());
        }
        // 3.1. Query teacher information
        List<Long> teacherIds = list.stream().map(Course::getTeacher).collect(Collectors.toList());
        List<UserDTO> teachers = userClient.queryUserByIds(teacherIds);
        AssertUtils.isNotEmpty(teachers, SearchErrorInfo.TEACHER_NOT_EXISTS);
        Map<Long, String> teacherMap = teachers.stream()
                .collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        // 3.2. Convert VO
        List<CourseVO> vos = new ArrayList<>(list.size());
        for (Course c : list) {
            CourseVO vo = BeanUtils.toBean(c, CourseVO.class);
            vo.setTeacher(teacherMap.getOrDefault(c.getTeacher(), "Unknown"));
            vos.add(vo);
        }
        return new PageDTO<>(result.getTotal(), result.getPages(), vos);
    }

    @Override
    public List<Long> queryCoursesIdByName(String keyword) {
        // 1. Create Request
        SearchRequest request = new SearchRequest(CourseRepository.INDEX_NAME);
        // 2. Build DSL
        request.source()
                .query(QueryBuilders.matchPhraseQuery(CourseRepository.DEFAULT_QUERY_NAME, keyword))
                .fetchSource(new String[]{"id"}, null);
        // 3. Query
        SearchResponse response;
        try {
            response = restClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new CommonException(SearchErrorInfo.QUERY_COURSE_ERROR, e);
        }
        // 4. Parse
        SearchHits searchHits = response.getHits();
        // 4.1. Get hits
        SearchHit[] hits = searchHits.getHits();
        if (hits.length == 0) {
            return CollUtils.emptyList();
        }
        // 4.2. Get id
        return Arrays.stream(hits)
                .map(SearchHit::getId)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }


    private SearchResponse searchForResponse(CoursePageQuery query, String[] excludeFields) {
        // 1. Create Request
        SearchRequest request = new SearchRequest(CourseRepository.INDEX_NAME);
        // 2. Build DSL
        // 2.1. Build query
        buildBasicQuery(request, query);
        // 2.2. Sort
        String sortBy = query.getSortBy();
        if (StringUtils.isNotBlank(sortBy)) {
            request.source().sort(sortBy, query.getIsAsc() ? SortOrder.ASC : SortOrder.DESC);
        }
        // 2.3. Pagination
        request.source().from(query.from()).size(query.getPageSize());
        // 2.4. Highlight
        request.source().highlighter(new HighlightBuilder().field(CourseRepository.DEFAULT_QUERY_NAME));
        // 2.5. Source processing
        request.source().fetchSource(null, excludeFields);
        // 3. Send Request
        SearchResponse response = null;
        try {
            response = restClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new CommonException(ErrorInfo.Msg.SERVER_INTER_ERROR, e);
        }
        return response;
    }

    private void buildBasicQuery(SearchRequest request, CoursePageQuery query) {
        // 1. Prepare bool query
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 2. Keyword search
        String keyword = query.getKeyword();
        if (StringUtils.isBlank(keyword)) {
            queryBuilder.must(QueryBuilders.matchAllQuery());
        } else {
            queryBuilder.must(QueryBuilders.matchPhraseQuery(CourseRepository.DEFAULT_QUERY_NAME, keyword));
        }
        // 3. Other conditions
        if (query.getCategoryIdLv1() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(CourseRepository.CATEGORY_ID_LV1, query.getCategoryIdLv1()));
        }
        if (query.getCategoryIdLv2() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(CourseRepository.CATEGORY_ID_LV2, query.getCategoryIdLv2()));
        }
        if (query.getCategoryIdLv3() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(CourseRepository.CATEGORY_ID_LV3, query.getCategoryIdLv3()));
        }
        if (query.getFree() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(CourseRepository.FREE, query.getFree()));
        }
        if (query.getType() != null) {
            queryBuilder.filter(QueryBuilders.termQuery(CourseRepository.TYPE, query.getType()));
        }
        LocalDateTime beginTime = query.getBeginTime();
        LocalDateTime endTime = query.getEndTime();
        if(beginTime != null || endTime != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(CourseRepository.UPDATE_TIME);
            if (beginTime != null) {
                rangeQuery.gte(beginTime);
            }
            if (endTime != null) {
                rangeQuery.lte(endTime);
            }
            queryBuilder.filter(rangeQuery);
        }
        // 4. Write to request
        request.source().query(queryBuilder);
    }

    private PageDTO<Course> handleSearchResponse(SearchResponse response, int pageSize) {
        SearchHits searchHits = response.getHits();
        // 1. Total count
        long total = searchHits.getTotalHits().value;
        // 2. Total pages
        long totalPages = (total + pageSize - 1) / pageSize;
        // 3. Get hit data
        SearchHit[] hits = searchHits.getHits();
        if (hits.length <= 0) {
            return new PageDTO<>(total, totalPages, CollUtils.emptyList());
        }
        // 4. Iterate
        List<Course> list = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            // 5. Get a specific source
            String jsonSource = hit.getSourceAsString();
            // 6. Deserialize
            Course course = JsonUtils.toBean(jsonSource, Course.class);
            // 7. Process highlight
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (CollUtils.isNotEmpty(highlightFields)) {
                // 7.1. Get highlight results
                HighlightField field = highlightFields.get(CourseRepository.DEFAULT_QUERY_NAME);
                Object[] fragments = field.getFragments();
                String value = StringUtils.join(fragments);
                // 7.2. Override non-highlight results
                course.setName(value);
            }
            list.add(course);
        }
        return new PageDTO<>(total, totalPages, list);
    }
}
