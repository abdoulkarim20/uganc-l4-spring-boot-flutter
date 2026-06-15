package gn.uganc.gestiongarage.business.utilisateur;

import gn.uganc.gestiongarage.business.utilisateur.dtos.UtilisateurDto;
import gn.uganc.gestiongarage.business.utilisateur.mappers.UtilisateurMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UtilisateurImpl implements IUtilisateur {

    private final UtilisateurRepository utilisateurRepository;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurImpl(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UtilisateurDto create(UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDto);
        utilisateur.setPassword(passwordEncoder.encode(utilisateurDto.getPassword()));
        return utilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public List<UtilisateurDto> getAll() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDto)
                .toList();
    }

    @Override
    public UtilisateurDto getById(Long id) {
        Utilisateur utilisateur = findUtilisateur(id);
        return utilisateurMapper.toDto(utilisateur);
    }

    @Override
    public UtilisateurDto update(Long id, UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = findUtilisateur(id);
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setTelephone(utilisateurDto.getTelephone());
        utilisateur.setUsername(utilisateurDto.getUsername());
        if (StringUtils.hasText(utilisateurDto.getPassword())) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateurDto.getPassword()));
        }
        utilisateur.setRole(utilisateurDto.getRole());
        return utilisateurMapper.toDto(utilisateurRepository.save(utilisateur));
    }

    @Override
    public void delete(Long id) {
        Utilisateur utilisateur = findUtilisateur(id);
        utilisateurRepository.delete(utilisateur);
    }

    private Utilisateur findUtilisateur(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'id " + id));
    }
}
