package gn.uganc.gestiongarage.business.client;

import gn.uganc.gestiongarage.business.client.dtos.ClientDto;
import gn.uganc.gestiongarage.business.client.mappers.ClientMapper;
import gn.uganc.gestiongarage.business.utilisateur.RoleUser;
import gn.uganc.gestiongarage.business.utilisateur.Utilisateur;
import gn.uganc.gestiongarage.business.utilisateur.UtilisateurRepository;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ClientImpl implements IClient {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientImpl(ClientRepository clientRepository, ClientMapper clientMapper,
                      UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientDto create(ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        Client savedClient = clientRepository.save(client);
        createClientUserIfMissing(savedClient, clientDto.getPassword());
        return clientMapper.toDto(savedClient);
    }

    @Override
    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .toList();
    }

    @Override
    public ClientDto getById(Long id) {
        Client client = findClient(id);
        return clientMapper.toDto(client);
    }

    @Override
    public ClientDto update(Long id, ClientDto clientDto) {
        Client client = findClient(id);
        String oldTelephone = client.getTelephone();
        client.setNom(clientDto.getNom());
        client.setPrenom(clientDto.getPrenom());
        client.setTelephone(clientDto.getTelephone());
        client.setAdresse(clientDto.getAdresse());
        Client savedClient = clientRepository.save(client);
        syncClientUser(oldTelephone, savedClient);
        return clientMapper.toDto(savedClient);
    }

    @Override
    public void delete(Long id) {
        Client client = findClient(id);
        clientRepository.delete(client);
    }

    private Client findClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    private void createClientUserIfMissing(Client client, String rawPassword) {
        if (!StringUtils.hasText(client.getTelephone()) || utilisateurRepository.existsByTelephone(client.getTelephone())) {
            return;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(client.getNom());
        utilisateur.setPrenom(client.getPrenom());
        utilisateur.setTelephone(client.getTelephone());
        utilisateur.setUsername(client.getTelephone());
        utilisateur.setPassword(passwordEncoder.encode(StringUtils.hasText(rawPassword) ? rawPassword : client.getTelephone()));
        utilisateur.setRole(RoleUser.CLIENT);
        utilisateur.setMustChangePassword(true);
        utilisateurRepository.save(utilisateur);
    }

    private void syncClientUser(String oldTelephone, Client client) {
        if (!StringUtils.hasText(oldTelephone)) {
            return;
        }
        utilisateurRepository.findByTelephone(oldTelephone).ifPresent(utilisateur -> {
            if (utilisateur.getRole() != RoleUser.CLIENT) {
                return;
            }
            utilisateur.setNom(client.getNom());
            utilisateur.setPrenom(client.getPrenom());
            utilisateur.setTelephone(client.getTelephone());
            if (oldTelephone.equals(utilisateur.getUsername())) {
                utilisateur.setUsername(client.getTelephone());
            }
            utilisateurRepository.save(utilisateur);
        });
    }
}
