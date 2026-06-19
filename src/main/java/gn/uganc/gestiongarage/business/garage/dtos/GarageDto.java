package gn.uganc.gestiongarage.business.garage.dtos;

import gn.uganc.gestiongarage.business.garage.StatutGarage;

import java.time.LocalDateTime;

public class GarageDto {

    private Long id;
    private String nom;
    private String telephone;
    private String email;
    private String adresse;
    private String ville;
    private String quartier;
    private String pays;
    private Double latitude;
    private Double longitude;
    private String description;
    private String nomResponsable;
    private String telephoneResponsable;
    private StatutGarage statut;
    private LocalDateTime dateCreation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public String getTelephoneResponsable() {
        return telephoneResponsable;
    }

    public void setTelephoneResponsable(String telephoneResponsable) {
        this.telephoneResponsable = telephoneResponsable;
    }

    public StatutGarage getStatut() {
        return statut;
    }

    public void setStatut(StatutGarage statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
