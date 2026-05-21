package app.repositories.impl;

import app.MongoTestBase;
import app.dto.filtros.PropuestasFiltro;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioPropuestasTest extends MongoTestBase {

    private Perfil u1;
    private Perfil u2;
    private Perfil u3;

    private PropuestasFiltro filtros;

    private List<MedioDeContacto> telegram(String usuario) {
        return List.of(
            new MedioDeContacto(
                MedioComunicacion.TELEGRAM,
                usuario
            )
        );
    }

    @BeforeEach
    void setUp() {

        u1 = Perfil.builder()
            .id(new ObjectId().toString())
            .usuario(new Usuario("u-1", Rol.USUARIO, "lucas", "fiscella"))
            .nombre("Lucas")
            .mediosDeContacto(telegram("@lucas"))
            .build();

        u2 = Perfil.builder()
            .id(new ObjectId().toString())
            .usuario(new Usuario("u-2", Rol.USUARIO, "sofia", "fiscella"))
            .nombre("Sofía")
            .mediosDeContacto(telegram("@sofia"))
            .build();

        u3 = Perfil.builder()
            .id(new ObjectId().toString())
            .usuario(new Usuario("u-3", Rol.USUARIO, "matias", "fiscella"))
            .nombre("Matías")
            .mediosDeContacto(telegram("@matias"))
            .build();

        filtros = new PropuestasFiltro(
            "",
            0, // primera página
            10,
            EstadoProceso.PENDIENTE
        );
    }

    @Test
    void findByOrigenId_retornaSoloPropuestasDelOrigen() {

        Propuesta p1 = new Propuesta(
            "p-1",
            u1,
            u2,
            new ArrayList<>(),
            null,
            List.of(
                new EstadoPropuesta(
                    LocalDateTime.now(),
                    EstadoProceso.PENDIENTE
                )
            )
        );

        Propuesta p2 = new Propuesta(
            "p-2",
            u2,
            u3,
            new ArrayList<>(),
            null,
            List.of(
                new EstadoPropuesta(
                    LocalDateTime.now(),
                    EstadoProceso.PENDIENTE
                )
            )
        );

        repositorioPropuestas.guardar(p1);
        repositorioPropuestas.guardar(p2);

        PaginaResultado<Propuesta> resultado =
            repositorioPropuestas.buscarPorAutorId(
                u1.getId(),
                filtros
            );

        assertEquals(1, resultado.contenido().size());
        assertEquals(
            "p-1",
            resultado.contenido().get(0).getId()
        );
    }

    @Test
    void findByDestinoId_retornaSoloPropuestasDelDestino() {

        Propuesta p1 = new Propuesta(
            "p-1",
            u1,
            u2,
            new ArrayList<>(),
            null,
            List.of(
                new EstadoPropuesta(
                    LocalDateTime.now(),
                    EstadoProceso.PENDIENTE
                )
            )
        );

        Propuesta p2 = new Propuesta(
            "p-2",
            u2,
            u3,
            new ArrayList<>(),
            null,
            List.of(
                new EstadoPropuesta(
                    LocalDateTime.now(),
                    EstadoProceso.PENDIENTE
                )
            )
        );

        repositorioPropuestas.guardar(p1);
        repositorioPropuestas.guardar(p2);

        PaginaResultado<Propuesta> resultado =
            repositorioPropuestas.buscarPorDestinatarioId(
                u2.getId(),
                filtros
            );

        assertEquals(1, resultado.contenido().size());
        assertEquals(
            "p-1",
            resultado.contenido().get(0).getId()
        );
    }

    @Test
    void findByOrigenId_sinResultados_retornaListaVacia() {

        PaginaResultado<Propuesta> resultado =
            repositorioPropuestas.buscarPorAutorId(
                new ObjectId().toString(),
                filtros
            );

        assertTrue(resultado.contenido().isEmpty());
    }
}