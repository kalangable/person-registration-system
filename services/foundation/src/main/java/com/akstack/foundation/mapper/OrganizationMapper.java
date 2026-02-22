package com.akstack.foundation.mapper;

import com.akstack.foundation.domain.model.Organization;
import com.akstack.foundation.web.dto.CreateOrganizationRequest;
import com.akstack.foundation.web.dto.OrganizationResponse;
import org.springframework.stereotype.Component;

@Component
public class OrganizationMapper {

    public Organization toEntity(CreateOrganizationRequest request) {
        Organization organization = new Organization();
        organization.setLegalName(request.getLegalName());
        organization.setTradeName(request.getTradeName());
        organization.setBrandName(request.getBrandName());
        organization.setFoundingDate(request.getFoundingDate());
        organization.setIdentificationType(request.getIdentificationType());
        organization.setIdentificationNumber(request.getIdentificationNumber());
        return organization;
    }

    public OrganizationResponse toResponse(Organization organization) {
        OrganizationResponse response = new OrganizationResponse();
        response.setId(organization.getId());
        response.setPartyNumber(organization.getPartyNumber());
        response.setPartyType(organization.getPartyType());
        response.setLegalName(organization.getLegalName());
        response.setTradeName(organization.getTradeName());
        response.setBrandName(organization.getBrandName());
        response.setFoundingDate(organization.getFoundingDate());
        response.setIdentificationType(organization.getIdentificationType());
        response.setIdentificationNumber(organization.getIdentificationNumber());
        response.setIsActive(organization.getIsActive());
        response.setCreatedAt(organization.getCreatedAt());
        response.setUpdatedAt(organization.getUpdatedAt());
        return response;
    }
}
