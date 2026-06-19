package gn.uganc.gestiongarage.business.mecanicien.dtos;

public class MecanicienDto {

    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String specialite;
    private String password;
    private Long garageId;
    private String garageNom;

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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
