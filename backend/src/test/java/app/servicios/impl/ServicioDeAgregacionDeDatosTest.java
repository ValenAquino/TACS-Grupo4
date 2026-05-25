package app.servicios.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.client.ImagenJugadorProveedor;
import app.model.entities.Figurita;
import app.model.entities.ImagenFigurita;
import app.model.entities.Seleccion;
import app.repositories.RepositorioImagenesFiguritas;
import app.repositories.RepositorioFiguritas;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicioDeAgregacionDeDatosTest {

  @Mock RepositorioImagenesFiguritas repositorioImagenes;
  @Mock ImagenJugadorProveedor imagenProveedor;
  @Mock RepositorioFiguritas repositorioFiguritas;

  @InjectMocks ServicioDeAgregacionDeDatos service;

  Figurita messi = Figurita.builder()
      .id("ARG-10").numero(10).jugador("Messi")
      .seleccion(Seleccion.ARGENTINA).posicion("Delantero").build();

  @Test
  void agregarDatos_noExisteDocumento_llamaProveedorYGuarda() {
    when(repositorioImagenes.iniciarProcesamiento("ARG-10")).thenReturn(null);
    when(imagenProveedor.buscarImagen("Messi"))
        .thenReturn(Optional.of("https://img.example.com/messi.jpg"));

    service.agregarDatos(List.of(messi));

    assertEquals("https://img.example.com/messi.jpg", messi.getImagenUrl());
    verify(repositorioImagenes).guardar(any(ImagenFigurita.class));
    verify(repositorioFiguritas).guardar(messi);
  }

  @Test
  void agregarDatos_noExisteDocumentoYProveedorNoEncuentra_seteaNull() {
    when(repositorioImagenes.iniciarProcesamiento("ARG-10")).thenReturn(null);
    when(imagenProveedor.buscarImagen("Messi")).thenReturn(Optional.empty());

    service.agregarDatos(List.of(messi));

    assertNull(messi.getImagenUrl());
    verify(repositorioImagenes).guardar(any(ImagenFigurita.class));
    verify(repositorioFiguritas).guardar(messi);
  }

  @Test
  void agregarDatos_documentoCompletado_usaCacheYNoLlamaApi() {
    ImagenFigurita completado = new ImagenFigurita("ARG-10",
        "https://img.example.com/messi.jpg", "COMPLETADO", LocalDateTime.now());
    when(repositorioImagenes.iniciarProcesamiento("ARG-10")).thenReturn(completado);

    service.agregarDatos(List.of(messi));

    assertEquals("https://img.example.com/messi.jpg", messi.getImagenUrl());
    verify(imagenProveedor, never()).buscarImagen(any());
    verify(repositorioFiguritas).guardar(messi);
  }

  @Test
  void agregarDatos_enProceso_retomaYEnriquece() {
    ImagenFigurita enProceso = new ImagenFigurita("ARG-10", null, "EN_PROCESO", LocalDateTime.now());
    when(repositorioImagenes.iniciarProcesamiento("ARG-10")).thenReturn(enProceso);
    when(repositorioImagenes.retomarProcesamiento("ARG-10")).thenReturn(enProceso);
    when(imagenProveedor.buscarImagen("Messi"))
        .thenReturn(Optional.of("https://img.example.com/messi.jpg"));

    service.agregarDatos(List.of(messi));

    assertEquals("https://img.example.com/messi.jpg", messi.getImagenUrl());
    verify(imagenProveedor).buscarImagen("Messi");
  }

  @Test
  void agregarDatos_enProceso_otraInstanciaLoToma_seSalta() {
    ImagenFigurita enProceso = new ImagenFigurita("ARG-10", null, "EN_PROCESO", LocalDateTime.now());
    when(repositorioImagenes.iniciarProcesamiento("ARG-10")).thenReturn(enProceso);
    when(repositorioImagenes.retomarProcesamiento("ARG-10")).thenReturn(null);

    service.agregarDatos(List.of(messi));

    assertNull(messi.getImagenUrl());
    verify(imagenProveedor, never()).buscarImagen(any());
    verify(repositorioFiguritas, never()).guardar(any());
  }

  @Test
  void agregarDatos_excepcionInesperada_continuaConSiguienteFigurita() {
    Figurita mbappe = Figurita.builder()
        .id("FRA-10").numero(10).jugador("Mbappé")
        .seleccion(Seleccion.FRANCIA).posicion("Delantero").build();
    when(repositorioImagenes.iniciarProcesamiento("ARG-10"))
        .thenThrow(new RuntimeException("error inesperado"));
    when(repositorioImagenes.iniciarProcesamiento("FRA-10")).thenReturn(null);
    when(imagenProveedor.buscarImagen("Mbappé"))
        .thenReturn(Optional.of("https://img.example.com/mbappe.jpg"));

    service.agregarDatos(List.of(messi, mbappe));

    assertNull(messi.getImagenUrl());
    assertEquals("https://img.example.com/mbappe.jpg", mbappe.getImagenUrl());
  }
}
