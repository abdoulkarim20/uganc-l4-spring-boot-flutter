package gn.uganc.gestiongarage.business.client.mappers;

import gn.uganc.gestiongarage.business.client.Client;
import gn.uganc.gestiongarage.business.client.dtos.ClientDto;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDto toDto(Client client) {
        ClientDto clientDto = new ClientDto();
        clientDto.setId(client.getId());
        clientDto.setNom(client.getNom());
        clientDto.setPrenom(client.getPrenom());
        clientDto.setTelephone(client.getTelephone());
        clientDto.setAdresse(client.getAdresse());
        return clientDto;
    }

    public Client toEntity(ClientDto clientDto) {
        Client client = new Client();
        client.setId(clientDto.getId());
        client.setNom(clientDto.getNom());
        client.setPrenom(clientDto.getPrenom());
        client.setTelephone(clientDto.getTelephone());
        client.setAdresse(clientDto.getAdresse());
        return client;
    }
}
