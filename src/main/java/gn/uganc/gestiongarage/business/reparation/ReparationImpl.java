package gn.uganc.gestiongarage.business.reparation;

import gn.uganc.gestiongarage.business.mecanicien.Mecanicien;
import gn.uganc.gestiongarage.business.mecanicien.MecanicienRepository;
import gn.uganc.gestiongarage.business.reparation.dtos.ReparationDto;
import gn.uganc.gestiongarage.business.reparation.mappers.ReparationMapper;
import gn.uganc.gestiongarage.business.vehicule.Vehicule;
import gn.uganc.gestiongarage.business.vehicule.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReparationImpl implements IReparation {

    private final ReparationRepository reparationRepository;
    private final VehiculeRepository vehiculeRepository;
    private final MecanicienRepository mecanicienRepository;
    private final ReparationMapper reparationMapper;

    public ReparationImpl(ReparationRepository reparationRepository, VehiculeRepository vehiculeRepository,
                          MecanicienRepository mecanicienRepository, ReparationMapper reparationMapper) {
        this.reparationRepository = reparationRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.mecanicienRepository = mecanicienRepository;
        this.reparationMapper = reparationMapper;
    }

    @Override
    public ReparationDto create(ReparationDto reparationDto) {
        Vehicule vehicule = findVehicule(reparationDto.getVehiculeId());
        Mecanicien mecanicien = findMecanicien(reparationDto.getMecanicienId());
        Reparation reparation = reparationMapper.toEntity(reparationDto, vehicule, mecanicien);
        return reparationMapper.toDto(reparationRepository.save(reparation));
    }

    @Override
    public List<ReparationDto> getAll() {
        return reparationRepository.findAll().stream()
                .map(reparationMapper::toDto)
                .toList();
    }

    @Override
    public ReparationDto getById(Long id) {
        Reparation reparation = findReparation(id);
        return reparationMapper.toDto(reparation);
    }

    @Override
    public ReparationDto update(Long id, ReparationDto reparationDto) {
        Reparation reparation = findReparation(id);
        Vehicule vehicule = findVehicule(reparationDto.getVehiculeId());
        Mecanicien mecanicien = findMecanicien(reparationDto.getMecanicienId());
        reparation.setDateReparation(reparationDto.getDateReparation());
        reparation.setDescription(reparationDto.getDescription());
        reparation.setCout(reparationDto.getCout());
        reparation.setStatut(reparationDto.getStatut());
        reparation.setVehicule(vehicule);
        reparation.setMecanicien(mecanicien);
        return reparationMapper.toDto(reparationRepository.save(reparation));
    }

    @Override
    public void delete(Long id) {
        Reparation reparation = findReparation(id);
        reparationRepository.delete(reparation);
    }

    private Reparation findReparation(Long id) {
        return reparationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reparation introuvable avec l'id " + id));
    }

    private Vehicule findVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicule introuvable avec l'id " + id));
    }

    private Mecanicien findMecanicien(Long id) {
        return mecanicienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mecanicien introuvable avec l'id " + id));
    }
}
