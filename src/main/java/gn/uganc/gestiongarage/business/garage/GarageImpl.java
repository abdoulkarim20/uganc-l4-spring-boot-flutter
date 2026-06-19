package gn.uganc.gestiongarage.business.garage;

import gn.uganc.gestiongarage.business.garage.dtos.GarageDto;
import gn.uganc.gestiongarage.business.garage.mappers.GarageMapper;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.exception.BusinessException;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import gn.uganc.gestiongarage.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class GarageImpl implements IGarage {

    private final GarageRepository garageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final GarageMapper garageMapper;
    private final CurrentUserService currentUserService;

    public GarageImpl(GarageRepository garageRepository, UtilisateurRepository utilisateurRepository,
                      GarageMapper garageMapper, CurrentUserService currentUserService) {
        this.garageRepository = garageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.garageMapper = garageMapper;
        this.currentUserService = currentUserService;
    }

    @Override
    public GarageDto create(GarageDto garageDto) {
        validateGarage(garageDto, null);
        Garage garage = garageMapper.toEntity(garageDto);
        if (garage.getStatut() == null) {
            garage.setStatut(StatutGarage.EN_ATTENTE);
        }
        return garageMapper.toDto(garageRepository.save(garage));
    }

    @Override
    public List<GarageDto> getAll() {
        if (currentUserService.isGarageAdmin()) {
            return List.of(garageMapper.toDto(findGarage(currentUserService.getCurrentGarageId())));
        }
        return garageRepository.findAll().stream()
                .map(garageMapper::toDto)
                .toList();
    }

    @Override
    public GarageDto getById(Long id) {
        return garageMapper.toDto(findGarage(id));
    }

    @Override
    public GarageDto update(Long id, GarageDto garageDto) {
        Garage garage = findGarage(id);
        validateGarage(garageDto, id);
        garage.setNom(garageDto.getNom());
        garage.setTelephone(garageDto.getTelephone());
        garage.setEmail(garageDto.getEmail());
        garage.setAdresse(garageDto.getAdresse());
        garage.setVille(garageDto.getVille());
        garage.setQuartier(garageDto.getQuartier());
        garage.setPays(garageDto.getPays());
        garage.setLatitude(garageDto.getLatitude());
        garage.setLongitude(garageDto.getLongitude());
        garage.setDescription(garageDto.getDescription());
        garage.setNomResponsable(garageDto.getNomResponsable());
        garage.setTelephoneResponsable(garageDto.getTelephoneResponsable());
        garage.setStatut(garageDto.getStatut() == null ? garage.getStatut() : garageDto.getStatut());
        return garageMapper.toDto(garageRepository.save(garage));
    }

    @Override
    public void delete(Long id) {
        Garage garage = findGarage(id);
        if (utilisateurRepository.existsByGarageId(id)) {
            throw new BusinessException("Impossible de supprimer un garage qui contient des utilisateurs.");
        }
        garageRepository.delete(garage);
    }

    private Garage findGarage(Long id) {
        Garage garage = garageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage", id));
        if (currentUserService.isGarageAdmin() && !garage.getId().equals(currentUserService.getCurrentGarageId())) {
            throw new ResourceNotFoundException("Garage", id);
        }
        return garage;
    }

    private void validateGarage(GarageDto garageDto, Long currentId) {
        if (!StringUtils.hasText(garageDto.getNom())) {
            throw new BusinessException("Le nom du garage est obligatoire.");
        }
        if (!StringUtils.hasText(garageDto.getTelephone())) {
            throw new BusinessException("Le telephone du garage est obligatoire.");
        }
        boolean phoneExists = currentId == null
                ? garageRepository.existsByTelephone(garageDto.getTelephone())
                : garageRepository.existsByTelephoneAndIdNot(garageDto.getTelephone(), currentId);
        if (phoneExists) {
            throw new BusinessException("Ce numero de telephone est deja utilise par un garage.");
        }
    }
}
