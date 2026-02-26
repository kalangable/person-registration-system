package com.akstack.foundation.service;

import com.akstack.foundation.service.exception.DuplicateIdentificationException;
import com.akstack.foundation.service.exception.InvalidIdentificationException;
import com.akstack.foundation.service.exception.ResourceNotFoundException;
import com.akstack.foundation.service.validation.IdentificationValidationService;
import com.akstack.foundation.domain.model.Person;
import com.akstack.foundation.domain.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final PartyNumberService partyNumberService;
    private final IdentificationValidationService validationService;

    public Person create(Person person) {
        // Validate identification if provided
        if (person.getIdentificationType() != null && person.getIdentificationNumber() != null) {
            validateIdentification(person.getIdentificationType(), person.getIdentificationNumber());
            checkDuplicateIdentification(person.getIdentificationType(), person.getIdentificationNumber());
        }

        // Generate party number
        String partyNumber = partyNumberService.generatePartyNumber();
        person.setPartyNumber(partyNumber);

        return personRepository.save(person);
    }

    @Transactional(readOnly = true)
    public Person findById(Long id) {
        return personRepository.findById(id)
                .filter(person -> !person.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Person", id.toString()));
    }

    @Transactional(readOnly = true)
    public List<Person> findAll() {
        return personRepository.findAllNotDeleted();
    }

    @Transactional(readOnly = true)
    public Person findByIdentification(String identificationType, String identificationNumber) {
        return personRepository.findByIdentification(identificationType, identificationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Person with " + identificationType, identificationNumber));
    }

    public Person update(Long id, Person updatedPerson) {
        Person existingPerson = findById(id);

        // Validate new identification if changed
        if (updatedPerson.getIdentificationType() != null && 
            updatedPerson.getIdentificationNumber() != null) {
            
            boolean identificationChanged = 
                !updatedPerson.getIdentificationType().equals(existingPerson.getIdentificationType()) ||
                !updatedPerson.getIdentificationNumber().equals(existingPerson.getIdentificationNumber());

            if (identificationChanged) {
                validateIdentification(updatedPerson.getIdentificationType(), updatedPerson.getIdentificationNumber());
                checkDuplicateIdentification(updatedPerson.getIdentificationType(), updatedPerson.getIdentificationNumber());
            }
        }

        // Update fields
        existingPerson.setFirstName(updatedPerson.getFirstName());
        existingPerson.setMiddleName(updatedPerson.getMiddleName());
        existingPerson.setLastName(updatedPerson.getLastName());
        existingPerson.setDateOfBirth(updatedPerson.getDateOfBirth());
        existingPerson.setIdentificationType(updatedPerson.getIdentificationType());
        existingPerson.setIdentificationNumber(updatedPerson.getIdentificationNumber());
        existingPerson.setIsActive(updatedPerson.getIsActive());

        return personRepository.save(existingPerson);
    }

    public void delete(Long id) {
        Person person = findById(id);
        person.softDelete();
        personRepository.save(person);
    }

    public void activate(Long id) {
        Person person = findById(id);
        person.activate();
        personRepository.save(person);
    }

    public void deactivate(Long id) {
        Person person = findById(id);
        person.deactivate();
        personRepository.save(person);
    }

    private void validateIdentification(String identificationType, String identificationNumber) {
        if (!validationService.validate(identificationType, identificationNumber)) {
            throw new InvalidIdentificationException(identificationType, identificationNumber);
        }
    }

    private void checkDuplicateIdentification(String identificationType, String identificationNumber) {
        if (personRepository.existsByIdentificationAndNotDeleted(identificationType, identificationNumber)) {
            throw new DuplicateIdentificationException(identificationType, identificationNumber);
        }
    }
}
