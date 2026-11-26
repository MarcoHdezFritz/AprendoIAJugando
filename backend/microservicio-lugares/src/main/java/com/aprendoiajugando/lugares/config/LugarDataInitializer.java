
package com.aprendoiajugando.lugares.config;

import com.aprendoiajugando.lugares.model.Lugar;
import com.aprendoiajugando.lugares.repository.LugarRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LugarDataInitializer {

    @Bean
    CommandLineRunner cargarLugares(LugarRepository repository) {
        return args -> {

            if (repository.count() == 0) {

                repository.save(new Lugar(
                        "Plaza de Armas",
                        "Punto turístico ubicado en el centro histórico de Santiago.",
                        -33.4372,
                        -70.6506,
                        null,
                        "QR-PLAZA-ARMAS",
                        1L,
                        true
                ));

                repository.save(new Lugar(
                        "Palacio de La Moneda",
                        "Sede del Gobierno de Chile y lugar histórico de Santiago.",
                        -33.4429,
                        -70.6536,
                        null,
                        "QR-LA-MONEDA",
                        2L,
                        true
                ));
            }
        };
    }
}
