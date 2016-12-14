package no.acando;

import com.complexible.stardog.api.ConnectionConfiguration;


class Delt {

    static ConnectionConfiguration getConnectionConfiguration(String database) {
        return ConnectionConfiguration
            .to(database)
            .server("http://128.199.32.62:5820")
            .reasoning(true)
            .credentials("admin", "acando");
    }

}
