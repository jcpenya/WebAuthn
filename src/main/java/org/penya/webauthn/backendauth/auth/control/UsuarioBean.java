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
 * Reglas de negocio para Gestion Usuarios. Provee los mecanismos de acceso a
 * datos y logica CRUD para los usuarios.
 *
 * @author jcpenya
 */
@Stateless
@LocalBean
public class UsuarioBean implements Serializable {

    @PersistenceContext(unitName = "BackendAuth-PU")
    EntityManager em;

    /**
     * Busca un usuario por su nombre de usuario
     *
     * @param usuario Nombre de usuario a buscar
     * @return Usuario encontrado o null si no se encuentra.
     * @throws IllegalArgumentException si el nombre de usuario no es valido
     * @throws IllegalStateException si no se puede acceder al repositorio
     * @see Usuario#getUsuario()
     */
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

    /**
     * Busca un usuairo por su identificador unico para el estandar WebAuthn
     *
     * @param handle Identificador unico para el estandar WebAuthn
     * @return Usuario encontrado o null si no se encuentra
     * @throws IllegalArgumentException si el nombre de usuario no es valido
     * @throws IllegalStateException si no se puede acceder al repositorio
     * @see Usuario#getHandle()
     */
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

    /**
     * Almacena un Usuario en el repositorio
     *
     * @param nuevo Usuario a almacenar
     * @throws IllegalArgumentException Si el usuario no es valido y no se puede
     * almacenar
     * @throws IllegalStateException Si no se puede acceder al repositorio
     */
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
