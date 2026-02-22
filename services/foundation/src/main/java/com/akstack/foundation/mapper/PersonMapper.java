package com.akstack.foundation.mapper;

import com.akstack.foundation.domain.model.Person;
import com.akstack.foundation.web.dto.CreatePersonRequest;
import com.akstack.foundation.web.dto.PersonResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public Person toEntity(CreatePersonRequest request) {
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setMiddleName(request.getMiddleName());
        person.setLastName(request.getLastName());
        person.setDateOfBirth(request.getDateOfBirth());
        person.setIdentificationType(request.getIdentificationType());
        person.setIdentificationNumber(request.getIdentificationNumber());
        return person;
    }

    public PersonResponse toResponse(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setPartyNumber(person.getPartyNumber());
        response.setPartyType(person.getPartyType());
        response.setFirstName(person.getFirstName());
        response.setMiddleName(person.getMiddleName());
        response.setLastName(person.getLastName());
        response.setFullName(person.getFullName());
        response.setDateOfBirth(person.getDateOfBirth());
        response.setIdentificationType(person.getIdentificationType());
        response.setIdentificationNumber(person.getIdentificationNumber());
        response.setIsActive(person.getIsActive());
        response.setCreatedAt(person.getCreatedAt());
        response.setUpdatedAt(person.getUpdatedAt());
        return response;
    }
}
