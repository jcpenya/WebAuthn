package org.penya.webauthn.backendauth;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

/**
 * Configura la base de los recursos REST para la aplicación y define los
 * parámetros para documentación Swagger
 *
 * @author jcpenya
 */
@ApplicationPath("resources")
@OpenAPIDefinition(info = @Info(
        title = "BackendAuth - Prototipo de Autenticación WebAuthn",
        version = "1.0.0",
        contact = @Contact(
                name = "jcpenya",
                email = "jcpenya@gmail.com",
                url = "http://www.penya.org")
),
        servers = {
            @Server(url = "/BackendAuth", description = "www.penya.org")
        }
)
public class JakartaRestConfiguration extends Application {

}
