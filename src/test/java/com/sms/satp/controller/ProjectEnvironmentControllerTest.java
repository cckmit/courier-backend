package com.sms.satp.controller;

import static com.sms.satp.utils.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.satp.common.constant.Constants;
import com.sms.satp.common.response.Response;
import com.sms.satp.entity.dto.PageDto;
import com.sms.satp.entity.dto.ProjectEnvironmentDto;
import com.sms.satp.service.ProjectEnvironmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(value = ProjectEnvironmentController.class)
@DisplayName("Tests for ProjectEnvironmentController")
class ProjectEnvironmentControllerTest {

    @MockBean
    private ProjectEnvironmentService projectEnvironmentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final static Integer PAGE_NUMBER = 3;
    private final static Integer PAGE_SIZE = 20;
    private final static String PROJECT_ID = "id";
    private final static String PROJECT_NAME = "name";

    @Test
    @DisplayName("Query the page data for the ProjectEnvironment by default query criteria")
    void getProjectEnvironmentPageByDefaultRequirements() throws Exception {
        PageDto pageDto = PageDto.builder().build();
        when(projectEnvironmentService.page(pageDto, PROJECT_ID)).thenReturn(null);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get(Constants.PROJECT_ENVIRONMENT_PATH + "/page/" + PROJECT_ID);
        ResultActions perform = mockMvc.perform(request);
        perform.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is(Response.ok().build().getCode())))
            .andExpect(jsonPath("$.message", is(Response.ok().build().getMessage())));
    }

    @Test
    @DisplayName("Query the page data for the ProjectEnvironment by specified query criteria")
    void getProjectEnvironmentPageBySpecifiedRequirements() throws Exception {
        PageDto pageDto = PageDto.builder()
            .pageNumber(PAGE_NUMBER)
            .pageSize(PAGE_SIZE)
            .build();
        when(projectEnvironmentService.page(pageDto, PROJECT_ID)).thenReturn(null);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .get(Constants.PROJECT_ENVIRONMENT_PATH + "/page/" + PROJECT_ID)
            .param("pageNumber", String.valueOf(PAGE_NUMBER))
            .param("pageSize", String.valueOf(PAGE_SIZE));
        ResultActions perform = mockMvc.perform(request);
        perform.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is(Response.ok().build().getCode())))
            .andExpect(jsonPath("$.message", is(Response.ok().build().getMessage())));
    }

    @Test
    @DisplayName("Add a ProjectEnvironment")
    void addProjectEnvironmentDto() throws Exception{
        ProjectEnvironmentDto projectEnvironmentDto = ProjectEnvironmentDto.builder()
            .name(PROJECT_NAME)
            .build();
        doNothing().when(projectEnvironmentService).add(projectEnvironmentDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .post(Constants.PROJECT_ENVIRONMENT_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(objectMapper, projectEnvironmentDto));
        ResultActions perform = mockMvc.perform(request);
        perform.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is(Response.ok().build().getCode())))
            .andExpect(jsonPath("$.message", is(Response.ok().build().getMessage())));
    }

    @Test
    @DisplayName("Edit the ProjectEnvironment by id")
    void editProjectEnvironmentDto() throws Exception{
        ProjectEnvironmentDto projectEnvironmentDto = ProjectEnvironmentDto.builder()
            .id(PROJECT_ID)
            .name(PROJECT_NAME)
            .build();
        doNothing().when(projectEnvironmentService).edit(projectEnvironmentDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .put(Constants.PROJECT_ENVIRONMENT_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(objectMapper, projectEnvironmentDto));
        ResultActions perform = mockMvc.perform(request);
        perform.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is(Response.ok().build().getCode())))
            .andExpect(jsonPath("$.message", is(Response.ok().build().getMessage())));
    }

    @Test
    @DisplayName("Delete the ProjectEnvironment by id")
    void deleteProjectEnvironmentDto() throws Exception{
        doNothing().when(projectEnvironmentService).deleteById(PROJECT_ID);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
            .delete(Constants.PROJECT_ENVIRONMENT_PATH + "/" + PROJECT_ID);
        ResultActions perform = mockMvc.perform(request);
        perform.andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code", is(Response.ok().build().getCode())))
            .andExpect(jsonPath("$.message", is(Response.ok().build().getMessage())));
    }
}