package com.akstack.foundation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
public class PartyNumberService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public String generatePartyNumber() {
        BigInteger nextVal = (BigInteger) entityManager
                .createNativeQuery("SELECT nextval('party_number_seq')")
                .getSingleResult();
        
        return String.valueOf(nextVal.longValue());
    }
}
