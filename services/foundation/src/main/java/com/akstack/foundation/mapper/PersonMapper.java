package com.akstack.foundation.mapper;

import com.akstack.foundation.domain.model.Person;
import com.akstack.foundation.web.dto.CreatePersonRequest;
import com.akstack.foundation.web.dto.PersonResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    Person toEntity(CreatePersonRequest request);

    PersonResponse toResponse(Person person);

}
