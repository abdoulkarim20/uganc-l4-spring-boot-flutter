package gn.uganc.gestiongarage.business.client;

import gn.uganc.gestiongarage.business.client.dtos.ClientDto;

import java.util.List;

public interface IClient {

    ClientDto create(ClientDto clientDto);

    List<ClientDto> getAll();

    ClientDto getById(Long id);

    ClientDto update(Long id, ClientDto clientDto);

    void delete(Long id);
}
