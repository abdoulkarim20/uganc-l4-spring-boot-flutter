package gn.uganc.gestiongarage.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gestion Garage API",
                version = "1.0.0",
                description = "Documentation des endpoints de gestion du garage"
        )
)
public class OpenApiConfig {
}
