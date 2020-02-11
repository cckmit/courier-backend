package com.sms.satp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sms.satp.entity.ApiInterface;
import com.sms.satp.entity.ProjectEnvironment;
import com.sms.satp.entity.dto.ApiInterfaceDto;
import com.sms.satp.entity.dto.PageDto;
import com.sms.satp.entity.dto.ProjectEnvironmentDto;
import com.sms.satp.mapper.ProjectEnvironmentMapper;
import com.sms.satp.repository.ApiInterfaceRepository;
import com.sms.satp.repository.ProjectEnvironmentRepository;
import com.sms.satp.repository.ProjectRepository;
import com.sms.satp.repository.StatusCodeDocRepository;
import com.sms.satp.repository.WikiRepository;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@SpringBootTest(classes = ApplicationTests.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Test the service layer interface of the ProjectEnvironment")
public class ProjectEnvironmentServiceTest {

    @MockBean
    private ApiInterfaceRepository apiInterfaceRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private ProjectEnvironmentRepository projectEnvironmentRepository;

    @MockBean
    private StatusCodeDocRepository statusCodeDocRepository;

    @MockBean
    private WikiRepository wikiRepository;

    @SpyBean
    private ProjectEnvironmentService projectEnvironmentService;

    @SpyBean
    private ProjectEnvironmentMapper projectEnvironmentMapper;

    private final static int TOTAL_ELEMENTS = 60;
    private final static int PAGE_NUMBER = 2;
    private final static int PAGE_SIZE = 20;
    private final static int DEFAULT_PAGE_NUMBER = 0;
    private final static int DEFAULT_PAGE_SIZE = 10;
    private final static String PROJECT_ID = "25";
    private final static String NAME = "title";

    @Test
    @DisplayName("Test the paging method with no parameters in the ProjectEnvironment service")
    void page_default_test() {
        ProjectEnvironment projectEnvironment = ProjectEnvironment.builder()
            .projectId(PROJECT_ID)
            .build();
        Example<ProjectEnvironment> example = Example.of(projectEnvironment);
        PageDto pageDto = PageDto.builder().build();
        Sort sort = Sort.by(Direction.fromString(pageDto.getOrder()), pageDto.getSort());
        Pageable pageable = PageRequest.of(pageDto.getPageNumber(), pageDto.getPageSize(), sort);
        List<ProjectEnvironment> projectEnvironmentList = new ArrayList<>();
        for (int i = 0; i < TOTAL_ELEMENTS; i++) {
            projectEnvironmentList.add(ProjectEnvironment.builder().name(NAME).build());
        }
        Page<ProjectEnvironment> projectEnvironmentPage = new PageImpl<>(projectEnvironmentList, pageable, TOTAL_ELEMENTS);
        when(projectEnvironmentRepository.findAll(example, pageable)).thenReturn(projectEnvironmentPage);
        Page<ProjectEnvironmentDto> projectEnvironmentDtoPage = projectEnvironmentService.page(pageDto, PROJECT_ID);
        assertThat(projectEnvironmentDtoPage.getTotalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(projectEnvironmentDtoPage.getPageable().getPageNumber()).isEqualTo(DEFAULT_PAGE_NUMBER);
        assertThat(projectEnvironmentDtoPage.getPageable().getPageSize()).isEqualTo(DEFAULT_PAGE_SIZE);
        assertThat(projectEnvironmentDtoPage.getContent()).allMatch(projectDto -> StringUtils.equals(projectDto.getName(), NAME));
    }

    @Test
    @DisplayName("Test the paging method with specified parameters in the ProjectEnvironment service")
    void page_test() {
        ProjectEnvironment projectEnvironment = ProjectEnvironment.builder()
            .projectId(PROJECT_ID)
            .build();
        Example<ProjectEnvironment> example = Example.of(projectEnvironment);
        PageDto pageDto = PageDto.builder()
            .pageNumber(PAGE_NUMBER)
            .pageSize(PAGE_SIZE)
            .order("asc")
            .build();
        Sort sort = Sort.by(Direction.fromString(pageDto.getOrder()), pageDto.getSort());
        Pageable pageable = PageRequest.of(pageDto.getPageNumber(), pageDto.getPageSize(), sort);
        List<ProjectEnvironment> projectEnvironmentList = new ArrayList<>();
        for (int i = 0; i < TOTAL_ELEMENTS; i++) {
            projectEnvironmentList.add(ProjectEnvironment.builder().name(NAME).build());
        }
        Page<ProjectEnvironment> projectEnvironmentPage = new PageImpl<>(projectEnvironmentList, pageable, TOTAL_ELEMENTS);
        when(projectEnvironmentRepository.findAll(example, pageable)).thenReturn(projectEnvironmentPage);
        Page<ProjectEnvironmentDto> projectEnvironmentDtos = projectEnvironmentService.page(pageDto, PROJECT_ID);
        assertThat(projectEnvironmentDtos.getTotalElements()).isEqualTo(TOTAL_ELEMENTS);
        assertThat(projectEnvironmentDtos.getPageable().getPageNumber()).isEqualTo(PAGE_NUMBER);
        assertThat(projectEnvironmentDtos.getPageable().getPageSize()).isEqualTo(PAGE_SIZE);
        assertThat(projectEnvironmentDtos.getContent()).allMatch(projectDto -> StringUtils.equals(projectDto.getName(), NAME));
    }

    @Test
    @DisplayName("Test the add method in the ProjectEnvironment service")
    void add_test() {
        ProjectEnvironmentDto projectEnvironmentDto = ProjectEnvironmentDto.builder().build();
        ProjectEnvironment projectEnvironment = projectEnvironmentMapper.toEntity(projectEnvironmentDto);
        when(projectEnvironmentRepository.insert(projectEnvironment)).thenReturn(projectEnvironment);
        projectEnvironmentService.add(projectEnvironmentDto);
        verify(projectEnvironmentRepository, times(1)).insert(projectEnvironment);
    }

    @Test
    @DisplayName("Test the edit method in the ProjectEnvironment service")
    void edit_test() {
        ProjectEnvironmentDto projectEnvironmentDto = ProjectEnvironmentDto.builder().build();
        ProjectEnvironment projectEnvironment = projectEnvironmentMapper.toEntity(projectEnvironmentDto);
        when(projectEnvironmentRepository.save(projectEnvironment)).thenReturn(projectEnvironment);
        projectEnvironmentService.edit(projectEnvironmentDto);
        verify(projectEnvironmentRepository, times(1)).save(projectEnvironment);
    }

    @Test
    @DisplayName("Test the delete method in the ProjectEnvironment service")
    void delete_test() {
        doNothing().when(projectEnvironmentRepository).deleteById(PROJECT_ID);
        projectEnvironmentService.deleteById(PROJECT_ID);
        verify(projectEnvironmentRepository, times(1)).deleteById(PROJECT_ID);
    }
}