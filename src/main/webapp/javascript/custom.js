// ================ Funciones comunes ==================== //

/**
 * Convierte la representación en texto a Arreglo de Bytes (Blob)
 * @param {Texto} base64Bytes
 * @returns {Blob}
 */
function base64urlToUint8array(base64Bytes) {
    const padding = '===='.substring(0, (4 - (base64Bytes.length % 4)) % 4);
    return base64js.toByteArray((base64Bytes + padding).replace(/\//g, "_").replace(/\+/g, "-"));
}
/**
 * Convierte Arreglo de Bytes (Blob) a la representación en Texto
 * @param {Blob} bytes
 * @returns {Texto}
 */
function uint8arrayToBase64url(bytes) {
    if (bytes instanceof Uint8Array) {
        return base64js.fromByteArray(bytes).replace(/\+/g, "-").replace(/\//g, "_").replace(/=/g, "");
    } else {
        return uint8arrayToBase64url(new Uint8Array(bytes));
    }
}
/**
 * Define un tipo de error para ser mostrado a manera de una excepcion.
 * @type void
 */
class WebAuthServerError extends Error {
    constructor(foo = 'bar', ...params) {
      super(...params)
      this.name = 'ServerError'
      this.foo = foo
      this.date = new Date()
    }
}
/**
 * Notifica error de parte del cliente
 * @param {type} response
 * @returns {undefined}
 */
function throwError(response) {
    throw new WebAuthServerError("Error from client", response.body);
}
/**
 * Valida las respuestas recibidas del servidor
 * @param {Response} response
 * @returns {Response}
 */
function checkStatus(response) {
    if (response.status !== 200) {
        throwError(response);
    } else {
        return response;
    }
}
/**
 * Valida una respusta y devuelve la promesa de JSON.
 * @param {Response} response
 * @returns {Promise}
 */
function initialCheckStatus(response) {
    checkStatus(response);
    return response.json();
}
/**
 * Navega al url provisto
 * @param {Response} response
 * @returns {void}
 */
function followRedirect(response) {
    if (response.status == 200) {
        window.location.href = response.url;
    } else {
        throwError(response);
    }
}
/**
 * Renderiza el error
 * @param {type} error
 * @returns {undefined}
 */
function displayError(error) {
    const errorElem = document.getElementById('errors');
    errorElem.innerHTML = error;
    console.error(error);
}