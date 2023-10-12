FROM payara/server-full:6.2023.6-jdk17
USER root
RUN apt update && apt install -y wget
USER payara
RUN wget -O $PAYARA_DIR/glassfish/lib/postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.5.2.jar
RUN echo 'create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property user="${ENV=POSTGRES_USER}":password="${ENV=POSTGRES_PASSWORD}":servername=db:portnumber="${ENV=POSTGRES_PORT}":databasename="${ENV=POSTGRES_DBNAME}"  pg_db' >> $CONFIG_DIR/pre-boot-commands.asadmin \
   	 && echo 'create-jdbc-resource --connectionpoolid pg_db jdbc/webauthn' >> $CONFIG_DIR/pre-boot-commands.asadmin
RUN echo 'deploy /opt/payara/aplicacion.war' >> $CONFIG_DIR/post-boot-commands.asadmin \
    && echo 'ping-connection-pool pg_db' >> $CONFIG_DIR/post-boot-commands.asadmin
