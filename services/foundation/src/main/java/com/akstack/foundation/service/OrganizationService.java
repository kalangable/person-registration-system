package com.akstack.foundation.service;

import com.akstack.foundation.service.exception.DuplicateIdentificationException;
import com.akstack.foundation.service.exception.InvalidIdentificationException;
import com.akstack.foundation.service.exception.ResourceNotFoundException;
import com.akstack.foundation.service.validation.IdentificationValidationService;
import com.akstack.foundation.domain.model.Organization;
import com.akstack.foundation.domain.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final PartyNumberService partyNumberService;
    private final IdentificationValidationService validationService;

    public Organization create(Organization organization) {
        // Validate identification if provided
        if (organization.getIdentificationType() != null && organization.getIdentificationNumber() != null) {
            validateIdentification(organization.getIdentificationType(), organization.getIdentificationNumber());
            checkDuplicateIdentification(organization.getIdentificationType(), organization.getIdentificationNumber());
        }

        // Generate party number
        String partyNumber = partyNumberService.generatePartyNumber();
        organization.setPartyNumber(partyNumber);

        return organizationRepository.save(organization);
    }

    @Transactional(readOnly = true)
    public Organization findById(Long id) {
        return organizationRepository.findById(id)
                .filter(org -> !org.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id.toString()));
    }

    @Transactional(readOnly = true)
    public List<Organization> findAll() {
        return organizationRepository.findAllNotDeleted();
    }

    @Transactional(readOnly = true)
    public Organization findByIdentification(String identificationType, String identificationNumber) {
        return organizationRepository.findByIdentification(identificationType, identificationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Organization with " + identificationType, identificationNumber));
    }

    public Organization update(Long id, Organization updatedOrganization) {
        Organization existingOrganization = findById(id);

        // Validate new identification if changed
        if (updatedOrganization.getIdentificationType() != null && 
            updatedOrganization.getIdentificationNumber() != null) {
            
            boolean identificationChanged = 
                !updatedOrganization.getIdentificationType().equals(existingOrganization.getIdentificationType()) ||
                !updatedOrganization.getIdentificationNumber().equals(existingOrganization.getIdentificationNumber());

            if (identificationChanged) {
                validateIdentification(updatedOrganization.getIdentificationType(), updatedOrganization.getIdentificationNumber());
                checkDuplicateIdentification(updatedOrganization.getIdentificationType(), updatedOrganization.getIdentificationNumber());
            }
        }

        // Update fields
        existingOrganization.setLegalName(updatedOrganization.getLegalName());
        existingOrganization.setTradeName(updatedOrganization.getTradeName());
        existingOrganization.setBrandName(updatedOrganization.getBrandName());
        existingOrganization.setFoundingDate(updatedOrganization.getFoundingDate());
        existingOrganization.setIdentificationType(updatedOrganization.getIdentificationType());
        existingOrganization.setIdentificationNumber(updatedOrganization.getIdentificationNumber());
        existingOrganization.setIsActive(updatedOrganization.getIsActive());

        return organizationRepository.save(existingOrganization);
    }

    public void delete(Long id) {
        Organization organization = findById(id);
        organization.softDelete();
        organizationRepository.save(organization);
    }

    public void activate(Long id) {
        Organization organization = findById(id);
        organization.activate();
        organizationRepository.save(organization);
    }

    public void deactivate(Long id) {
        Organization organization = findById(id);
        organization.deactivate();
        organizationRepository.save(organization);
    }

    private void validateIdentification(String identificationType, String identificationNumber) {
        if (!validationService.validate(identificationType, identificationNumber)) {
            throw new InvalidIdentificationException(identificationType, identificationNumber);
        }
    }

    private void checkDuplicateIdentification(String identificationType, String identificationNumber) {
        if (organizationRepository.existsByIdentificationAndNotDeleted(identificationType, identificationNumber)) {
            throw new DuplicateIdentificationException(identificationType, identificationNumber);
        }
    }
}
