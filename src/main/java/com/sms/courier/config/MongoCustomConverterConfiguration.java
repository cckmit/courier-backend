package com.sms.courier.config;

import com.sms.courier.common.enums.ApiBindingStatus;
import com.sms.courier.common.enums.ApiJsonType;
import com.sms.courier.common.enums.ApiProtocol;
import com.sms.courier.common.enums.ApiRequestParamType;
import com.sms.courier.common.enums.ApiStatus;
import com.sms.courier.common.enums.ApiTagType;
import com.sms.courier.common.enums.ApiType;
import com.sms.courier.common.enums.DocumentType;
import com.sms.courier.common.enums.DocumentUrlType;
import com.sms.courier.common.enums.EnumCommon;
import com.sms.courier.common.enums.GroupImportType;
import com.sms.courier.common.enums.ImportStatus;
import com.sms.courier.common.enums.JobStatus;
import com.sms.courier.common.enums.MatchType;
import com.sms.courier.common.enums.OperationModule;
import com.sms.courier.common.enums.OperationType;
import com.sms.courier.common.enums.ParamType;
import com.sms.courier.common.enums.ProjectType;
import com.sms.courier.common.enums.RawType;
import com.sms.courier.common.enums.RequestMethod;
import com.sms.courier.common.enums.ResponseParamsExtractionType;
import com.sms.courier.common.enums.ResultType;
import com.sms.courier.common.enums.ResultVerificationType;
import com.sms.courier.common.enums.RoleType;
import com.sms.courier.common.enums.SaveMode;
import com.sms.courier.common.enums.VerificationElementType;
import com.sms.courier.engine.enums.EngineStatus;
import com.sms.courier.security.pojo.CustomUser;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@Slf4j
public class MongoCustomConverterConfiguration {

