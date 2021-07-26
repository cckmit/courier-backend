package com.sms.satp.service;

import com.sms.satp.dto.request.UserGroupRequest;
import com.sms.satp.dto.response.UserGroupResponse;
import com.sms.satp.entity.system.UserGroupEntity;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface UserGroupService {

    UserGroupEntity findById(String id);

    List<UserGroupResponse> list();

    Boolean add(UserGroupRequest userGroupRequest);

    Boolean edit(UserGroupRequest userGroupRequest);

    Boolean delete(List<String> ids);

    List<SimpleGrantedAuthority> getAuthoritiesByUserGroup(String groupId);
}