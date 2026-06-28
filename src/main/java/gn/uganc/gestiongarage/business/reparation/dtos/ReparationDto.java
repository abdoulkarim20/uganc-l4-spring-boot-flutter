package gn.uganc.gestiongarage.business.reparation.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReparationDto {

    private Long id;
    private LocalDate dateReparation;
    private String description;
    private String consigneClient;
    private BigDecimal cout;
    private String statut;
    private Long vehiculeId;
    private Long mecanicienId;
    private Long garageId;
    private String garageNom;

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

    public String getConsigneClient() {
        return consigneClient;
    }

    public void setConsigneClient(String consigneClient) {
        this.consigneClient = consigneClient;
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

    public Long getGarageId() {
        return garageId;
    }

    public void setGarageId(Long garageId) {
        this.garageId = garageId;
    }

    public String getGarageNom() {
        return garageNom;
    }

    public void setGarageNom(String garageNom) {
        this.garageNom = garageNom;
    }
}