    private static final String BUCKET = "TestFile";

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = List
            .of(EnumCommonToIntegerConverter.INSTANCE, IntegerToApiProtocolConverter.INSTANCE,
                IntegerToApiRequestParamTypeConverter.INSTANCE, IntegerToApiStatusConverter.INSTANCE,
                IntegerToParamTypeConverter.INSTANCE, IntegerToRequestMethodConverter.INSTANCE,
                IntegerToApiTagTypeConverter.INSTANCE, IntegerToApiJsonTypeConverter.INSTANCE,
                IntegerToSaveModeConverter.INSTANCE, IntegerToDocumentTypeConverter.INSTANCE,
                IntegerToGroupImportTypeConverter.INSTANCE, IntegerToOperationTypeConverter.INSTANCE,
                IntegerToOperationModuleConverter.INSTANCE, IntegerToGroupImportTypeConverter.INSTANCE,
                IntegerToApiTypeConverter.INSTANCE, IntegerToMatchTypeConverter.INSTANCE,
                IntegerToApiBindingStatusConverter.INSTANCE, IntegerToGroupImportTypeConverter.INSTANCE,
                IntegerToDocumentUrlTypeConverter.INSTANCE, IntegerToJobStatusConverter.INSTANCE,
                IntegerToProjectTypeConverter.INSTANCE, IntegerToImportStatusConverter.INSTANCE,
                IntegerToResultVerificationTypeConverter.INSTANCE,
                IntegerToResponseParamsExtractionTypeConverter.INSTANCE, IntegerToResultTypeConverter.INSTANCE,
                IntegerToEngineStatusConverter.INSTANCE, IntegerToRoleTypeConverter.INSTANCE,
                IntegerToVerificationElementTypeConverter.INSTANCE, IntegerToRawTypeConverter.INSTANCE);
        return new MongoCustomConversions(converters);
    }

    @Bean
    AuditorAware<String> auditorAware() {
        // get createUserId and modifyUserId
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .filter(user -> !"anonymousUser".equals(user.toString()))
            .map(CustomUser.class::cast)
            .map(CustomUser::getId);
    }


    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory dbFactory, MongoConverter mongoConverter) {
        return new GridFsTemplate(dbFactory, mongoConverter, BUCKET);
    }

    @WritingConverter
    enum EnumCommonToIntegerConverter implements Converter<EnumCommon, Integer> {
        INSTANCE;

        public Integer convert(EnumCommon enumCommon) {
            return enumCommon.getCode();
        }
    }

    @ReadingConverter
    enum IntegerToApiProtocolConverter implements Converter<Integer, ApiProtocol> {
        INSTANCE;

        public ApiProtocol convert(@NonNull Integer code) {
            return ApiProtocol.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToApiRequestParamTypeConverter implements Converter<Integer, ApiRequestParamType> {
        INSTANCE;

        public ApiRequestParamType convert(@NonNull Integer code) {
            return ApiRequestParamType.getType(code);
        }
    }


    @ReadingConverter
    enum IntegerToApiStatusConverter implements Converter<Integer, ApiStatus> {
        INSTANCE;

        public ApiStatus convert(@NonNull Integer code) {
            return ApiStatus.getType(code);
        }
    }


    @ReadingConverter
    enum IntegerToParamTypeConverter implements Converter<Integer, ParamType> {
        INSTANCE;

        public ParamType convert(@NonNull Integer code) {
            return ParamType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToRequestMethodConverter implements Converter<Integer, RequestMethod> {
        INSTANCE;

        public RequestMethod convert(@NonNull Integer code) {
            return RequestMethod.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToApiTagTypeConverter implements Converter<Integer, ApiTagType> {
        INSTANCE;

        public ApiTagType convert(@NonNull Integer code) {
            return ApiTagType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToApiJsonTypeConverter implements Converter<Integer, ApiJsonType> {
        INSTANCE;

        public ApiJsonType convert(@NonNull Integer code) {
            return ApiJsonType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToSaveModeConverter implements Converter<Integer, SaveMode> {
        INSTANCE;

        public SaveMode convert(@NonNull Integer code) {
            return SaveMode.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToDocumentTypeConverter implements Converter<Integer, DocumentType> {
        INSTANCE;

        public DocumentType convert(@NonNull Integer code) {
            return DocumentType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToGroupImportTypeConverter implements Converter<Integer, GroupImportType> {
        INSTANCE;

        public GroupImportType convert(@NonNull Integer code) {
            return GroupImportType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToOperationTypeConverter implements Converter<Integer, OperationType> {
        INSTANCE;

        public OperationType convert(@NonNull Integer code) {
            return OperationType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToOperationModuleConverter implements Converter<Integer, OperationModule> {
        INSTANCE;

        public OperationModule convert(@NonNull Integer code) {
            return OperationModule.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToApiTypeConverter implements Converter<Integer, ApiType> {
        INSTANCE;

        public ApiType convert(@NonNull Integer code) {
            return ApiType.getApiType(code);
        }
    }

    @ReadingConverter
    enum IntegerToMatchTypeConverter implements Converter<Integer, MatchType> {
        INSTANCE;

        public MatchType convert(@NonNull Integer code) {
            return MatchType.getMatchType(code);
        }
    }

    @ReadingConverter
    enum IntegerToDocumentUrlTypeConverter implements Converter<Integer, DocumentUrlType> {
        INSTANCE;

        public DocumentUrlType convert(@NonNull Integer code) {
            return DocumentUrlType.getType(code);
        }
    }


    @ReadingConverter
    enum IntegerToApiBindingStatusConverter implements Converter<Integer, ApiBindingStatus> {
        INSTANCE;

        public ApiBindingStatus convert(@NonNull Integer code) {
            return ApiBindingStatus.getApiBindingStatus(code);
        }
    }

    @ReadingConverter
    enum IntegerToJobStatusConverter implements Converter<Integer, JobStatus> {
        INSTANCE;

        public JobStatus convert(@NonNull Integer code) {
            return JobStatus.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToProjectTypeConverter implements Converter<Integer, ProjectType> {
        INSTANCE;

        public ProjectType convert(@NonNull Integer code) {
            return ProjectType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToImportStatusConverter implements Converter<Integer, ImportStatus> {
        INSTANCE;

        public ImportStatus convert(@NonNull Integer code) {
            return ImportStatus.getType(code);
        }
    }


    @ReadingConverter
    enum IntegerToResultVerificationTypeConverter implements Converter<Integer, ResultVerificationType> {
        INSTANCE;

        public ResultVerificationType convert(@NonNull Integer code) {
            return ResultVerificationType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToResponseParamsExtractionTypeConverter implements Converter<Integer, ResponseParamsExtractionType> {
        INSTANCE;

        public ResponseParamsExtractionType convert(@NonNull Integer code) {
            return ResponseParamsExtractionType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToResultTypeConverter implements Converter<Integer, ResultType> {
        INSTANCE;

        public ResultType convert(@NonNull Integer code) {
            return ResultType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToEngineStatusConverter implements Converter<Integer, EngineStatus> {
        INSTANCE;

        public EngineStatus convert(@NonNull Integer code) {
            return EngineStatus.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToRoleTypeConverter implements Converter<Integer, RoleType> {
        INSTANCE;

        public RoleType convert(@NonNull Integer code) {
            return RoleType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToVerificationElementTypeConverter implements Converter<Integer, VerificationElementType> {
        INSTANCE;

        public VerificationElementType convert(@NonNull Integer code) {
            return VerificationElementType.getType(code);
        }
    }

    @ReadingConverter
    enum IntegerToRawTypeConverter implements Converter<Integer, RawType> {
        INSTANCE;

        public RawType convert(@NonNull Integer code) {
            return RawType.getType(code);
        }
    }

}
