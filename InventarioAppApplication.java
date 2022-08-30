package modelo;

import controlador.ControladorProducto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import vista.VistaInventario;

@SpringBootApplication
public class InventarioAppApplication {

    @Autowired
    RepositorioProducto repositorio;

    public static void main(String[] args) {
        //SpringApplication.run(InventarioAppApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(InventarioAppApplication.class);
        builder.headless(false);
        ConfigurableApplicationContext context =builder.run(args);
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            ControladorProducto controlador = new ControladorProducto(repositorio, new VistaInventario());

        };
    }

}
