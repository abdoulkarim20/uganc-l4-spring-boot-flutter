package gn.uganc.gestiongarage.business.reparation;

import gn.uganc.gestiongarage.business.garage.Garage;
import gn.uganc.gestiongarage.business.garage.GarageRepository;
import gn.uganc.gestiongarage.business.reparation.dtos.ReparationDto;
import gn.uganc.gestiongarage.business.reparation.mappers.ReparationMapper;
import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import gn.uganc.gestiongarage.business.vehicule.VehiculeRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import gn.uganc.gestiongarage.security.CurrentUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReparationImpl implements IReparation {

    private final ReparationRepository reparationRepository;
    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final GarageRepository garageRepository;
    private final ReparationMapper reparationMapper;
    private final CurrentUserService currentUserService;

    public ReparationImpl(ReparationRepository reparationRepository, VehiculeRepository vehiculeRepository,
                          UtilisateurRepository utilisateurRepository, GarageRepository garageRepository,
                          ReparationMapper reparationMapper, CurrentUserService currentUserService) {
        this.reparationRepository = reparationRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.garageRepository = garageRepository;
        this.reparationMapper = reparationMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public ReparationDto create(ReparationDto reparationDto) {
        Vehicule vehicule = findVehicule(reparationDto.getVehiculeId());
        Utilisateur mecanicien = findMecanicienUser(reparationDto.getMecanicienId());
        Garage garage = resolveGarage(reparationDto, mecanicien);
        validateGarageCoherence(mecanicien, garage);
        Reparation reparation = reparationMapper.toEntity(reparationDto, vehicule, mecanicien, garage);
        return reparationMapper.toDto(reparationRepository.save(reparation));
    }

    @Override
    public List<ReparationDto> getAll() {
        if (currentUserService.isGarageAdmin()) {
            return reparationRepository.findByGarageId(currentUserService.getCurrentGarageId()).stream()
                    .map(reparationMapper::toDto)
                    .toList();
        }
        return reparationRepository.findAll().stream()
                .map(reparationMapper::toDto)
                .toList();
    }

    @Override
    public ReparationDto getById(Long id) {
        return reparationMapper.toDto(findReparation(id));
    }

    @Override
    public ReparationDto update(Long id, ReparationDto reparationDto) {
        Reparation reparation = findReparation(id);
        Vehicule vehicule = findVehicule(reparationDto.getVehiculeId());
        Utilisateur mecanicien = findMecanicienUser(reparationDto.getMecanicienId());
        Garage garage = resolveGarage(reparationDto, mecanicien);
        validateGarageCoherence(mecanicien, garage);
        reparation.setDateReparation(reparationDto.getDateReparation());
        reparation.setDescription(reparationDto.getDescription());
        reparation.setCout(reparationDto.getCout());
        reparation.setStatut(reparationDto.getStatut());
        reparation.setVehicule(vehicule);
        reparation.setMecanicienUtilisateur(mecanicien);
        reparation.setGarage(garage);
        return reparationMapper.toDto(reparationRepository.save(reparation));
    }

    @Override
    public void delete(Long id) {
        reparationRepository.delete(findReparation(id));
    }

    private Reparation findReparation(Long id) {
        Reparation reparation = reparationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reparation", id));
        if (currentUserService.isGarageAdmin()
                && (reparation.getGarage() == null || !reparation.getGarage().getId().equals(currentUserService.getCurrentGarageId()))) {
            throw new ResourceNotFoundException("Reparation", id);
        }
        return reparation;
    }

    private Vehicule findVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    private Utilisateur findMecanicienUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mecanicien", id));
        if (utilisateur.getRole() != RoleUser.MECANICIEN) {
            throw new BusinessException("Le technicien affecte doit avoir le profil MECANICIEN.");
        }
        return utilisateur;
    }

    private Garage resolveGarage(ReparationDto reparationDto, Utilisateur mecanicien) {
        if (currentUserService.isGarageAdmin()) {
            Long currentGarageId = currentUserService.getCurrentGarageId();
            if (reparationDto.getGarageId() != null && !reparationDto.getGarageId().equals(currentGarageId)) {
                throw new BusinessException("Vous ne pouvez pas rattacher cette reparation a un autre garage.");
            }
            return garageRepository.findById(currentGarageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Garage", currentGarageId));
        }
        if (reparationDto.getGarageId() != null) {
            return garageRepository.findById(reparationDto.getGarageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Garage", reparationDto.getGarageId()));
        }
        if (mecanicien.getGarage() == null) {
            throw new BusinessException("Le mecanicien doit etre rattache a un garage.");
        }
        return mecanicien.getGarage();
    }

    private void validateGarageCoherence(Utilisateur mecanicien, Garage garage) {
        if (mecanicien.getGarage() == null || !mecanicien.getGarage().getId().equals(garage.getId())) {
            throw new BusinessException("Le mecanicien doit appartenir au meme garage que la reparation.");
        }
    }
}
