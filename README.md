# Webauthn
Prototipo de prueba del estándar WebAuthn mediante una aplicación Jakarta Enterprise 10
___
Este prototipo fue desarrollado como parte de la investigación *"Análisis de factibilidad para la implementación de autenticación Webauthn en aplicaciones web para el mercado salvadoreño".*
Utiliza la librería creada por Yubico, y cuyo ejemplo de uso puede ser consultado en [https://github.com/Yubico/java-webauthn-server](https://github.com/Yubico/java-webauthn-server), pero difiere sustancialmente en algunos elementos:
- Utiliza Maven en lugar de Gradle para gestionar depedendencias y construir el proyecto
- Usa Jakarta Enterprise 10 y Eclipse Microprofile en lugar de Spring
- El cliente es expuesto en una combinación de JSF y HTML+Javascript

## Archivos importantes
### webauthn.sql
DDL para crear la base de datos. Ya que la aplicación utilizó Postgresql como Base de Datos, por defecto asigna como dueño de los objetos creados al usuario postgres, sin embargo puede limpiarse la asignación de roles y fácilmente puede adaptarse al motor de datos de preferencia, siempre y cuando soporte almacenaje de datos tipos blob
### web.xml
Dentro de Jakarta Enterprise es el archivo que define como se despliega una aplicación. Puede cambiar el cambiar el context-param a Development o Production si necesita o no hacer debug de JSF.
### RepositorioCredenciales.java
Es el corazon de la autenticación WebAuthn para la librería de Yubico, ya que abstrae las búsquedas en la base de datos para información de autenticación [https://developers.yubico.com/java-webauthn-server/](https://developers.yubico.com/java-webauthn-server/).
### RelyingPartySupplier.java
Provee un único RelyingParty para toda la aplicacion y brinda mecanismos para acceder a el. Los detalles 
de la instancia se configuran mediante Variables de Entorno y por defecto mediante el archivo de configuracion
META-INF/microprofile-config.properties. Puede leer acerca de la precedencia de configuración en [Microprofile Config](https://microprofile.io/specifications/microprofile-config/).
### microprofile-config.properties
Almacena los valores por defecto para instanciar un RelyingParty. Estos deben ajustarse al desplegar la aplicación
para que coincidan con el dominio y certificado de seguridad utilizado, de lo contrario la autenticación fallará.

![Error de dominio Webauthn](https://www.penya.org/error_dominio_webauthn.jpg)
## Compilacion
Luego de obtener una copia del repositorio, puede compilarse mediante Maven, estando dentro del directorio que contiene al archivo pom.xml, mediante el comando:  
```  
$ mvn clean package  
```  
Esto generara un archivo WAR dentro del directorio target. Este archivo es el artefacto que deberá desplegarse en un sevidor de aplicaciones Jakarta Enterprise 10
## Despliegue
Durante el desarrollo se utilizó como servidor de aplicaciones Payara Community Edition 6.2023.6, por lo que las instrucciones a continuación aplican a dicho servidor.  
Si utiliza un servidor distinto deberá adaptar los pasos para el mismo
### Despliegue tradicional
El despliegue supone tener instalado JDK 17 (La versión LTS al momento de desarrollar el prototipo), un editor de texto y alguna utilidad para descomprimir archivos ZIP.
- Obtener el servidor
    - El servidor puede ser descargado desde la página oficial de Payara [https://www.payara.fish/downloads/payara-platform-community-edition/](https://www.payara.fish/downloads/payara-platform-community-edition/).
- Descomprimir el servidor
    - El servidor se oferta como un archivo ZIP, por tanto deberá descomprimirlo en algún lugar del sistema operativo donde puede ejecutarse. Por ejemplo en */usr/local/payara/payara6.2023.6/*
- Obtener el driver JDBC para la base de datos
    - Este paso depende del motor de datos usado. Durante el desarrollo se usó Postgres, por lo que dicho driver puede obtenerse en su versión 4.2 desde [https://jdbc.postgresql.org/download/](https://jdbc.postgresql.org/download/).
    - La instalación del driver consiste en copiar el archivo JAR descargado al directorio dentro de payara en *glassfish/lib/*, por ejemplo dentro de */usr/local/payara/payara6.2023.6/glassfish/lib/*
- Configurar variables de entorno (Paso opcional)  
    - En caso de sobreescribir los valores por defecto almacenados en el archivo *microprofile-config.java* tal como se mencionó previamente
- Arrancar el servidor
    - Dentro del directorio de payara, ingrese a bin/, por ejemplo */usr/local/payara/payara6.2023.6/bin/*
    - Desde ahi ejecute desde una terminal o cmd *asadmin start-domain* o *asadmin.bat start-domain* dependiendo si se encuentra en un ambiente Linux/Mac o Windows. Esto deberá generar un mensaje de éxito. De lo contrario deberá corregirse el error ants de continuar.
- Crear el un Pool de Conexiones y un Recurso
    - Para conectarse al servidor de base de datos, al menos requiere la siguiente información: usuario, contraseña, servidor, puerto y base de datos. Dicha información se puede exportar como variables de entorno o escribirla directamente
        - POSTGRES_USER: nombre del usuario para conectar a la base de datos
        - POSTGRES_PASSWORD: contraseña del usuario para conectar a la base de datos
        - POSTGRES_PORT: puerto donde escucha el servidor de bases de datos esperando peticiones. Por defecto Postgresql utiliza el puerto 5432.
        - POSTGRES_DBNAME: el nombre de la base de datos a la que se conectará.
        - db: Dirección IP o FQDN del servidor donde se encuentra funcionando el servidor de bases de datos.
    - Para crear el pool de conexiones ejecute *asadmin create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property user="${ENV=POSTGRES_USER}":password="${ENV=POSTGRES_PASSWORD}":servername=db:portnumber="${ENV=POSTGRES_PORT}":databasename="${ENV=POSTGRES_DBNAME}"  pg_db*
    - Para crear el recurso JDBC que expone el pool, ejecute *create-jdbc-resource --connectionpoolid pg_db jdbc/webauthn*
- Desplegar el artefacto
    - Para desplegar el artefacto WAR generado mediante la compilación, necesita su ruta completa en el sistema operativo.
    - Ejecute *asadmin deploy RutaCompletaAlArchivoWAR*
### Mediante Docker
Dentro de este repositorio se incluye un archivo Dockerfile con el siguiente contenido:
```  
FROM payara/server-full:6.2023.6-jdk17  
USER root  
RUN apt update && apt install -y wget  
USER payara  
RUN wget -O $PAYARA_DIR/glassfish/lib/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.5.2.jar  
RUN echo 'create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property user="${ENV=POSTGRES_USER}":password="${ENV=POSTGRES_PASSWORD}":servername=db:portnumber="${ENV=POSTGRES_PORT}":databasename="${ENV=POSTGRES_DBNAME}"  pg_db' >> $CONFIG_DIR/pre-boot-commands.asadmin \  
   	 && echo 'create-jdbc-resource --connectionpoolid pg_db jdbc/webauthn' >> $CONFIG_DIR/pre-boot-commands.asadmin  
RUN echo 'deploy /opt/payara/aplicacion.war' >> $CONFIG_DIR/post-boot-commands.asadmin \  
    && echo 'ping-connection-pool pg_db' >> $CONFIG_DIR/post-boot-commands.asadmin    
```  
El siguiente paso es crear una imagen, y puede crearla usando el siguiente comando  
```  
$ docker build -t payara/full_pg:6.2023.6 ./
```
Ahora necesitará alguna información para lanzar el contenedor. Primero son las mismas variables de entorno y dirección IP para el servidor de bases de datos. Además la ruta a donde se encuentra el artefacto generado, expresado de forma relativa. Con esta información a mano puede lanzarse el contenedor:
```  
$ docker run --rm -p 8080:8080 --name payara -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=contraseniapostgres -e POSTGRES_DBNAME=webauthn -e POSTGRES_PORT=5432 --add-host=db:192.168.1.4 -v ~/webauthn/target/BackendAuth-1.0-SNAPSHOT.war:/opt/payara/aplicacion.war  payara/full_pg:6.2023.6
```  

