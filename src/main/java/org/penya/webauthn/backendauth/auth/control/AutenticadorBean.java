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
 * Reglas de negocio para Gestion de Credenciales de un usuario. Provee los
 * mecanismos de acceso a datos y logica CRUD para las credenciales WebAuthn de
 * un usuario.
 *
 * @author jcpenya
 */
@Stateless
@LocalBean
public class AutenticadorBean implements Serializable {

    @PersistenceContext(unitName = "BackendAuth-PU")
    EntityManager em;

    /**
     * Guarda en el repositorio una nueva credencial
     *
     * @param a Credencial a almacenar
     * @see Autenticador
     */
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

    /**
     * Modifica una credencial almacenada en el repositorio
     *
     * @param a Credencial a modificar
     * @return La credencial modificada
     */
    public Autenticador modificar(Autenticador a) {
        if (a != null) {
            if (em != null) {
                return em.merge(a);
            }
            throw new IllegalStateException();
        }
        throw new IllegalArgumentException();
    }

    /**
     * Busca una credencial en base a su identificador unico para el estandar
     * WebAuthn
     *
     * @param identificadorCredencial Identificador unico para el estandar
     * WebAuthn
     * @param first primer registro a obtener
     * @param pageSize cantidad de registros a obtener
     * @return Lista de credenciales asociadas al identificador.
     */
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

    /**
     * Busca en el repositorio las credenciales asociadas al usuario.
     *
     * @param u Usuario al que pertenecen las credenciales
     * @param first primer registro a obtener
     * @param pageSize cantidad de registros a obtener
     * @return Lista de credenciales asociadas al usuario
     */
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
