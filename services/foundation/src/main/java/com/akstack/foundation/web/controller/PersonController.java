package com.akstack.foundation.web.controller;

import com.akstack.foundation.service.PersonService;
import com.akstack.foundation.domain.model.Person;
import com.akstack.foundation.web.dto.CreatePersonRequest;
import com.akstack.foundation.web.dto.PersonResponse;
import com.akstack.foundation.mapper.PersonMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonService personService;
    private final PersonMapper personMapper;

    public PersonController(PersonService personService, PersonMapper personMapper) {
        this.personService = personService;
        this.personMapper = personMapper;
    }

    @PostMapping
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody CreatePersonRequest request) {
        Person person = personMapper.toEntity(request);
        Person createdPerson = personService.create(person);
        PersonResponse response = personMapper.toResponse(createdPerson);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> findById(@PathVariable Long id) {
        Person person = personService.findById(id);
        PersonResponse response = personMapper.toResponse(person);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PersonResponse>> findAll() {
        List<Person> persons = personService.findAll();
        List<PersonResponse> responses = persons.stream()
                .map(personMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable Long id, 
                                                  @Valid @RequestBody CreatePersonRequest request) {
        Person person = personMapper.toEntity(request);
        Person updatedPerson = personService.update(id, person);
        PersonResponse response = personMapper.toResponse(updatedPerson);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        personService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        personService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
