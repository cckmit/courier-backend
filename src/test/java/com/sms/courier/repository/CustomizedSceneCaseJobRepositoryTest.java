package com.sms.courier.repository;

import com.sms.courier.dto.request.SceneCaseJobRequest;
import com.sms.courier.entity.job.SceneCaseJobEntity;
import com.sms.courier.repository.impl.CustomizedSceneCaseJobRepositoryImpl;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Tests for CustomizedSceneCaseJobRepositoryTest")
class CustomizedSceneCaseJobRepositoryTest {

    private final MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    private final CustomizedSceneCaseJobRepository customizedSceneCaseJobRepository =
        new CustomizedSceneCaseJobRepositoryImpl(mongoTemplate);

    private final static String MOCK_ID = "1";
    private final static Long COUNT = 1L;

    @Test
    @DisplayName("Test the page method in the CustomizedSceneCaseJobRepository")
    void page_test() {
        List<SceneCaseJobEntity> sceneCaseJobList = Lists.newArrayList(SceneCaseJobEntity.builder().id(MOCK_ID).build());
        when(mongoTemplate.find(any(Query.class), eq(SceneCaseJobEntity.class))).thenReturn(sceneCaseJobList);
        when(mongoTemplate.count(any(Query.class), eq(SceneCaseJobEntity.class))).thenReturn(COUNT);
        SceneCaseJobRequest request =
            SceneCaseJobRequest.builder().sceneCaseId(MOCK_ID).userIds(Lists.newArrayList(MOCK_ID)).build();
        Page<SceneCaseJobEntity> page = customizedSceneCaseJobRepository.page(request);
        assertThat(page).isNotNull();
    }

}