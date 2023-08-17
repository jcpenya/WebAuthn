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
import org.penya.webauthn.backendauth.auth.entity.Autenticador;
import org.penya.webauthn.backendauth.auth.entity.Usuario;

/**
 *
 * @author jcpenya
 */
@Stateless
@LocalBean
public class AutenticadorBean implements Serializable {

    @PersistenceContext(unitName = "BackendAuth-PU")
    EntityManager em;

    public void guardar(Autenticador a) {
        if (a != null) {
            if (em != null) {
                em.persist(a);
                return;
            }
            throw new IllegalStateException();
        }
        throw new IllegalArgumentException();
    }

    public List<Autenticador> findByIdentificadorCredencial(ByteArray identificadorCredencial, int first, int pageSize) {
        if (identificadorCredencial != null) {
            if (em != null) {
                try {
                    Query q = em.createNamedQuery("Autenticador.findByIdentificadorCredencial");
                    q.setFirstResult(first);
                    q.setMaxResults(pageSize);
                    q.setParameter("identificadorCredencial", identificadorCredencial);
                    return q.getResultList();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                    throw new IllegalStateException();
                }
            }
            throw new IllegalStateException();
        }
        throw new IllegalArgumentException();

    }

    public List<Autenticador> findByUsuario(Usuario u, int first, int pageSize) {
        if (em != null) {
            try {
                if (u != null && u.getId() != null) {
                    Query q = em.createNamedQuery("Autenticador.findByIdUsuario");
                    q.setFirstResult(first);
                    q.setMaxResults(pageSize);
                    q.setParameter("idUsuario", u.getId());
                    return q.getResultList();
                }
                throw new IllegalArgumentException();
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                throw new IllegalArgumentException(ex);
            }
        }
        throw new IllegalStateException("No se puede acceder al repositorio");
    }

}
