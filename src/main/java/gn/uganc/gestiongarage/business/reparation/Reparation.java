package gn.uganc.gestiongarage.business.reparation;

import gn.uganc.gestiongarage.business.mecanicien.Mecanicien;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "reparations")
public class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate dateReparation;
    @Column(length = 1000)
    private String description;
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal cout;
    @Column(length = 30, nullable = false)
    private String statut;

    @ManyToOne(optional = false)
    private Vehicule vehicule;

    @ManyToOne(optional = false)
    private Mecanicien mecanicien;

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

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Mecanicien getMecanicien() {
        return mecanicien;
    }

    public void setMecanicien(Mecanicien mecanicien) {
        this.mecanicien = mecanicien;
    }
}
