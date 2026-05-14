package app.repositories.impl;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class RepositorioPerfilesEnMemoriaTest {

    private RepositorioPerfilesEnMemoria repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioPerfilesEnMemoria();
    }

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    @Test
    void save_y_findById_retornaPerfil() {
        Usuario user = new Usuario("u-1000",  Rol.USUARIO, "lucas", "fiscella");
        Perfil perfil = Perfil.builder()
            .id("u-1").usuario(user).nombre("Lucas")
            .mediosDeContacto(telegram("@lucas"))
            .build();

        repositorio.guardar(perfil);

        assertEquals(perfil, repositorio.buscarPorId("u-1"));
    }

    @Test
    void findById_inexistente_retornaNull() {
        assertThrows(NotFoundException.class, () -> repositorio.buscarPorId("inexistente"));
    }

    @Test
    void buscarPorFiguritaFaltanteDevuelve2() {
        Usuario user = new Usuario("u-1000", Rol.USUARIO, "lucas", "fiscella");
        Perfil perfil = Perfil.builder()
            .id("u-1").usuario(user).nombre("Lucas")
            .mediosDeContacto(telegram("@lucas"))
            .build();

        user = new Usuario("u-1001", Rol.USUARIO, "lucas", "fiscella");
        Perfil perfil2 = Perfil.builder()
            .id("u-2").usuario(user).nombre("Juan")
            .mediosDeContacto(telegram("@juan"))
            .build();

        user = new Usuario("u-1002", Rol.USUARIO, "lucas", "fiscella");
        Perfil perfil3 = Perfil.builder()
            .id("u-4").usuario(user).nombre("Cristina")
            .mediosDeContacto(telegram("@cristina"))
            .build();

        Figurita messi   = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, null);
        Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, null);

        perfil.getColeccion().agregarFaltante(messi);
        perfil.getColeccion().agregarFaltante(diMaria);

        perfil2.getColeccion().agregarFaltante(messi);
        perfil2.getColeccion().agregarFaltante(diMaria);

        perfil3.getColeccion().agregarFaltante(diMaria);

        repositorio.guardar(perfil);
        repositorio.guardar(perfil2);
        repositorio.guardar(perfil3);

        assertEquals(2, repositorio.buscarPorFiguritaFaltante(messi).size());
    }
}