package gn.uganc.gestiongarage.business.registration;

import gn.uganc.gestiongarage.business.garage.Garage;
import gn.uganc.gestiongarage.business.garage.GarageRepository;
import gn.uganc.gestiongarage.business.garage.StatutGarage;
import gn.uganc.gestiongarage.business.garage.dtos.GarageDto;
import gn.uganc.gestiongarage.business.garage.mappers.GarageMapper;
import gn.uganc.gestiongarage.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/garage-registration")
public class GarageRegistrationController {

    private final GarageRepository garageRepository;
    private final GarageMapper garageMapper;

    public GarageRegistrationController(GarageRepository garageRepository, GarageMapper garageMapper) {
        this.garageRepository = garageRepository;
        this.garageMapper = garageMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GarageDto register(@RequestBody GarageDto request) {
        validate(request);
        Garage garage = garageMapper.toEntity(request);
        garage.setStatut(StatutGarage.EN_ATTENTE);
        return garageMapper.toDto(garageRepository.save(garage));
    }

    private void validate(GarageDto request) {
        if (!StringUtils.hasText(request.getNom())) {
            throw new BusinessException("Le nom du garage est obligatoire.");
        }
        if (!StringUtils.hasText(request.getTelephone())) {
            throw new BusinessException("Le téléphone du garage est obligatoire.");
        }
        if (!StringUtils.hasText(request.getNomResponsable())) {
            throw new BusinessException("Le nom du responsable est obligatoire.");
        }
        if (!StringUtils.hasText(request.getTelephoneResponsable())) {
            throw new BusinessException("Le téléphone du responsable est obligatoire.");
        }
        if (garageRepository.existsByTelephone(request.getTelephone())) {
            throw new BusinessException("Ce numéro de téléphone est déjà utilisé par un garage.");
        }
    }
}
