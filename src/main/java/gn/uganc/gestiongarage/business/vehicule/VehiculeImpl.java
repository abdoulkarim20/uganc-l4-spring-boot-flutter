package gn.uganc.gestiongarage.business.vehicule;

import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.business.vehicule.dtos.VehiculeDto;
import gn.uganc.gestiongarage.business.vehicule.mappers.VehiculeMapper;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import gn.uganc.gestiongarage.security.CurrentUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeImpl implements IVehicule {

    private final VehiculeRepository vehiculeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final VehiculeMapper vehiculeMapper;
    private final CurrentUserService currentUserService;

    public VehiculeImpl(VehiculeRepository vehiculeRepository, UtilisateurRepository utilisateurRepository,
                        VehiculeMapper vehiculeMapper, CurrentUserService currentUserService) {
        this.vehiculeRepository = vehiculeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.vehiculeMapper = vehiculeMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public VehiculeDto create(VehiculeDto vehiculeDto) {
        Utilisateur proprietaire = findClientUser(vehiculeDto.getClientId());
        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDto, proprietaire);
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    public List<VehiculeDto> getAll() {
        if (currentUserService.isGarageAdmin()) {
            return vehiculeRepository.findDistinctByReparationsGarageId(currentUserService.getCurrentGarageId()).stream()
                    .map(vehiculeMapper::toDto)
                    .toList();
        }
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @Override
    public VehiculeDto getById(Long id) {
        return vehiculeMapper.toDto(findVehicule(id));
    }

    @Override
    public VehiculeDto update(Long id, VehiculeDto vehiculeDto) {
        Vehicule vehicule = findVehicule(id);
        Utilisateur proprietaire = findClientUser(vehiculeDto.getClientId());
        vehicule.setImmatriculation(vehiculeDto.getImmatriculation());
        vehicule.setMarque(vehiculeDto.getMarque());
        vehicule.setModele(vehiculeDto.getModele());
        vehicule.setAnnee(vehiculeDto.getAnnee());
        vehicule.setProprietaire(proprietaire);
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    public void delete(Long id) {
        vehiculeRepository.delete(findVehicule(id));
    }

    private Vehicule findVehicule(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        if (currentUserService.isGarageAdmin() && !isVisibleForCurrentGarage(vehicule.getId())) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        return vehicule;
    }

    private boolean isVisibleForCurrentGarage(Long vehiculeId) {
        return vehiculeRepository.findDistinctByReparationsGarageId(currentUserService.getCurrentGarageId())
                .stream()
                .anyMatch(vehicule -> vehicule.getId().equals(vehiculeId));
    }

    private Utilisateur findClientUser(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        if (utilisateur.getRole() != RoleUser.CLIENT) {
            throw new BusinessException("Le proprietaire du vehicule doit etre un client.");
        }
        return utilisateur;
    }
}
