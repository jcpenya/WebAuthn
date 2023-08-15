package org.penya.webauthn.backendauth.auth.entity;

import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.UserIdentity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jcpenya
 */
@Entity
@Table(name = "usuario", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Usuario.findByNombre", query = "SELECT u FROM Usuario u WHERE u.nombre = :nombre"),
    @NamedQuery(name = "Usuario.findByUsuario", query = "SELECT u FROM Usuario u WHERE u.usuario = :usuario"),
    @NamedQuery(name = "Usuario.findByHandle", query = "SELECT u FROM Usuario u WHERE u.handle = :handle")
})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_usuario", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "usuario", nullable = false, length = 155)
    @Size(min = 3, max = 155)
    @NotBlank
    private String usuario;

    @Basic(optional = false)
    @Column(name = "nombre", nullable = false, length = 255)
    @Size(min = 3, max = 255)
    @NotBlank
    private String nombre;

    @Lob
    @Column(name = "handle", nullable = false, length = 64)
    private ByteArray handle;

    @OneToMany(mappedBy = "idUsuario")
    private List<Autenticador> autenticadorList;

    public Usuario() {
    }

    public Usuario(UserIdentity u) {
        this.usuario = u.getName();
        this.nombre = u.getDisplayName();
        this.handle = u.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ByteArray getHandle() {
        return handle;
    }

    public void setHandle(ByteArray handle) {
        this.handle = handle;
    }

    public UserIdentity toUserIdentity() {
        return UserIdentity.builder()
                .name(getUsuario())
                .displayName(getNombre())
                .id(getHandle())
                .build();
    }

    public List<Autenticador> getAutenticadorList() {
        return autenticadorList;
    }

    public void setAutenticadorList(List<Autenticador> autenticadorList) {
        this.autenticadorList = autenticadorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.penya.webauthn.backendauth.auth.entity[ id=" + id + " ]";
    }

}
