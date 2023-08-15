/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.boundary;

import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.UserIdentity;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Random;
import org.penya.webauthn.backendauth.auth.control.RelyingPartySupplier;
import org.penya.webauthn.backendauth.auth.control.UsuarioBean;
import org.penya.webauthn.backendauth.auth.entity.Usuario;

/**
 *
 * @author jcpenya
 */
@Path("registro")
@Named
public class RegistroEndpoint implements Serializable {

    @Inject
    UsuarioBean uBean;
    @Inject
    RelyingPartySupplier rPartySupplier;

    //Registro nuevo usuario
    @POST
    @Path("/{usuario}/{nombre}")
    public Response registroNuevoUsuario(@PathParam(value = "usuario") final String usuario,
            @PathParam(value = "nombre")
            final String nombre,
            @Context HttpServletRequest request) {

//newUserRegistration https://developer.okta.com/blog/2022/04/26/webauthn-java#authentication-controllers
        HttpSession sesion = request.getSession();
        Usuario existe = uBean.findByUsuario(usuario);
        if (existe == null) { // el usuario no existe, se procede a crearlo
            byte[] bytes = new byte[32];
            Random r = new Random();
            r.nextBytes(bytes);
            ByteArray id = new ByteArray(bytes);

            UserIdentity userIdentity = UserIdentity.builder()
                    .name(usuario)
                    .displayName(nombre)
                    .id(id)
                    .build();
            Usuario nuevo = new Usuario(userIdentity);
            uBean.guardar(nuevo);
            return registroNuevaAutenticacion(nuevo, request);
        }
        return Response.status(Response.Status.CONFLICT).header("user-found", usuario).build();

    }

    /**
     * Agrega un nuevo mecanismo de autenticacion a un usuario existente
     *
     * @param usuario
     * @param request
     * @return Estado 404 si el usuario no existe junto con una cabecera
     * user-not-found.
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response registroNuevaAutenticacion(Usuario usuario, @Context HttpServletRequest request) {
        HttpSession sesion = request.getSession();
        Usuario existe = uBean.findByHandle(usuario.getHandle());
        if (existe != null) { // el usuario ya existe, asi que se agrega el nuevo mecanismo de autenticacion
            UserIdentity identidad = usuario.toUserIdentity();
            StartRegistrationOptions opcionesRegistro = StartRegistrationOptions.builder()
                    .user(identidad)
                    .build();
            PublicKeyCredentialCreationOptions registro = rPartySupplier.getRelyingParty().startRegistration(opcionesRegistro);
            sesion.setAttribute("userId", usuario.getNombre());
            sesion.setAttribute("registro", registro);
            try {
                return Response.ok(registro.toCredentialsCreateJson()).build();
            } catch (Exception ex) {
                return Response.status(500).header("sesion-", usuario.getNombre()).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).header("user-not-found", usuario.getNombre()).build();

    }
}
