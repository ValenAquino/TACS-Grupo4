package app.config;

import app.model.entities.Calificacion;
import app.model.entities.Coleccion;
import app.model.entities.EstadoProceso;
import app.model.entities.EstadoPropuesta;
import app.model.entities.Figurita;
import app.model.entities.FiguritaIntercambiable;
import app.model.entities.MedioComunicacion;
import app.model.entities.MedioDeContacto;
import app.model.entities.MetodoIntercambio;
import app.model.entities.Perfil;
import app.model.entities.Propuesta;
import app.model.entities.Rol;
import app.model.entities.Seleccion;
import app.model.entities.Subasta;
import app.model.entities.Usuario;
import app.repositories.RepositorioCalificacion;
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import app.repositories.RepositorioUsuarios;
import app.repositories.impl.campos.CamposPerfil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class InicializadorDeDatos implements CommandLineRunner {

  private final RepositorioPerfiles perfiles;
  private final RepositorioPropuestas propuestas;
  private final RepositorioSubastas subastas;
  private final RepositorioFiguritas figuritas;
  private final RepositorioColecciones colecciones;
  private final RepositorioUsuarios usuarios;
  private final RepositorioCalificacion calificaciones;

  public InicializadorDeDatos(RepositorioPerfiles perfiles,
                              RepositorioPropuestas propuestas,
                              RepositorioSubastas subastas,
                              RepositorioColecciones colecciones,
                              RepositorioFiguritas figuritas,
                              RepositorioUsuarios usuarios,
                              RepositorioCalificacion calificaciones) {
    this.perfiles = perfiles;
    this.propuestas = propuestas;
    this.subastas = subastas;
    this.colecciones = colecciones;
    this.figuritas = figuritas;
    this.usuarios = usuarios;
    this.calificaciones = calificaciones;
  }

  private List<MedioDeContacto> telegram(String numero) {
    return new ArrayList<>(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero)));
  }

  private Propuesta propuesta(String id, Perfil autor, Perfil destino,
                              List<Figurita> figuritas, Figurita buscada, EstadoProceso estado) {
    EstadoPropuesta estadoActual = new EstadoPropuesta(LocalDateTime.now(), estado);
    return Propuesta.builder()
        .id(id)
        .autor(autor)
        .destinatario(destino)
        .figuritasOfrecidas(figuritas)
        .figuritaBuscada(buscada)
        .estado(new ArrayList<>(List.of(estadoActual)))
        .estadoActual(estadoActual)
        .build();
  }

  @Override
  public void run(String... args) {
    Figurita messi     = new Figurita("ARG-10", 10, "Messi",     Seleccion.ARGENTINA, "Delantero");
    Figurita diMaria   = new Figurita("ARG-11", 11, "Di María",  Seleccion.ARGENTINA, "Extremo");
    Figurita lautaro   = new Figurita("ARG-9",   9, "Lautaro",   Seleccion.ARGENTINA, "Delantero");
    Figurita mbappe    = new Figurita("FRA-10", 10, "Mbappé",    Seleccion.FRANCIA,   "Delantero");
    Figurita griezmann = new Figurita("FRA-7",  27, "Griezmann", Seleccion.FRANCIA,   "Mediocampista");
    Figurita vinicius  = new Figurita("BRA-10", 30, "Vinicius",  Seleccion.BRASIL,    "Extremo");
    Figurita pedri     = new Figurita("ESP-10", 31, "Pedri",     Seleccion.ESPAÑA,    "Mediocampista");
    Figurita kroos     = new Figurita("GER-8",  40, "Kroos",     Seleccion.ALEMANIA,  "Mediocampista");
    Figurita neymar    = new Figurita("BRA-11", 58, "Neymar",    Seleccion.BRASIL,    "Delantero");

    figuritas.guardar(messi);
    figuritas.guardar(diMaria);
    figuritas.guardar(lautaro);
    figuritas.guardar(mbappe);
    figuritas.guardar(griezmann);
    figuritas.guardar(vinicius);
    figuritas.guardar(pedri);
    figuritas.guardar(kroos);
    figuritas.guardar(neymar);

    cargarPerfiles(messi, diMaria, lautaro, mbappe, griezmann, vinicius, pedri, kroos, neymar);
    cargarCalificaciones();
    cargarPropuestas(messi, diMaria, griezmann, mbappe, vinicius);
    cargarSubastas(griezmann, vinicius, pedri, kroos, neymar, mbappe, diMaria, messi, lautaro);
    cargarFiguritasExtra();
  }

  private void cargarFiguritasExtra() {
    String[][] jugadores = {
        {"ARG", "ARGENTINA", "Delantero", "Almada", "Defensor", "Acuña", "Delantero", "Dybala", "Mediocampista", "Mac Allister", "Defensor", "Molina", "Defensor", "Otamendi", "Mediocampista", "Palacios", "Arquero", "Rulli"},
        {"BRA", "BRASIL", "Delantero", "Endrick", "Defensor", "Éder Militão", "Mediocampista", "Gerson", "Defensor", "Marquinhos", "Mediocampista", "Paquetá", "Extremo", "Raphinha", "Extremo", "Rodrygo", "Extremo", "Savinho"},
        {"FRA", "FRANCIA", "Mediocampista", "Camavinga", "Extremo", "Coman", "Extremo", "Dembélé", "Defensor", "Koundé", "Arquero", "Lloris", "Arquero", "Maignan", "Mediocampista", "Rabiot", "Mediocampista", "Tchouaméni"},
        {"ESP", "ESPAÑA", "Defensor", "Cucurella", "Mediocampista", "Dani Olmo", "Mediocampista", "Fabián Ruiz", "Extremo", "Ferran Torres", "Defensor", "Laporte", "Defensor", "Le Normand", "Arquero", "Navas", "Extremo", "Yamal"},
        {"GER", "ALEMANIA", "Extremo", "Gnabry", "Mediocampista", "Havertz", "Mediocampista", "Kimmich", "Mediocampista", "Musiala", "Arquero", "Neuer", "Defensor", "Rüdiger", "Extremo", "Sané", "Mediocampista", "Wirtz"}
    };
    MetodoIntercambio[] metodos = {MetodoIntercambio.INTERCAMBIO, MetodoIntercambio.SUBASTA};
    String[] perfilIds = {"1000", "1001", "1002", "1003"};

    int contador = 0;
    for (String[] sel : jugadores) {
      String prefix = sel[0];
      Seleccion seleccion = Seleccion.valueOf(sel[1]);
      for (int i = 0; i < 8; i++) {
        String id = prefix + "-" + (i + 1);
        String posicion = sel[2 + i * 2];
        String nombre = sel[3 + i * 2];
        Figurita fig = new Figurita(id, i + 1, nombre, seleccion, posicion);
        figuritas.guardar(fig);
        contador++;
      }
    }
  }

  private void cargarPerfiles(Figurita messi, Figurita diMaria, Figurita lautaro,
                              Figurita mbappe, Figurita griezmann, Figurita vinicius,
                              Figurita pedri, Figurita kroos, Figurita neymar) {
    // Juan
    // faltantes: Griezmann (subasta 8), Kroos
    // repetidas: Pedri, Kroos (para ofertar en subasta 8)
    Coleccion coleccionJuan = new Coleccion();
    coleccionJuan.getFaltantes().add(griezmann);
    coleccionJuan.getFaltantes().add(kroos);
    colecciones.guardar(coleccionJuan);
    Usuario userJuan = new Usuario("u-1003", Rol.USUARIO, "juan_jose", "una contrasenia");
    usuarios.guardar(userJuan);
    Perfil juan = Perfil.builder()
        .id("1003").usuario(userJuan)
        .nombre("Juan").coleccion(coleccionJuan)
        .mediosDeContacto(telegram("@juan")).build();
    perfiles.guardar(juan);
    coleccionJuan.getRepetidas().add(new FiguritaIntercambiable(pedri, 2, List.of(MetodoIntercambio.INTERCAMBIO), juan.getId()));
    coleccionJuan.getRepetidas().add(new FiguritaIntercambiable(kroos, 2, List.of(MetodoIntercambio.SUBASTA),     juan.getId()));
    colecciones.guardar(coleccionJuan);

    // Lucas
    // faltantes: Messi, Lautaro (subasta 3), Pedri (subasta 7), Vinicius
    // repetidas: DiMaria (subasta 3), Griezmann (subasta 7), Mbappe, Kroos
    Coleccion coleccionLucas = new Coleccion();
    coleccionLucas.getFaltantes().add(messi);
    coleccionLucas.getFaltantes().add(lautaro);
    coleccionLucas.getFaltantes().add(pedri);
    coleccionLucas.getFaltantes().add(vinicius);
    colecciones.guardar(coleccionLucas);
    Usuario userLucas = new Usuario("u-1000", Rol.USUARIO, "lucas_fis", "gordo123");
    usuarios.guardar(userLucas);
    Perfil lucas = Perfil.builder()
        .id("1000").usuario(userLucas)
        .nombre("lucas").coleccion(coleccionLucas)
        .mediosDeContacto(telegram("@lucas")).build();
    perfiles.guardar(lucas);
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(diMaria,   2, 0, List.of(MetodoIntercambio.SUBASTA),     lucas.getId()));
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(griezmann, 2, 0, List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId()));
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(mbappe,    3, 0, List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId()));
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(kroos,     2, 0, List.of(MetodoIntercambio.SUBASTA),     lucas.getId()));
    colecciones.guardar(coleccionLucas);

    // Sofía
    // faltantes: Griezmann (subasta 7 → la recibe), Kroos (subasta 8), Messi, Lautaro
    // repetidas: Mbappe, Neymar, Griezmann (para ofertar en subasta 7), Pedri
    Coleccion coleccionSofia = new Coleccion();
    coleccionSofia.getFaltantes().add(messi);
    coleccionSofia.getFaltantes().add(lautaro);
    coleccionSofia.getFaltantes().add(griezmann);
    coleccionSofia.getFaltantes().add(kroos);
    colecciones.guardar(coleccionSofia);
    Usuario userSofia = new Usuario("u-1001", Rol.USUARIO, "sofia_ape", "password");
    usuarios.guardar(userSofia);
    Perfil sofia = Perfil.builder()
        .id("1001").usuario(userSofia)
        .nombre("Sofía").coleccion(coleccionSofia)
        .mediosDeContacto(telegram("@sofia")).build();
    perfiles.guardar(sofia);
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(mbappe,    2, List.of(MetodoIntercambio.INTERCAMBIO), sofia.getId()));
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(neymar,    1, List.of(MetodoIntercambio.INTERCAMBIO), sofia.getId()));
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(pedri,     2, List.of(MetodoIntercambio.SUBASTA),     sofia.getId()));
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(vinicius,  1, List.of(MetodoIntercambio.SUBASTA),     sofia.getId()));
    colecciones.guardar(coleccionSofia);

    // Matías
    // faltantes: DiMaria (subasta 3), Pedri, Kroos
    // repetidas: Vinicius, Messi, Lautaro (para ofertar en subasta 3)
    Coleccion coleccionMatias = new Coleccion();
    coleccionMatias.getFaltantes().add(diMaria);
    coleccionMatias.getFaltantes().add(pedri);
    coleccionMatias.getFaltantes().add(kroos);
    colecciones.guardar(coleccionMatias);
    Usuario userMatias = new Usuario("u-1002", Rol.USUARIO, "mati_crim", "wordpass");
    usuarios.guardar(userMatias);
    Perfil matias = Perfil.builder()
        .id("1002").usuario(userMatias)
        .nombre("Matías").coleccion(coleccionMatias)
        .mediosDeContacto(telegram("@matias")).build();
    perfiles.guardar(matias);
    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(vinicius, 1, List.of(MetodoIntercambio.INTERCAMBIO), matias.getId()));
    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(messi,    2, List.of(MetodoIntercambio.INTERCAMBIO), matias.getId()));
    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(lautaro,  2, List.of(MetodoIntercambio.INTERCAMBIO), matias.getId()));
    colecciones.guardar(coleccionMatias);
  }

  private void cargarCalificaciones() {
    CamposPerfil sinCampos = new CamposPerfil(false);
    Perfil lucas  = perfiles.buscarPorId("1000", sinCampos);
    Perfil sofia  = perfiles.buscarPorId("1001", sinCampos);
    Perfil matias = perfiles.buscarPorId("1002", sinCampos);
    Perfil juan   = perfiles.buscarPorId("1003", sinCampos);
    if (lucas == null || sofia == null || matias == null || juan == null) return;

    // Lucas: recibe 5 y 4 → promedio 4.5 → 5 estrellas
    calificaciones.guardar(new Calificacion("C-1", sofia,  lucas, 5, "Excelente trato, muy rápido",    "2000", MetodoIntercambio.INTERCAMBIO));
    calificaciones.guardar(new Calificacion("C-2", matias, lucas, 4, "Todo bien, lo recomiendo",       "2002", MetodoIntercambio.INTERCAMBIO));

    // Sofía: recibe 4, 3 y 4 → promedio 3.67 → 4 estrellas
    calificaciones.guardar(new Calificacion("C-3", lucas,  sofia, 4, "Buena experiencia",              "2000", MetodoIntercambio.INTERCAMBIO));
    calificaciones.guardar(new Calificacion("C-4", matias, sofia, 3, "Normal, sin problemas",          "2001", MetodoIntercambio.INTERCAMBIO));
    calificaciones.guardar(new Calificacion("C-5", juan,   sofia, 4, "Respondió rápido",               "3000", MetodoIntercambio.SUBASTA));

    // Matías: recibe 2 y 3 → promedio 2.5 → 3 estrellas
    calificaciones.guardar(new Calificacion("C-6", lucas,  matias, 2, "Tardó bastante en responder",   "2002", MetodoIntercambio.INTERCAMBIO));
    calificaciones.guardar(new Calificacion("C-7", sofia,  matias, 3, "Aceptable",                     "2001", MetodoIntercambio.INTERCAMBIO));

    // Juan: recibe 1 y 2 → promedio 1.5 → 2 estrellas
    calificaciones.guardar(new Calificacion("C-8", lucas,  juan, 1, "No cumplió con el intercambio",   "3001", MetodoIntercambio.SUBASTA));
    calificaciones.guardar(new Calificacion("C-9", sofia,  juan, 2, "Mala comunicación",               "3000", MetodoIntercambio.SUBASTA));
  }

  private void cargarPropuestas(Figurita messi, Figurita diMaria,
                                Figurita griezmann, Figurita mbappe, Figurita vinicius) {
    CamposPerfil sinCampos = new CamposPerfil(false);
    Perfil lucas  = perfiles.buscarPorId("1000", sinCampos);
    Perfil sofia  = perfiles.buscarPorId("1001", sinCampos);
    Perfil matias = perfiles.buscarPorId("1002", sinCampos);

    propuestas.guardar(propuesta("2000", lucas,  sofia,  List.of(messi),     mbappe,   EstadoProceso.PENDIENTE));
    propuestas.guardar(propuesta("2001", sofia,  matias, List.of(griezmann), vinicius, EstadoProceso.ACEPTADO));
    propuestas.guardar(propuesta("2002", matias, lucas,  List.of(vinicius),  diMaria,  EstadoProceso.RECHAZADO));
  }

  private void cargarSubastas(Figurita messi, Figurita diMaria, Figurita lautaro,
                              Figurita mbappe, Figurita griezmann, Figurita vinicius,
                              Figurita pedri, Figurita kroos, Figurita neymar) {
    CamposPerfil sinCampos = new CamposPerfil(false);
    Perfil lucas  = perfiles.buscarPorId("1000", sinCampos);
    Perfil sofia  = perfiles.buscarPorId("1001", sinCampos);
    Perfil matias = perfiles.buscarPorId("1002", sinCampos);
    Perfil juan   = perfiles.buscarPorId("1003", sinCampos);
    if (lucas == null) throw new RuntimeException("Lucas es null");
    if (sofia == null) throw new RuntimeException("Sofía es null");
    if (matias == null) throw new RuntimeException("Matías es null");
    if (juan == null) throw new RuntimeException("Juan es null");

    // ─── MIS SUBASTAS (autor = Lucas) ────────────────────────────────────────

    // id=1 | Activa, cierra en ~45 min, 3 ofertas
    Propuesta ofertaSofia = Propuesta.builder()
        .id("o1").autor(sofia).destinatario(lucas)
        .figuritasOfrecidas(List.of(neymar, vinicius))
        .figuritaBuscada(mbappe).build();
    Propuesta ofertaMatias = Propuesta.builder()
        .id("o2").autor(matias).destinatario(lucas)
        .figuritasOfrecidas(List.of(pedri, kroos))
        .figuritaBuscada(mbappe).build();
    Propuesta ofertaJuan = Propuesta.builder()
        .id("o3").autor(juan).destinatario(lucas)
        .figuritasOfrecidas(List.of(griezmann, lautaro))
        .figuritaBuscada(mbappe).build();
    ofertaSofia.seleccionar(lucas.getId());
    subastas.guardar(Subasta.builder()
        .id("1").autor(lucas)
        .fechaInicio(LocalDateTime.now())
        .fechaCierre(LocalDateTime.now().plusMinutes(45))
        .figuritaSubastada(mbappe)
        .ofertas(new ArrayList<>(List.of(ofertaSofia, ofertaMatias, ofertaJuan)))
        .build());

    // id=2 | Activa, cierra en 2 días, sin ofertas
    subastas.guardar(Subasta.builder()
        .id("2").autor(lucas)
        .fechaInicio(LocalDateTime.now())
        .fechaCierre(LocalDateTime.now().plusDays(2))
        .figuritaSubastada(pedri).build());

    // id=3 | Finalizada hace 2 días, ganador: matias, sin calificar
    EstadoPropuesta aceptado3 = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.ACEPTADO);
    Propuesta ofertaGanadora3 = Propuesta.builder()
        .id("o4").autor(matias).destinatario(lucas)
        .figuritasOfrecidas(List.of(messi, lautaro))
        .figuritaBuscada(diMaria)
        .estado(new ArrayList<>(List.of(aceptado3)))
        .estadoActual(aceptado3)
        .build();
    subastas.guardar(Subasta.builder()
        .id("3").autor(lucas)
        .fechaInicio(LocalDateTime.now().minusDays(2))
        .fechaCierre(LocalDateTime.now().minusMinutes(1))
        .figuritaSubastada(diMaria)
        .ofertas(new ArrayList<>(List.of(ofertaGanadora3))).build());

    // id=7 | Finalizada hace 5 días, ganador: sofia, ya calificada
    EstadoPropuesta aceptado7 = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.ACEPTADO);
    Propuesta ofertaGanadora7 = Propuesta.builder()
        .id("o5").autor(sofia).destinatario(lucas)
        .figuritasOfrecidas(List.of(pedri))
        .figuritaBuscada(griezmann)
        .estado(new ArrayList<>(List.of(aceptado7)))
        .estadoActual(aceptado7)
        .build();
    subastas.guardar(Subasta.builder()
        .id("7").autor(lucas)
        .fechaInicio(LocalDateTime.now().minusDays(5))
        .fechaCierre(LocalDateTime.now().minusMinutes(1))
        .figuritaSubastada(griezmann)
        .ofertas(new ArrayList<>(List.of(ofertaGanadora7))).build());

    // ─── SUBASTAS DONDE LUCAS PARTICIPÓ (autor = otro perfil) ────────────────

    // id=4 | Activa, cierra en 2h, oferta de lucas SELECCIONADA
    Propuesta ofertaLucas4 = Propuesta.builder()
        .id("o6").autor(lucas).destinatario(sofia)
        .figuritasOfrecidas(List.of(griezmann, kroos))
        .figuritaBuscada(vinicius).build();
    ofertaLucas4.seleccionar(sofia.getId());
    subastas.guardar(Subasta.builder()
        .id("4").autor(sofia)
        .fechaInicio(LocalDateTime.now())
        .fechaCierre(LocalDateTime.now().plusHours(2))
        .figuritaSubastada(vinicius)
        .ofertas(new ArrayList<>(List.of(ofertaLucas4))).build());

    // id=5 | Activa, cierra en 1 día, oferta de lucas PENDIENTE
    Propuesta ofertaLucas5 = Propuesta.builder()
        .id("o7").autor(lucas).destinatario(matias)
        .figuritasOfrecidas(List.of(diMaria, messi))
        .figuritaBuscada(messi).build();
    subastas.guardar(Subasta.builder()
        .id("5").autor(matias)
        .fechaInicio(LocalDateTime.now())
        .fechaCierre(LocalDateTime.now().plusDays(1))
        .figuritaSubastada(messi)
        .ofertas(new ArrayList<>(List.of(ofertaLucas5))).build());

    // id=8 | Finalizada hace 5 días, ganador: juan, lucas no ganó
    Propuesta ofertaLucas8 = Propuesta.builder()
        .id("o9").autor(lucas).destinatario(sofia)
        .figuritasOfrecidas(List.of(kroos))
        .figuritaBuscada(griezmann).build();
    EstadoPropuesta aceptado8 = new EstadoPropuesta(LocalDateTime.now(), EstadoProceso.ACEPTADO);
    Propuesta ofertaJuan1 = Propuesta.builder()
        .id("o10").autor(juan).destinatario(sofia)
        .figuritasOfrecidas(List.of(kroos))
        .figuritaBuscada(griezmann)
        .estado(new ArrayList<>(List.of(aceptado8)))
        .estadoActual(aceptado8)
        .build();
    Calificacion calificacion = new Calificacion("202914", lucas, sofia, 2, "asda", "8", MetodoIntercambio.SUBASTA);
    calificaciones.guardar(calificacion);
    sofia.agregarNuevaCalificacion(calificacion);
    perfiles.guardar(sofia);
    subastas.guardar(Subasta.builder()
        .id("8").autor(sofia)
        .fechaInicio(LocalDateTime.now().minusDays(5))
        .fechaCierre(LocalDateTime.now().minusMinutes(1))
        .figuritaSubastada(griezmann)
        .ofertas(new ArrayList<>(List.of(ofertaLucas8, ofertaJuan1))).build());

    // ─── SUBASTAS DONDE LUCAS NO PARTICIPÓ ────────────────────────────────────

    // id=6 | Finalizada hace 5 días, sin ofertas
    subastas.guardar(Subasta.builder()
        .id("6").autor(juan)
        .fechaInicio(LocalDateTime.now().minusDays(5))
        .fechaCierre(LocalDateTime.now().minusMinutes(1))
        .figuritaSubastada(neymar).build());
  }
}