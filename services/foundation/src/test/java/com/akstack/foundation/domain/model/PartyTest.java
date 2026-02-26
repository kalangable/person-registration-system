package com.akstack.foundation.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

class PartyTest {

    @Test
    void shouldCreatePartyWithCorrectDefaults() {
        Party party = new Person();

        assertThat(party.getPartyType()).isEqualTo(PartyType.PERSON);
        assertThat(party.getIsActive()).isTrue();
        assertThat(party.getIsDeleted()).isFalse();
        assertThat(party.getDeletedAt()).isNull();
    }

    @Test
    void shouldSetAuditingFields() {
        Party party = new Person();
        LocalDateTime now = LocalDateTime.now();

        party.setCreatedAt(now);
        party.setUpdatedAt(now);

        assertThat(party.getCreatedAt()).isEqualTo(now);
        assertThat(party.getUpdatedAt()).isEqualTo(now);
    }
}
