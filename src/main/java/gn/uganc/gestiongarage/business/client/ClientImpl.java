package gn.uganc.gestiongarage.business.client;

import gn.uganc.gestiongarage.business.client.dtos.ClientDto;
import gn.uganc.gestiongarage.business.client.mappers.ClientMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientImpl implements IClient {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientImpl(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public ClientDto create(ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        return clientMapper.toDto(clientRepository.save(client));
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
        client.setNom(clientDto.getNom());
        client.setPrenom(clientDto.getPrenom());
        client.setTelephone(clientDto.getTelephone());
        client.setAdresse(clientDto.getAdresse());
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Override
    public void delete(Long id) {
        Client client = findClient(id);
        clientRepository.delete(client);
    }

    private Client findClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'id " + id));
    }
}
