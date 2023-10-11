package org.penya.webauthn.backendauth.auth.control;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.penya.webauthn.backendauth.auth.entity.Autenticador;
import org.penya.webauthn.backendauth.auth.entity.Usuario;

/**
 * Crea un Repositorio de Credenciales, usando el almacenamiento de la
 * aplicación para persistencia
 *
 * @author jcpenya
 */
@Named
@ApplicationScoped
public class RepositorioCredenciales implements CredentialRepository, Serializable {

    @Inject
    UsuarioBean uBean;

    @Inject
    AutenticadorBean aBean;

    /**
     * Busca las claves públicas almacenadas para el Usuario provisto
     *
     * @param nombreUsuario Usuario a buscar
     * @return Conjunto de Claves Públicas para el usuario
     * @see
     * AutenticadorBean#findByUsuario(org.penya.webauthn.backendauth.auth.entity.Usuario,
     * int, int)
     * @see UsuarioBean#findByUsuario(java.lang.String)
     */
    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String nombreUsuario) {
        Usuario usuario = uBean.findByUsuario(nombreUsuario);
        List<Autenticador> autenticador = aBean.findByUsuario(usuario, 0, 10000000);
        return autenticador.stream().map(
                credencial -> PublicKeyCredentialDescriptor.builder().
                        id(credencial.getIdentificadorCredencial()).build()).
                collect(Collectors.toSet()
                );
    }

    /**
     * Busca el identificador unico del usuario para el estandar WebAuthn
     *
     * @param nombreUsuario Usuario a buscar
     * @return Identificador unico del usuario si existiese.
     * @see Usuario#getHandle()
     */
    @Override
    public Optional<ByteArray> getUserHandleForUsername(String nombreUsuario) {
        Usuario usuario = uBean.findByUsuario(nombreUsuario);
        return Optional.of(usuario.getHandle());
    }

    /**
     * Obtiene el nombre de usuario a partir del identificador unico para el
     * estandar WebAuthn
     *
     * @param handle Identificador unico del usuario para el estandar WebAuthn
     * @return Nombre de usuario identificado por el handle si existiese.
     * @see UsuarioBean#findByHandle(com.yubico.webauthn.data.ByteArray)
     */
    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray handle) {
        Usuario usuario = uBean.findByHandle(handle);
        return Optional.of(usuario.getNombre());
    }

    /**
     * Busca la credencial de autenticación asociada a un usuario
     *
     * @param identificadorCredencial El identificador de la credencial buscada
     * @param handle El identificador único del usuario para el estándar
     * WebAuthn
     * @return Credencial almacenada si existe
     * @see
     * AutenticadorBean#findByIdentificadorCredencial(com.yubico.webauthn.data.ByteArray,
     * int, int)
     *
     */
    @Override
    public Optional<RegisteredCredential> lookup(ByteArray identificadorCredencial, ByteArray handle) {
        List<Autenticador> autenticadores = aBean.findByIdentificadorCredencial(identificadorCredencial, 0, 1000000);
        return Optional.of(
                autenticadores.stream()
                        .map(credencial -> RegisteredCredential.builder()
                        .credentialId(credencial.getIdentificadorCredencial())
                        .userHandle(credencial.getIdUsuario().getHandle())
                        .publicKeyCose(credencial.getClavePublica())
                        .signatureCount(credencial.getCuenta())
                        .build()
                        ).collect(Collectors.toList()).get(0)
        );

    }

    /**
     * Busca una credencial por su identificador unico para el estandar WebAuthn
     *
     * @param handle Identificador unico de la credencial para el estandar
     * WebAuthn
     * @return Conjunto de Credenciales encontradas
     * @see
     * AutenticadorBean#findByIdentificadorCredencial(com.yubico.webauthn.data.ByteArray,
     * int, int)
     */
    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray handle) {
        List<Autenticador> autenticadores = aBean.findByIdentificadorCredencial(handle, 0, 10000000);
        return autenticadores.stream()
                .map(credencial
                        -> RegisteredCredential.builder()
                        .credentialId(credencial.getIdentificadorCredencial())
                        .userHandle(credencial.getIdUsuario().getHandle())
                        .publicKeyCose(credencial.getClavePublica())
                        .signatureCount(credencial.getCuenta())
                        .build()
                )
                .collect(Collectors.toSet());
    }

}
