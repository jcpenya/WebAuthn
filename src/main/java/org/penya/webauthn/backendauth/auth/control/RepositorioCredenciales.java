/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.control;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.penya.webauthn.backendauth.auth.entity.Autenticador;
import org.penya.webauthn.backendauth.auth.entity.RegistroDeIdentidad;
import org.penya.webauthn.backendauth.auth.entity.Usuario;

/**
 *
 * @author jcpenya
 */
@Named
@ApplicationScoped
public class RepositorioCredenciales implements CredentialRepository, Serializable {

    //https://github.com/Yubico/java-webauthn-server
    //https://developer.okta.com/blog/2022/04/26/webauthn-java
    @Inject
    UsuarioBean uBean;

    @Inject
    AutenticadorBean aBean;

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

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String nombreUsuario) {
        Usuario usuario = uBean.findByUsuario(nombreUsuario);
        return Optional.of(usuario.getHandle());
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray handle) {
        Usuario usuario = uBean.findByHandle(handle);
        return Optional.of(usuario.getNombre());
    }

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
