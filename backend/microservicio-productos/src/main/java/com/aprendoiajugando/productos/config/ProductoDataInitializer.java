
package com.aprendoiajugando.productos.config;

import com.aprendoiajugando.productos.model.Producto;
import com.aprendoiajugando.productos.repository.ProductoRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductoDataInitializer {

    @Bean
    CommandLineRunner cargarProductos(ProductoRepository repository) {
        return args -> {

            if (repository.count() == 0) {

                repository.save(new Producto(
                        "Historia de la Plaza de Armas",
                        "Patrimonio de Santiago",
                        "Conoce el origen de la Plaza de Armas, su importancia en la fundación de Santiago y los edificios históricos que la rodean.",
                        "Historia y patrimonio",
                        "https://www.youtube.com/watch?v=91S3goTBvYg",
                        true
                ));

                repository.save(new Producto(
                        "Historia del Palacio de La Moneda",
                        "Patrimonio nacional",
                        "Descubre el origen del Palacio de La Moneda, su construcción y su transformación en la sede del Gobierno de Chile.",
                        "Historia de Chile",
                        "https://www.youtube.com/watch?v=phFxjuAwZlY",
                        true
                ));

                repository.save(new Producto(
                        "Descubriendo el Cerro Santa Lucía",
                        "Historia y naturaleza",
                        "Recorre el Cerro Santa Lucía y descubre sus terrazas, monumentos, jardines y su transformación en un paseo público.",
                        "Turismo y patrimonio",
                        "https://www.youtube.com/watch?v=YJVA02qEPXI",
                        true
                ));

                repository.save(new Producto(
                        "Conociendo el Barrio Lastarria",
                        "Cultura e historia urbana",
                        "Explora el Barrio Lastarria, sus calles, arquitectura, espacios culturales y su importancia turística en Santiago.",
                        "Cultura y turismo",
                        "https://www.youtube.com/watch?v=eCWV7mtPFnU",
                        true
                ));

                System.out.println("4 cápsulas educativas cargadas correctamente.");
            }
        };
    }
}
