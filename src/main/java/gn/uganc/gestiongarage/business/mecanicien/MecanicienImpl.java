package gn.uganc.gestiongarage.business.mecanicien;

import gn.uganc.gestiongarage.business.mecanicien.dtos.MecanicienDto;
import gn.uganc.gestiongarage.business.mecanicien.mappers.MecanicienMapper;
import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MecanicienImpl implements IMecanicien {

    private final MecanicienRepository mecanicienRepository;
    private final MecanicienMapper mecanicienMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public MecanicienImpl(MecanicienRepository mecanicienRepository, MecanicienMapper mecanicienMapper,
                          UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.mecanicienRepository = mecanicienRepository;
        this.mecanicienMapper = mecanicienMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public MecanicienDto create(MecanicienDto mecanicienDto) {
        Mecanicien mecanicien = mecanicienMapper.toEntity(mecanicienDto);
        Mecanicien savedMecanicien = mecanicienRepository.save(mecanicien);
        createMecanicienUserIfMissing(savedMecanicien, mecanicienDto.getPassword());
        return mecanicienMapper.toDto(savedMecanicien);
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
        String oldTelephone = mecanicien.getTelephone();
        mecanicien.setNom(mecanicienDto.getNom());
        mecanicien.setPrenom(mecanicienDto.getPrenom());
        mecanicien.setTelephone(mecanicienDto.getTelephone());
        mecanicien.setSpecialite(mecanicienDto.getSpecialite());
        Mecanicien savedMecanicien = mecanicienRepository.save(mecanicien);
        syncMecanicienUser(oldTelephone, savedMecanicien);
        return mecanicienMapper.toDto(savedMecanicien);
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

    private void createMecanicienUserIfMissing(Mecanicien mecanicien, String rawPassword) {
        if (!StringUtils.hasText(mecanicien.getTelephone()) || utilisateurRepository.existsByTelephone(mecanicien.getTelephone())) {
            return;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(mecanicien.getNom());
        utilisateur.setPrenom(mecanicien.getPrenom());
        utilisateur.setTelephone(mecanicien.getTelephone());
        utilisateur.setUsername(mecanicien.getTelephone());
        utilisateur.setPassword(passwordEncoder.encode(StringUtils.hasText(rawPassword) ? rawPassword : mecanicien.getTelephone()));
        utilisateur.setRole(RoleUser.MECANICIEN);
        utilisateur.setMustChangePassword(true);
        utilisateurRepository.save(utilisateur);
    }

    private void syncMecanicienUser(String oldTelephone, Mecanicien mecanicien) {
        if (!StringUtils.hasText(oldTelephone)) {
            return;
        }
        utilisateurRepository.findByTelephone(oldTelephone).ifPresent(utilisateur -> {
            if (utilisateur.getRole() != RoleUser.MECANICIEN) {
                return;
            }
            utilisateur.setNom(mecanicien.getNom());
            utilisateur.setPrenom(mecanicien.getPrenom());
            utilisateur.setTelephone(mecanicien.getTelephone());
            if (oldTelephone.equals(utilisateur.getUsername())) {
                utilisateur.setUsername(mecanicien.getTelephone());
            }
            utilisateurRepository.save(utilisateur);
        });
    }
}
