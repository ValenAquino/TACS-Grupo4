package app.repositories.impl;

import app.MongoTestBase;
import app.exceptions.NotFoundException;
import app.model.entities.Coleccion;
import app.model.entities.Figurita;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.Rol;
import app.model.entities.Seleccion;
import app.model.entities.Perfil;
import app.model.entities.Usuario;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioPerfilesTest extends MongoTestBase {

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    @Test
    void findById_inexistente_retornaNull() {
        assertThrows(NotFoundException.class, () -> repositorioPerfiles.buscarPorId("inexistente"));
    }

    @Test
    void buscarPorFiguritaFaltanteDevuelve2() {
        Usuario user = new Usuario("u-1000", Rol.USUARIO, "lucas", "fiscella");
        repositorioUsuarios.guardar(user);
        Coleccion coleccion1 = new Coleccion();
        Coleccion coleccion2 = new Coleccion();
        Coleccion coleccion3 = new Coleccion();

        Perfil perfil = Perfil.builder()
            .id("u-1").usuario(user).nombre("Lucas")
            .coleccion(coleccion1)
            .mediosDeContacto(telegram("@lucas"))
            .build();

        user = new Usuario("u-1001", Rol.USUARIO, "lucas", "fiscella");
        repositorioUsuarios.guardar(user);
        Perfil perfil2 = Perfil.builder()
            .id("u-2").usuario(user).nombre("Juan")
            .coleccion(coleccion2)
            .mediosDeContacto(telegram("@juan"))
            .build();


        user = new Usuario("u-1002", Rol.USUARIO, "lucas", "fiscella");
        repositorioUsuarios.guardar(user);

        Perfil perfil3 = Perfil.builder()
            .id("u-4").usuario(user).nombre("Cristina")
            .coleccion(coleccion3)
            .mediosDeContacto(telegram("@cristina"))
            .build();

        Figurita messi   = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
        Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);

        perfil.getColeccion().agregarFaltante(messi);
        perfil.getColeccion().agregarFaltante(diMaria);

        perfil2.getColeccion().agregarFaltante(messi);
        perfil2.getColeccion().agregarFaltante(diMaria);

        perfil3.getColeccion().agregarFaltante(diMaria);

        repositorioColecciones.guardar(coleccion1);
        repositorioColecciones.guardar(coleccion2);
        repositorioColecciones.guardar(coleccion3);

        repositorioPerfiles.guardar(perfil);
        repositorioPerfiles.guardar(perfil2);
        repositorioPerfiles.guardar(perfil3);

        assertEquals(2, repositorioPerfiles.buscarPorFiguritaFaltante(messi).size());
    }
}