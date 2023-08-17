document.addEventListener("submit", (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    fetch('/BackendAuth/resources/registro/'+formData.get('username')+'/'+formData.get('display'), {
        method: 'POST',
        body: formData
    })
    .then(response => initialCheckStatus(response))
    .then(credentialCreateJson => ({
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
        navigator.credentials.create(credentialCreateOptions))
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
    .then((encodedResult) => {
        const form = document.getElementById("form");
        const formData = new FormData(form);
        formData.append("credential", JSON.stringify(encodedResult));
        
        const data=new URLSearchParams();
        for(const pair of formData){
            data.append(pair[0],pair[1]);
        }
        return fetch("/BackendAuth/resources/registro/finalizar/", {
            method: 'POST',
            body: data
        });
    })
    .then((response) => {
        followRedirect(response);
    })
    .catch((error) => {
        displayError(error);
    });
})