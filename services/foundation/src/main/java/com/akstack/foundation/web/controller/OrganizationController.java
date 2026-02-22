package com.akstack.foundation.web.controller;

import com.akstack.foundation.service.OrganizationService;
import com.akstack.foundation.domain.model.Organization;
import com.akstack.foundation.web.dto.CreateOrganizationRequest;
import com.akstack.foundation.web.dto.OrganizationResponse;
import com.akstack.foundation.mapper.OrganizationMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    public OrganizationController(OrganizationService organizationService, 
                                  OrganizationMapper organizationMapper) {
        this.organizationService = organizationService;
        this.organizationMapper = organizationMapper;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody CreateOrganizationRequest request) {
        Organization organization = organizationMapper.toEntity(request);
        Organization createdOrganization = organizationService.create(organization);
        OrganizationResponse response = organizationMapper.toResponse(createdOrganization);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable Long id) {
        Organization organization = organizationService.findById(id);
        OrganizationResponse response = organizationMapper.toResponse(organization);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> findAll() {
        List<Organization> organizations = organizationService.findAll();
        List<OrganizationResponse> responses = organizations.stream()
                .map(organizationMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(@PathVariable Long id, 
                                                        @Valid @RequestBody CreateOrganizationRequest request) {
        Organization organization = organizationMapper.toEntity(request);
        Organization updatedOrganization = organizationService.update(id, organization);
        OrganizationResponse response = organizationMapper.toResponse(updatedOrganization);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        organizationService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        organizationService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
