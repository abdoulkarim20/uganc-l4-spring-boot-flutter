package gn.uganc.gestiongarage.business.utilisateur;

import gn.uganc.gestiongarage.business.garage.Garage;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String nom;
    @Column(length = 100, nullable = false)
    private String prenom;
    @Column(length = 20, nullable = false, unique = true)
    private String telephone;
    @Column(length = 80, nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleUser role;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean mustChangePassword = false;
    @Column(length = 120)
    private String email;
    @Column(length = 180)
    private String adresse;
    @Column(length = 100)
    private String specialite;
    private LocalDateTime dateCreation;
    @ManyToOne
    @JoinColumn(name = "garage_id")
    private Garage garage;

    @PrePersist
    void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleUser getRole() {
        return role;
    }

    public void setRole(RoleUser role) {
        this.role = role;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }
}
