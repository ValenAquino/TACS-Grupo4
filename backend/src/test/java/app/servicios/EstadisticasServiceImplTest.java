package app.servicios;

import app.dto.EstadisticasDto;
import app.model.entities.*;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import app.repositories.RepositorioPerfiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadisticasServiceImplTest {

    @Mock private RepositorioPerfiles repositorioUsuarios;
    @Mock private RepositorioPropuestas repositorioPropuestas;
    @Mock private RepositorioSubastas repositorioSubastas;

    private ServicioEstadisticas service;

    private List<MedioDeContacto> telegram(String numero) {
        return List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero));
    }

    private Perfil perfil(String id, String usuarioId, String nombre) {
        return new Perfil(id, new Usuario(usuarioId, Rol.USUARIO, "lucas", "fiscella"), nombre,
            new Coleccion(), telegram("@" + nombre.toLowerCase()), new ArrayList<>());
    }

    @BeforeEach
    void setUp() {
        service = new ServicioEstadisticas(repositorioUsuarios, repositorioPropuestas, repositorioSubastas);
    }

    @Test
    void getEstadisticas_sinDatos_retornaTodosCeros() {
        when(repositorioUsuarios.contar()).thenReturn(0L);
        when(repositorioUsuarios.buscarTodos()).thenReturn(List.of());
        when(repositorioPropuestas.contar()).thenReturn(0);
        when(repositorioSubastas.buscarTodos()).thenReturn(List.of());
