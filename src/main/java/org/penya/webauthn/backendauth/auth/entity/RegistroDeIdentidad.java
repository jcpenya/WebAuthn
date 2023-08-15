/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.entity;

import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.UserIdentity;
import jakarta.json.bind.annotation.JsonbTransient;
import java.time.Instant;
import java.util.Optional;
import java.util.SortedSet;

/**
 *
 * @author jcpenya
 */
@Deprecated
public class RegistroDeIdentidad {

    UserIdentity userIdentity;
    Optional<String> aliasParaCredencial;
    SortedSet<AuthenticatorTransport> transportes;
    Instant horaRegistro;
    Optional<Object> metadatosAtestado;

    public String getNombreUsuario() {
        return userIdentity.getName();
    }

    public String getHoraDeRegistro() {
        return horaRegistro.toString();
    }

    public UserIdentity getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(UserIdentity userIdentity) {
        this.userIdentity = userIdentity;
    }

    public Optional<String> getAliasParaCredencial() {
        return aliasParaCredencial;
    }

    public void setAliasParaCredencial(Optional<String> aliasParaCredencial) {
        this.aliasParaCredencial = aliasParaCredencial;
    }

    public SortedSet<AuthenticatorTransport> getTransportes() {
        return transportes;
    }

    public void setTransportes(SortedSet<AuthenticatorTransport> transportes) {
        this.transportes = transportes;
    }

    @JsonbTransient
    public Instant getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(Instant horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public Optional<Object> getMetadatosAtestado() {
        return metadatosAtestado;
    }

    public void setMetadatosAtestado(Optional<Object> metadatosAtestado) {
        this.metadatosAtestado = metadatosAtestado;
    }

}
