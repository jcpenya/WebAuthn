/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.penya.webauthn.backendauth.auth.entity;

import com.yubico.webauthn.data.ByteArray;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
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
