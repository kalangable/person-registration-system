package com.akstack.foundation.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "party")
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "party_number", nullable = false, unique = true, length = 20)
    private String partyNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false, length = 20)
    private PartyType partyType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    protected Party() {
    }

    protected Party(PartyType partyType) {
        this.partyType = partyType;
        this.isActive = true;
        this.isDeleted = false;
    }

    // Business methods
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
