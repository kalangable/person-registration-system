package com.akstack.foundation.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PartyTypeTest {

    @Test
    void shouldHavePersonAndOrganizationValues() {
        assertThat(PartyType.values()).containsExactly(PartyType.PERSON, PartyType.ORGANIZATION);
    }

    @Test
    void shouldReturnCorrectStringValues() {
        assertThat(PartyType.PERSON.name()).isEqualTo("PERSON");
        assertThat(PartyType.ORGANIZATION.name()).isEqualTo("ORGANIZATION");
    }
}
