package gn.uganc.gestiongarage.business.reparation.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReparationDto {

    private Long id;
    private LocalDate dateReparation;
    private String description;
    private BigDecimal cout;
    private String statut;
    private Long vehiculeId;
    private Long mecanicienId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateReparation() {
        return dateReparation;
    }

    public void setDateReparation(LocalDate dateReparation) {
        this.dateReparation = dateReparation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCout() {
        return cout;
    }

    public void setCout(BigDecimal cout) {
        this.cout = cout;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Long getVehiculeId() {
        return vehiculeId;
    }

    public void setVehiculeId(Long vehiculeId) {
        this.vehiculeId = vehiculeId;
    }

    public Long getMecanicienId() {
        return mecanicienId;
    }

    public void setMecanicienId(Long mecanicienId) {
        this.mecanicienId = mecanicienId;
    }
}
