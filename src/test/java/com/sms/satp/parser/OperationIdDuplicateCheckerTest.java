package com.sms.satp.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sms.satp.entity.api.ApiEntity;
import com.sms.satp.entity.project.ProjectImportFlowEntity;
import com.sms.satp.parser.impl.OperationIdDuplicateChecker;
import com.sms.satp.repository.ProjectImportFlowRepository;
import com.sms.satp.websocket.Payload;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@DisplayName("Test for OperationIdDuplicateChecker")
public class OperationIdDuplicateCheckerTest {

    private final OperationIdDuplicateChecker operationIdDuplicateChecker = new OperationIdDuplicateChecker();
    private ApplicationContext applicationContext = mock(ApplicationContext.class);
    private ProjectImportFlowRepository projectImportFlowRepository = mock(ProjectImportFlowRepository.class);
    private final SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);

    @Test
    public void check_test() {
        ProjectImportFlowEntity projectImportFlowEntity = ProjectImportFlowEntity.builder().build();
        when(applicationContext.getBean(ProjectImportFlowRepository.class)).thenReturn(projectImportFlowRepository);
        when(applicationContext.getBean(SimpMessagingTemplate.class)).thenReturn(simpMessagingTemplate);
        doNothing().when(simpMessagingTemplate).convertAndSend(any(), any(Payload.class));
        when(projectImportFlowRepository.save(any(ProjectImportFlowEntity.class))).thenReturn(projectImportFlowEntity);
        boolean result = operationIdDuplicateChecker.check(getApi(), projectImportFlowEntity, applicationContext);
        assertThat(result).isFalse();
    }

    private List<ApiEntity> getApi() {
        ArrayList<ApiEntity> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(ApiEntity.builder().id(ObjectId.get().toString()).swaggerId("swagger")
                .apiName("test" + Math.random()).build());
        }
        return list;
    }
}
