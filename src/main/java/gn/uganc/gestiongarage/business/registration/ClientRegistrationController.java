package gn.uganc.gestiongarage.business.registration;

import gn.uganc.gestiongarage.business.registration.dtos.ClientRegistrationRequest;
import gn.uganc.gestiongarage.business.registration.dtos.ClientRegistrationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/client-registration")
public class ClientRegistrationController {

    private final ClientRegistrationService clientRegistrationService;

    public ClientRegistrationController(ClientRegistrationService clientRegistrationService) {
        this.clientRegistrationService = clientRegistrationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientRegistrationResponse register(@RequestBody ClientRegistrationRequest request) {
        return clientRegistrationService.register(request);
    }
}
