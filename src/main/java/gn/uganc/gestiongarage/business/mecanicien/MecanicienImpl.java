package gn.uganc.gestiongarage.business.mecanicien;

import gn.uganc.gestiongarage.business.mecanicien.dtos.MecanicienDto;
import gn.uganc.gestiongarage.business.mecanicien.mappers.MecanicienMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MecanicienImpl implements IMecanicien {

    private final MecanicienRepository mecanicienRepository;
    private final MecanicienMapper mecanicienMapper;

    public MecanicienImpl(MecanicienRepository mecanicienRepository, MecanicienMapper mecanicienMapper) {
        this.mecanicienRepository = mecanicienRepository;
        this.mecanicienMapper = mecanicienMapper;
    }

    @Override
    public MecanicienDto create(MecanicienDto mecanicienDto) {
        Mecanicien mecanicien = mecanicienMapper.toEntity(mecanicienDto);
        return mecanicienMapper.toDto(mecanicienRepository.save(mecanicien));
    }

    @Override
    public List<MecanicienDto> getAll() {
        return mecanicienRepository.findAll().stream()
                .map(mecanicienMapper::toDto)
                .toList();
    }

    @Override
    public MecanicienDto getById(Long id) {
        Mecanicien mecanicien = findMecanicien(id);
        return mecanicienMapper.toDto(mecanicien);
    }

    @Override
    public MecanicienDto update(Long id, MecanicienDto mecanicienDto) {
        Mecanicien mecanicien = findMecanicien(id);
        mecanicien.setNom(mecanicienDto.getNom());
        mecanicien.setPrenom(mecanicienDto.getPrenom());
        mecanicien.setTelephone(mecanicienDto.getTelephone());
        mecanicien.setSpecialite(mecanicienDto.getSpecialite());
        return mecanicienMapper.toDto(mecanicienRepository.save(mecanicien));
    }

    @Override
    public void delete(Long id) {
        Mecanicien mecanicien = findMecanicien(id);
        mecanicienRepository.delete(mecanicien);
    }

    private Mecanicien findMecanicien(Long id) {
        return mecanicienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mecanicien introuvable avec l'id " + id));
    }
}
