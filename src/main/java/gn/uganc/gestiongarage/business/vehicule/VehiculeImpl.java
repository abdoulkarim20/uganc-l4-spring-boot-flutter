package gn.uganc.gestiongarage.business.vehicule;

import gn.uganc.gestiongarage.business.client.Client;
import gn.uganc.gestiongarage.business.client.ClientRepository;
import gn.uganc.gestiongarage.business.vehicule.dtos.VehiculeDto;
import gn.uganc.gestiongarage.business.vehicule.mappers.VehiculeMapper;
import gn.uganc.gestiongarage.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculeImpl implements IVehicule {

    private final VehiculeRepository vehiculeRepository;
    private final ClientRepository clientRepository;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeImpl(VehiculeRepository vehiculeRepository, ClientRepository clientRepository,
                        VehiculeMapper vehiculeMapper) {
        this.vehiculeRepository = vehiculeRepository;
        this.clientRepository = clientRepository;
        this.vehiculeMapper = vehiculeMapper;
    }

    @Override
    public VehiculeDto create(VehiculeDto vehiculeDto) {
        Client client = findClient(vehiculeDto.getClientId());
        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDto, client);
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    public List<VehiculeDto> getAll() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDto)
                .toList();
    }

    @Override
    public VehiculeDto getById(Long id) {
        Vehicule vehicule = findVehicule(id);
        return vehiculeMapper.toDto(vehicule);
    }

    @Override
    public VehiculeDto update(Long id, VehiculeDto vehiculeDto) {
        Vehicule vehicule = findVehicule(id);
        Client client = findClient(vehiculeDto.getClientId());
        vehicule.setImmatriculation(vehiculeDto.getImmatriculation());
        vehicule.setMarque(vehiculeDto.getMarque());
        vehicule.setModele(vehiculeDto.getModele());
        vehicule.setAnnee(vehiculeDto.getAnnee());
        vehicule.setClient(client);
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    @Override
    public void delete(Long id) {
        Vehicule vehicule = findVehicule(id);
        vehiculeRepository.delete(vehicule);
    }

    private Vehicule findVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", id));
    }

    private Client findClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }
}
