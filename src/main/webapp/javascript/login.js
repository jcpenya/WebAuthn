/**
 * Valida las credenciales del usuario mediante WebAuthn
 * @returns void Muestra un error si existiese o navega al URL provisto si la autenticación funciona
 */
async function checkCredentials() { // Autenticara al usuario
    this.form = document.getElementById("form");
    const formData = new FormData(form);
    fetch('/BackendAuth/resources/autenticacion/' + formData.get("username"), {// Obtiene un challenge del servidor para el usuario.
        method: 'GET'
    })
            .then(response => initialCheckStatus(response))
            .then(credentialGetJson => ({
                    publicKey: {
                        ...credentialGetJson.publicKey,
                        allowCredentials: credentialGetJson.publicKey.allowCredentials
                                && credentialGetJson.publicKey.allowCredentials.map(credential => ({
                                        ...credential,
                                        id: base64urlToUint8array(credential.id),
                                    })),
                        challenge: base64urlToUint8array(credentialGetJson.publicKey.challenge),
                        extensions: credentialGetJson.publicKey.extensions,
                    },
                }))
            .then(credentialGetOptions =>
                navigator.credentials.get(credentialGetOptions)) // en base al challenge, obtiene la clave privada del navegador.
            .then(publicKeyCredential => ({
                    type: publicKeyCredential.type,
                    id: publicKeyCredential.id,
                    response: {
                        authenticatorData: uint8arrayToBase64url(publicKeyCredential.response.authenticatorData),
                        clientDataJSON: uint8arrayToBase64url(publicKeyCredential.response.clientDataJSON),
                        signature: uint8arrayToBase64url(publicKeyCredential.response.signature),
                        userHandle: publicKeyCredential.response.userHandle && uint8arrayToBase64url(publicKeyCredential.response.userHandle),
                    },
                    clientExtensionResults: publicKeyCredential.getClientExtensionResults(),
                }))
            .then((encodedResult) => { // intenta autenticar con el desafio generado
                document.getElementById("credential").value = JSON.stringify(encodedResult);
                this.form.action = '/BackendAuth/resources/autenticacion/' + formData.get("username");
                this.form.submit();
            })
            .catch(error => displayError(error))
}