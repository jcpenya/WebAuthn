package org.penya.webauthn.backendauth.auth.entity;

import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor de datos para que las entidades pueden utilizar el tipo ByteArray y
 * en el respositorio puedan almacenarse como byte[].
 *
 * @author jcpenya
 */
@Converter(autoApply = true)
public class ByteArrayConverter implements AttributeConverter<ByteArray, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(ByteArray x) {
        if (x != null) {
            return x.getBytes();
        }
        return null;
    }

    @Override
    public ByteArray convertToEntityAttribute(byte[] y) {
        if (y != null) {
            return new ByteArray(y);
        }
        return null;
    }

}
