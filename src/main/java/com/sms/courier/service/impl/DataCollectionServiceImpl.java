package com.sms.courier.service.impl;

import static com.sms.courier.common.enums.DataCollection.COLLECTION_NAME;
import static com.sms.courier.common.enums.DataCollection.DATA_NAME;
import static com.sms.courier.common.enums.OperationModule.DATA_COLLECTION;
import static com.sms.courier.common.enums.OperationType.ADD;
import static com.sms.courier.common.enums.OperationType.DELETE;
import static com.sms.courier.common.enums.OperationType.EDIT;
import static com.sms.courier.common.exception.ErrorCode.ADD_DATA_COLLECTION_ERROR;
import static com.sms.courier.common.exception.ErrorCode.DATA_COLLECTION_NOTEXITS_ERROR;
import static com.sms.courier.common.exception.ErrorCode.DELETE_DATA_COLLECTION_BY_ID_ERROR;
import static com.sms.courier.common.exception.ErrorCode.EDIT_DATA_COLLECTION_ERROR;
import static com.sms.courier.common.exception.ErrorCode.EDIT_NOT_EXIST_ERROR;
import static com.sms.courier.common.exception.ErrorCode.EXPORT_DATA_COLLECTION_ERROR;
import static com.sms.courier.common.exception.ErrorCode.GET_DATA_COLLECTION_BY_ID_ERROR;
import static com.sms.courier.common.exception.ErrorCode.GET_DATA_COLLECTION_LIST_ERROR;
import static com.sms.courier.common.exception.ErrorCode.GET_DATA_COLLECTION_PARAM_LIST_BY_ID_ERROR;
import static com.sms.courier.common.exception.ErrorCode.IMPORT_DATA_COLLECTION_ERROR;
import static com.sms.courier.common.field.CommonField.CREATE_DATE_TIME;
import static com.sms.courier.common.field.CommonField.GROUP_ID;
import static com.sms.courier.common.field.CommonField.PROJECT_ID;
import static com.sms.courier.common.field.CommonField.REMOVE;
import static com.sms.courier.utils.Assert.isTrue;
import static com.sms.courier.utils.Assert.notEmpty;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.google.common.collect.Lists;
import com.sms.courier.common.aspect.annotation.Enhance;
import com.sms.courier.common.aspect.annotation.LogRecord;
import com.sms.courier.common.enums.ImportMode;
import com.sms.courier.common.exception.ApiTestPlatformException;
import com.sms.courier.dto.request.DataCollectionImportRequest;
import com.sms.courier.dto.request.DataCollectionRequest;
import com.sms.courier.dto.response.DataCollectionResponse;
import com.sms.courier.entity.datacollection.DataCollectionEntity;
import com.sms.courier.entity.datacollection.DataParam;
import com.sms.courier.entity.datacollection.TestData;
import com.sms.courier.entity.env.ProjectEnvironmentEntity;
import com.sms.courier.mapper.DataCollectionMapper;
import com.sms.courier.repository.CustomizedDataCollectionRepository;
import com.sms.courier.repository.DataCollectionRepository;
import com.sms.courier.service.DataCollectionService;
import com.sms.courier.service.ProjectEnvironmentService;
import com.sms.courier.utils.ExceptionUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataCollectionServiceImpl implements DataCollectionService {

    private static final String DELIMITER = ",";
    private final DataCollectionRepository dataCollectionRepository;
    private final DataCollectionMapper dataCollectionMapper;
    private final CustomizedDataCollectionRepository customizedDataCollectionRepository;
    private final ProjectEnvironmentService projectEnvironmentService;

    public DataCollectionServiceImpl(DataCollectionRepository dataCollectionRepository,
        DataCollectionMapper dataCollectionMapper,
        CustomizedDataCollectionRepository customizedDataCollectionRepository,
        ProjectEnvironmentService projectEnvironmentService) {
        this.dataCollectionRepository = dataCollectionRepository;
        this.dataCollectionMapper = dataCollectionMapper;
        this.customizedDataCollectionRepository = customizedDataCollectionRepository;
        this.projectEnvironmentService = projectEnvironmentService;
    }

    @Override
    public DataCollectionResponse findById(String id) {
        return dataCollectionRepository.findById(id).map(dataCollectionMapper::toDto)
            .orElseThrow(() -> ExceptionUtils.mpe(GET_DATA_COLLECTION_BY_ID_ERROR));
    }

    @Override
    public DataCollectionEntity findOne(String id) {
        Optional<DataCollectionEntity> optional = dataCollectionRepository.findById(id);
        if (optional.isPresent() && !optional.get().isRemoved()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public List<DataCollectionResponse> list(String projectId, String collectionName, String groupId) {
        try {
            Sort sort = Sort.by(Direction.DESC, CREATE_DATE_TIME.getName());
            DataCollectionEntity dataCollection = DataCollectionEntity.builder().projectId(projectId)
                .collectionName(collectionName)
                .groupId(groupId)
                .build();
            ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher(PROJECT_ID.getName(), GenericPropertyMatchers.exact())
                .withMatcher(GROUP_ID.getName(), GenericPropertyMatchers.exact())
                .withMatcher(REMOVE.getName(), GenericPropertyMatchers.exact())
                .withStringMatcher(StringMatcher.CONTAINING);
            Example<DataCollectionEntity> example = Example.of(dataCollection, exampleMatcher);
            List<DataCollectionResponse> responseList = dataCollectionMapper
                .toDtoList(dataCollectionRepository.findAll(example, sort));
            for (DataCollectionResponse response : responseList) {
                if (Objects.nonNull(response.getEnvId())) {
                    ProjectEnvironmentEntity entity = projectEnvironmentService.findOne(response.getEnvId());
                    response.setEnvName(Objects.nonNull(entity) ? entity.getEnvName() : null);
                }
            }
            return responseList;
        } catch (Exception e) {
            log.error("Failed to get the DataCollection list!", e);
            throw new ApiTestPlatformException(GET_DATA_COLLECTION_LIST_ERROR);
        }
    }

    @Override
    @LogRecord(operationType = ADD, operationModule = DATA_COLLECTION,
        template = "{{#dataCollectionRequest.collectionName}}")
    public Boolean add(DataCollectionRequest dataCollectionRequest) {
        log.info("DataCollectionService-add()-params: [DataCollection]={}", dataCollectionRequest.toString());
        try {
            DataCollectionEntity dataCollection = dataCollectionMapper.toEntity(dataCollectionRequest);
            dataCollectionRepository.insert(dataCollection);
        } catch (Exception e) {
            log.error("Failed to add the DataCollection!", e);
            throw new ApiTestPlatformException(ADD_DATA_COLLECTION_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    @LogRecord(operationType = EDIT, operationModule = DATA_COLLECTION,
        template = "{{#dataCollectionRequest.collectionName}}")
    public Boolean edit(DataCollectionRequest dataCollectionRequest) {
        log.info("DataCollectionService-edit()-params: [DataCollection]={}", dataCollectionRequest.toString());
        try {
            boolean exists = dataCollectionRepository.existsById(dataCollectionRequest.getId());
            isTrue(exists, EDIT_NOT_EXIST_ERROR, "DataCollection", dataCollectionRequest.getId());
            DataCollectionEntity dataCollection = dataCollectionMapper.toEntity(dataCollectionRequest);
            dataCollectionRepository.save(dataCollection);
        } catch (ApiTestPlatformException courierException) {
            log.error(courierException.getMessage());
            throw courierException;
        } catch (Exception e) {
            log.error("Failed to add the DataCollection!", e);
            throw new ApiTestPlatformException(EDIT_DATA_COLLECTION_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    @LogRecord(operationType = DELETE, operationModule = DATA_COLLECTION,
        template = "{{#result?.![#this.collectionName]}}",
        enhance = @Enhance(enable = true, primaryKey = "ids"))
    public Boolean delete(List<String> ids) {
        try {
            return customizedDataCollectionRepository.deleteByIds(ids);
        } catch (Exception e) {
            log.error("Failed to delete the DataCollection!", e);
            throw new ApiTestPlatformException(DELETE_DATA_COLLECTION_BY_ID_ERROR);
        }
    }

    @Override
    public List<String> getParamListById(String id) {
        try {
            return customizedDataCollectionRepository.getParamListById(id);
        } catch (Exception e) {
            log.error("Failed to get the DataCollectionParamList by Id!", e);
            throw new ApiTestPlatformException(GET_DATA_COLLECTION_PARAM_LIST_BY_ID_ERROR);
        }
    }

    @Override
    public Boolean importDataCollection(DataCollectionImportRequest request) {
        try {
            Optional<DataCollectionEntity> optional = dataCollectionRepository.findById(request.getId());
            DataCollectionEntity dataCollection = optional
                .orElseThrow(() -> ExceptionUtils.mpe("The dataCollection not exits. id = %s", request.getId()));
            List<String> paramList = Objects.requireNonNullElse(dataCollection.getParamList(), new ArrayList<>());
            List<TestData> dataList = Objects.requireNonNullElse(dataCollection.getDataList(), new ArrayList<>());
            ImportMode importMode = ImportMode.getType(request.getImportMode());
            InputStream inputStream = request.getFile().getInputStream();
            List<String> sourceList = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            notEmpty(sourceList, "The file is empty.");
            if (importMode == ImportMode.COVER) {
                paramList.clear();
                dataList.clear();
            }
            String[] keys = sourceList.get(0).split(DELIMITER);
            for (int i = 1; i < keys.length; i++) {
                if (!paramList.contains(keys[i])) {
                    paramList.add(keys[i]);
                }
            }
            for (int i = 1; i < sourceList.size(); i++) {
                String[] values = sourceList.get(i).split(DELIMITER);
                List<DataParam> dataParams = new ArrayList<>();
                for (int j = 1; j < values.length; j++) {
                    dataParams.add(DataParam.builder().key(keys[j]).value(values[j]).build());
                }
                TestData testData = TestData.builder().dataName(values[0]).data(dataParams).build();
                dataList.add(testData);
            }
            dataCollectionRepository.save(dataCollection);
            return Boolean.TRUE;
        } catch (ApiTestPlatformException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to import the DataCollection!", e);
            throw new ApiTestPlatformException(IMPORT_DATA_COLLECTION_ERROR);
        }
    }

    @Override
    public List<DataCollectionResponse> listByEnvIdAndEnvIdIsNull(String envId, String projectId) {
        try {
            Sort sort = Sort.by(Direction.DESC, CREATE_DATE_TIME.getName());
            DataCollectionEntity dataCollection = DataCollectionEntity.builder().envId(envId).projectId(projectId)
                .build();
            ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher(REMOVE.getName(), GenericPropertyMatchers.exact())
                .withMatcher(PROJECT_ID.getName(), GenericPropertyMatchers.exact())
                .withStringMatcher(StringMatcher.CONTAINING);
            Example<DataCollectionEntity> example = Example.of(dataCollection, exampleMatcher);
            List<DataCollectionEntity> dataCollectionEntityList = dataCollectionRepository.findAll(example, sort);
            return dataCollectionMapper.toDtoList(dataCollectionEntityList);
        } catch (Exception e) {
            log.error("Failed to get the DataCollection list!", e);
            throw new ApiTestPlatformException(GET_DATA_COLLECTION_LIST_ERROR);
        }
    }

    @Override
    public void exportDataCollection(OutputStream outputStream, String id) {
        try {
            Optional<DataCollectionEntity> optional = dataCollectionRepository.findById(id);
            DataCollectionEntity dataCollection = optional
                    .orElseThrow(() -> ExceptionUtils.mpe(DATA_COLLECTION_NOTEXITS_ERROR, id));
            List<ExcelExportEntity> list = Lists.newArrayList();
            ExcelExportEntity entity1 = new ExcelExportEntity(COLLECTION_NAME.getName(), DATA_NAME);
            list.add(entity1);
            for (String string : dataCollection.getParamList()) {
                ExcelExportEntity colEntity = new ExcelExportEntity(string, string);
                list.add(colEntity);
            }
            List<Map<String, Object>> datList = Lists.newArrayList();
            for (TestData testData : dataCollection.getDataList()) {
                Map<String, Object> dataMap = testData.getData().stream().collect(Collectors.toMap(DataParam::getKey,
                        DataParam::getValue));
                dataMap.put(DATA_NAME.getName(), testData.getDataName());
                datList.add(dataMap);
            }
            Workbook workbook = ExcelExportUtil.exportExcel(
                    new ExportParams(null, dataCollection.getCollectionName()), list, datList);
            workbook.write(outputStream);
            workbook.close();
        } catch (Exception e) {
            log.error("Failed to export the DataCollection!", e);
            throw new ApiTestPlatformException(EXPORT_DATA_COLLECTION_ERROR);
        }
    }
}