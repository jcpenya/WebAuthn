/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.boundary;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.penya.webauthn.backendauth.auth.control.AutenticadorBean;
import org.penya.webauthn.backendauth.auth.control.UsuarioBean;
import org.penya.webauthn.backendauth.auth.entity.Autenticador;
import org.penya.webauthn.backendauth.auth.entity.Usuario;

/**
 *
 * @author jcpenya
 */
@Named
@ViewScoped
public class FrmRegistrado implements Serializable {

    @Inject
    FacesContext fc;
    @Inject
    UsuarioBean uBean;
    @Inject
    AutenticadorBean aBean;
    HttpServletRequest request;
    Usuario usuario;

    @PostConstruct
    public void inicializar() {
        this.request = (HttpServletRequest) fc.getExternalContext().getRequest();
        if (request != null && request.getSession() != null && request.getSession().getAttribute("usuario") != null) {
            this.usuario = uBean.findByUsuario(request.getSession().getAttribute("usuario").toString());
            if (usuario != null) {
                this.usuario.setAutenticadorList(aBean.findByUsuario(usuario, 0, 100000));
                if (this.getPoseeAutenticadores()) {
                    return;
                }
            }
        }
        try {
            fc.getExternalContext().redirect("index.html");
        } catch (IOException ex) {
            Logger.getLogger(FrmRegistrado.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean getPoseeAutenticadores() {
        return this.usuario != null && this.usuario.getAutenticadorList() != null && !this.usuario.getAutenticadorList().isEmpty();
    }

}
