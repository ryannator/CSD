package csd.tariff.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "agreement_participants", schema = "tariff")
public class AgreementParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "agreement_id", nullable = false)
    private TradeAgreement agreement;
    
    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
    
    @Column(name = "participant_type", length = 20)
    private String participantType; // PARTNER, EXCLUDED, etc.
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public AgreementParticipant() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AgreementParticipant(TradeAgreement agreement, Country country, String participantType) {
        this();
        this.agreement = agreement;
        this.country = country;
        this.participantType = participantType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public TradeAgreement getAgreement() {
        return agreement;
    }
    
    public void setAgreement(TradeAgreement agreement) {
        this.agreement = agreement;
    }
    
    public Country getCountry() {
        return country;
    }
    
    public void setCountry(Country country) {
        this.country = country;
    }
    
    public String getParticipantType() {
        return participantType;
    }
    
    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}