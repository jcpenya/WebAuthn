/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.control;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.HashSet;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author jcpenya
 */
@Named
@ApplicationScoped
public class RelyingPartySupplier implements Serializable {

    @Inject
    @ConfigProperty(name = "nombre_host")
    String nombreHost;

    @Inject
    @ConfigProperty(name = "nombre_aplicacion")
    String nombreAplicacion;

    @Inject
    @ConfigProperty(name = "origen")
    String origen;

    @Inject
    RepositorioCredenciales repositorioCredenciales;

    RelyingParty relyingParty;

    @PostConstruct
    public void inicializar() {
        RelyingPartyIdentity identity = RelyingPartyIdentity.builder().id(nombreHost)
                .name(nombreAplicacion)
                .build();

        HashSet<String> origenes = new HashSet<>();
        origenes.add(origen);

        this.relyingParty = RelyingParty.builder().
                identity(identity)
                .credentialRepository(repositorioCredenciales)
                .origins(origenes)
                .build();
    }

    public RelyingParty getRelyingParty() {
        return relyingParty;
    }

}
