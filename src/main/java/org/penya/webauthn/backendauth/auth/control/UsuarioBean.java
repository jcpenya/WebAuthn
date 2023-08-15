/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.control;

import com.yubico.webauthn.data.ByteArray;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.penya.webauthn.backendauth.auth.entity.Usuario;

/**
 *
 * @author jcpenya
 */
@Stateless
@LocalBean
public class UsuarioBean implements Serializable {

    @PersistenceContext(unitName = "BackendAuth-PU")
    EntityManager em;

    public Usuario findByUsuario(final String usuario) {
        if (em != null) {
            try {
                if (usuario != null) {
                    Query q = em.createNamedQuery("Usuario.findByUsuario");
                    q.setFirstResult(0);
                    q.setMaxResults(100000);
                    q.setParameter("usuario", usuario);
                    List resultado = q.getResultList();
                    if (resultado != null && !resultado.isEmpty()) {
                        return (Usuario) resultado.get(0);
                    }
                    return null;
                }
                throw new IllegalArgumentException(usuario);
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                throw new IllegalArgumentException(ex);
            }
        }
        throw new IllegalStateException("No se puede acceder al repositorio");
    }

    public Usuario findByHandle(ByteArray handle) {
        if (em != null) {
            try {
                if (handle != null && !handle.isEmpty()) {
                    Query q = em.createNamedQuery("Usuario.findByHandle");
                    q.setFirstResult(0);
                    q.setMaxResults(100000);
                    q.setParameter("handle", handle);
                    return (Usuario) q.getResultList().get(0);
                }
                throw new IllegalArgumentException("handle invalido");
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                throw new IllegalArgumentException(ex);
            }
        }
        throw new IllegalStateException("No se puede acceder al repositorio");
    }

    public void guardar(Usuario nuevo) throws IllegalArgumentException, IllegalStateException {
        if (em != null) {
            try {
                em.persist(nuevo);
                return;
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                throw new IllegalArgumentException(ex);
            }
        }
        throw new IllegalStateException("No se puede acceder al repositorio");
    }

}
