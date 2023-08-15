package org.penya.webauthn.backendauth.auth.entity;

import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.AttestedCredentialData;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Optional;

/**
 *
 * @author jcpenya
 */
@Entity
@Table(name = "autenticador", schema = "public")
@NamedQueries({
    @NamedQuery(name = "Autenticador.findAll", query = "SELECT a FROM Autenticador a"),
    @NamedQuery(name = "Autenticador.findById", query = "SELECT a FROM Autenticador a WHERE a.id = :id"),
    @NamedQuery(name = "Autenticador.findByCuenta", query = "SELECT a FROM Autenticador a WHERE a.cuenta = :cuenta"),
    @NamedQuery(name = "Autenticador.findByIdUsuario", query = "SELECT a FROM Autenticador a WHERE a.idUsuario.id = :idUsuario"),
    @NamedQuery(name = "Autenticador.findByIdentificadorCredencial", query = "SELECT a FROM Autenticador a WHERE a.identificadorCredencial = :identificadorCredencial")
})
public class Autenticador implements Serializable {

    private static final long serialVersionUID = 1L;

    public Autenticador() {
    }

    public Autenticador(RegistrationResult resultado, AuthenticatorAttestationResponse respuesta, Usuario u, String nombre) {
        Optional<AttestedCredentialData> atestados = respuesta.getAttestation()
                .getAuthenticatorData().getAttestedCredentialData();
        this.identificadorCredencial = resultado.getKeyId().getId();
        this.clavePublica = resultado.getPublicKeyCose();
        this.idAutenticador = atestados.get().getAaguid();
        this.cuenta = resultado.getSignatureCount();
        this.nombre = nombre;
        this.idUsuario = u;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Lob
    @Column(nullable = false,name = "identificador_credencial")
    @Basic(optional = false)
    private ByteArray identificadorCredencial;

    @Lob
    @Column(nullable = false, name = "clave_publica")
    @Basic(optional = false)
    private ByteArray clavePublica;

   

    @Basic(optional = false)
    @Column(nullable = false, name = "cuenta")
    private Long cuenta;

    @Lob
    @Column(nullable = true, name = "id_autenticador")
    private ByteArray idAutenticador;

    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne
    private Usuario idUsuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ByteArray getIdentificadorCredencial() {
        return identificadorCredencial;
    }

    public void setIdentificadorCredencial(ByteArray identificadorCredencial) {
        this.identificadorCredencial = identificadorCredencial;
    }

    public ByteArray getClavePublica() {
        return clavePublica;
    }

    public void setClavePublica(ByteArray clavePublica) {
        this.clavePublica = clavePublica;
    }

    public Long getCuenta() {
        return cuenta;
    }

    public void setCuenta(Long cuenta) {
        this.cuenta = cuenta;
    }

    public ByteArray getIdAutenticador() {
        return idAutenticador;
    }

    public void setIdAutenticador(ByteArray idAutenticador) {
        this.idAutenticador = idAutenticador;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
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
        if (!(object instanceof Autenticador)) {
            return false;
        }
        Autenticador other = (Autenticador) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.penya.webauthn.backendauth.auth.entity.Autenticador[ id=" + id + " ]";
    }

}
