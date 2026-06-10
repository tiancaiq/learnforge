package com.learnforge.search.repository.impl;

import com.learnforge.search.domain.po.Course;
import com.learnforge.search.repository.CourseRepository;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.JsonUtils;
import com.learnforge.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.learnforge.search.constants.SearchErrorInfo.*;

@Slf4j
@Component
public class CourseRepositoryImpl implements CourseRepository {
  
    private final RestHighLevelClient restHighLevelClient;

    public CourseRepositoryImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public void save(Course course) {
        IndexRequest request = new IndexRequest(INDEX_NAME)
                .id(course.getId().toString())
                .source(JsonUtils.toJsonStr(course), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new CommonException(SAVE_COURSE_ERROR, e);
        }
    }

    @Override
    public void deleteById(Long courseId) {
        try {
            restHighLevelClient.delete(new DeleteRequest(INDEX_NAME, courseId.toString()), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new CommonException(SAVE_COURSE_ERROR, e);
        }
    }

    @Override
    public Optional<Course> findById(Long courseId) {
        GetResponse response = null;
        try {
            response = restHighLevelClient.get(new GetRequest(INDEX_NAME, courseId.toString()), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new CommonException(QUERY_COURSE_ERROR, e);
        }
        String source = response.getSourceAsString();
        if (StringUtils.isBlank(source)) {
            return Optional.empty();
        }
        return Optional.of(JsonUtils.toBean(source, Course.class));
    }

    @Override
    public void updateById(Long courseId, Object... sources) {
        // 1. Create Request
        UpdateRequest request = new UpdateRequest(INDEX_NAME, courseId.toString());
        // 2. Update fields
        request.doc(sources);
        // 3. Send Request
        try {
            restHighLevelClient.update(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new CommonException(UPDATE_COURSE_STATUS_ERROR, e);
        }
    }

    @Override
    public void increment(Long courseId, String field, int amount) {
        // 1. Create Request
        UpdateRequest request = new UpdateRequest(INDEX_NAME, courseId.toString());
        // 2. Update fields
        String code = "ctx._source." + field + " += params.count";
        Map<String, Object> params = new HashMap<>();
        params.put("count", amount);
        request.script(new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, code, params));
        // 3. Send Request
        try {
            restHighLevelClient.update(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new CommonException(UPDATE_COURSE_STATUS_ERROR, e);
        }
    }

    @Override
    public void incrementSold(List<Long> courseIds, int amount) {
        // 1. Bulk request
        BulkRequest bulkRequest = new BulkRequest(INDEX_NAME);

        for (Long courseId : courseIds) {
            // 2. Create Request
            UpdateRequest request = new UpdateRequest(INDEX_NAME, courseId.toString());
            // 3. Update fields
            Map<String, Object> params = new HashMap<>();
            params.put(INCREMENT_SOLD_SCRIPT_PARAM, amount);
            request.script(new Script(ScriptType.STORED, null, INCREMENT_SOLD_SCRIPT_ID, params));
            bulkRequest.add(request);
        }

        // 4. Send request
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new CommonException(UPDATE_COURSE_STATUS_ERROR, e);
        }
    }

    @Override
    public void saveAll(List<Course> list) {
        // 1. Create BulkRequest
        BulkRequest request = new BulkRequest(INDEX_NAME);
        // 2. Add parameters
        for (Course course : list) {
            request.add(new IndexRequest(INDEX_NAME)
                    .id(course.getId().toString())
                    .source(JsonUtils.toJsonStr(course), XContentType.JSON));
        }
        // 3. Batch processing
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            for (BulkItemResponse itemResponse : bulkResponse.getItems()) {
                if (itemResponse.status().compareTo(RestStatus.BAD_REQUEST) >= 0) {
                    log.error("Batch processing failed, id: {}, reason: {}", itemResponse.getId(), itemResponse.getFailureMessage());
                }
            }
        } catch (IOException e) {
            throw new CommonException(SAVE_COURSE_ERROR, e);
        }
    }

    @Override
    public void deleteByIds(List<Long> courseIds) {
        // 1. Create BulkRequest
        BulkRequest request = new BulkRequest(INDEX_NAME);
        // 2. Add parameters
        for (Long courseId : courseIds) {
            request.add(new DeleteRequest(INDEX_NAME, courseId.toString()));
        }
        // 3. Batch processing
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            for (BulkItemResponse itemResponse : bulkResponse.getItems()) {
                if (itemResponse.status().compareTo(RestStatus.BAD_REQUEST) >= 0) {
                    log.error("Batch processing failed, id: {}, reason: {}", itemResponse.getId(), itemResponse.getFailureMessage());
                }
            }
        } catch (IOException e) {
            throw new CommonException(SAVE_COURSE_ERROR, e);
        }
    }
}
