package org.penya.webauthn.backendauth;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

/**
 * Configures Jakarta RESTful Web Services for the application.
 *
 * @author Juneau
 */
@ApplicationPath("resources")
@OpenAPIDefinition(info = @Info(
        title = "Example application",
        version = "1.0.0",
        contact = @Contact(
                name = "Phillip Kruger",
                email = "phillip.kruger@phillip-kruger.com",
                url = "http://www.phillip-kruger.com")
),
        servers = {
            @Server(url = "/example", description = "localhost")
        }
)
public class JakartaRestConfiguration extends Application {

}
