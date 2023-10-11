// ==================== Ceremonia de Registro ===================== //
/**
 * Realiza el registro de credenciales mediante WebAuthn
 * @type type
 */
document.addEventListener("submit", (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);

    const datos = new URLSearchParams();
    for (const pair of formData) {
        datos.append(pair[0], pair[1]);
    }

    fetch('/BackendAuth/resources/registro/' + formData.get('username') + '/' + formData.get('display'), { // Obtiene los parametros para crear el registro.
        method: 'POST',
        body: datos
    })
            .then(response => initialCheckStatus(response))
            .then(credentialCreateJson => ({// crea la clave publica y los detalles del usuario.
                    publicKey: {
                        ...credentialCreateJson.publicKey,
                        challenge: base64urlToUint8array(credentialCreateJson.publicKey.challenge),
                        user: {
                            ...credentialCreateJson.publicKey.user,
                            id: base64urlToUint8array(credentialCreateJson.publicKey.user.id),
                        },
                        excludeCredentials: credentialCreateJson.publicKey.excludeCredentials.map(credential => ({
                                ...credential,
                                id: base64urlToUint8array(credential.id),
                            })),
                        extensions: credentialCreateJson.publicKey.extensions,
                    },
                }))
            .then(credentialCreateOptions =>
                navigator.credentials.create(credentialCreateOptions))// genera la clave privada en el navegador
            .then(publicKeyCredential => ({
                    type: publicKeyCredential.type,
                    id: publicKeyCredential.id,
                    response: {
                        attestationObject: uint8arrayToBase64url(publicKeyCredential.response.attestationObject),
                        clientDataJSON: uint8arrayToBase64url(publicKeyCredential.response.clientDataJSON),
                        transports: publicKeyCredential.response.getTransports && publicKeyCredential.response.getTransports() || [],
                    },
                    clientExtensionResults: publicKeyCredential.getClientExtensionResults(),
                }))
            .then((encodedResult) => { // generado el par de claves, envia la clave publica al servidor
                const form = document.getElementById("form");
                const formData = new FormData(form);
                formData.append("credential", JSON.stringify(encodedResult));

                const data = new URLSearchParams();
                for (const pair of formData) {
                    data.append(pair[0], pair[1]);
                }
                return fetch("/BackendAuth/resources/registro/finalizar/", {
                    method: 'POST',
                    body: data
                });
            })
            .then((response) => {
                followRedirect(response); // Si se obtiene un estatus positivo se sigue el URL provisto
            })
            .catch((error) => {
                displayError(error);
            });
})