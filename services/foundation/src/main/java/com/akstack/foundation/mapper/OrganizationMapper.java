package com.akstack.foundation.mapper;

import com.akstack.foundation.domain.model.Organization;
import com.akstack.foundation.web.dto.CreateOrganizationRequest;
import com.akstack.foundation.web.dto.OrganizationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    Organization toEntity(CreateOrganizationRequest request);

    OrganizationResponse toResponse(Organization organization);
}
