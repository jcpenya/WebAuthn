/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.boundary;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.Serializable;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.penya.webauthn.backendauth.auth.control.AutenticadorBean;
import org.penya.webauthn.backendauth.auth.control.RelyingPartySupplier;
import org.penya.webauthn.backendauth.auth.control.UsuarioBean;
import org.penya.webauthn.backendauth.auth.entity.Autenticador;
import org.penya.webauthn.backendauth.auth.entity.Usuario;

/**
 *
 * @author jcpenya
 */
@Path("autenticacion")
public class AutenticacionEndpoint implements Serializable {
    //TODO: CREAR LOS METODOS PARA LOGIN
    // https://developer.okta.com/blog/2022/04/26/webauthn-java#authentication-controllers

    @Inject
    UsuarioBean uBean;
    @Inject
    AutenticadorBean aBean;
    @Inject
    RelyingPartySupplier rPartySupplier;

    @GET
    @Path("/{usuario}")
    public Response obtenerDesafio(@PathParam("usuario") final String nombreUsuario, @Context HttpServletRequest request) {
        HttpSession sesion = request.getSession();
        Usuario existe = uBean.findByUsuario(nombreUsuario);
        if (existe != null) {
            AssertionRequest peticion = rPartySupplier.
                    getRelyingParty().
                    startAssertion(StartAssertionOptions.builder().username(nombreUsuario).build());
            try {
                sesion.setAttribute("peticion", peticion);
                return Response.status(Response.Status.OK).entity(peticion.toCredentialsGetJson()).type(MediaType.APPLICATION_JSON).build();
//                return Response.ok(peticion.toCredentialsGetJson()).build();
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return Response.status(Response.Status.NOT_FOUND).header("user-not-found", nombreUsuario).build();
    }

    @POST
    @Path("/{usuario}")
    public Response autenticar(@PathParam("usuario") final String nombreUsuario,
            @FormParam("credential") final String credencial,
            @Context HttpServletRequest request,
            @Context UriInfo info) {
        HttpSession sesion = request.getSession();
        if (sesion.getAttribute("peticion") != null) {
            try {
                PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc = PublicKeyCredential.parseAssertionResponseJson(credencial);
                AssertionRequest peticion = (AssertionRequest) sesion.getAttribute("peticion");
                AssertionResult resultado = rPartySupplier.getRelyingParty().finishAssertion(
                        FinishAssertionOptions.builder()
                                .request(peticion)
                                .response(pkc)
                                .build()
                );
                if (resultado.isSuccess()) {
                    sesion.setAttribute("usuario", nombreUsuario); // guardar en sesion
                    Usuario u = uBean.findByUsuario(nombreUsuario);
                    Autenticador aut = aBean.findByUsuario(u, 0, 10000).get(0);
                    aut.setCuenta(aut.getCuenta() + 1l); // aumentar la cuenta de uso
                    aBean.modificar(aut);

                    // enviar a bienvenida 
                    URI uri = info.getBaseUriBuilder().path("../registrado.jsf").build();
                    return Response.status(303).location(uri).build();
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                return Response.status(502).header("registration-failed", nombreUsuario).build();
            }
        }
        return Response.status(400).header("credential-not-saved", credencial).build();
    }

    @DELETE
    @Path("/{usuario}")
    public Response cerrarSesion(@PathParam("usuario") final String nombreUsuario,
            @Context HttpServletRequest request,
            @Context UriInfo info) {
        HttpSession sesion = request.getSession();
        if (sesion.getAttribute("usuario") != null) {
            sesion.invalidate();
        }
        URI uri = info.getBaseUriBuilder().path("../index.html").build();
        return Response.status(303).location(uri).build();

    }

}
