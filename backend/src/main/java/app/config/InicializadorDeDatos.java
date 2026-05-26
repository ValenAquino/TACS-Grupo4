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
import app.repositories.RepositorioUsuarios;
import app.servicios.ServicioEnriquecimiento;
import app.repositories.impl.campos.CamposPerfil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
  private final MongoTemplate mongoTemplate;
  private final ServicioEnriquecimiento enriquecimientoService;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Value("${SEED_DATA:false}")
  private boolean seedData;

  public InicializadorDeDatos(RepositorioPerfiles perfiles,
                              RepositorioPropuestas propuestas,
                              RepositorioSubastas subastas,
                              RepositorioColecciones colecciones,
                              RepositorioFiguritas figuritas,
                              RepositorioUsuarios usuarios,
                              RepositorioCalificacion calificaciones,
                              MongoTemplate mongoTemplate,
                              ServicioEnriquecimiento enriquecimientoService) {
    this.perfiles = perfiles;
    this.propuestas = propuestas;
    this.subastas = subastas;
    this.colecciones = colecciones;
    this.figuritas = figuritas;
    this.usuarios = usuarios;
    this.calificaciones = calificaciones;
    this.mongoTemplate = mongoTemplate;
    this.enriquecimientoService = enriquecimientoService;
  }

  private List<MedioDeContacto> telegram(String numero) {
    return new ArrayList<>(List.of(new MedioDeContacto(MedioComunicacion.TELEGRAM, numero)));
  }

  private Propuesta propuesta(Perfil autor, Perfil destino,
                              List<Figurita> figuritas, Figurita buscada, EstadoProceso estado) {
    EstadoPropuesta estadoActual = new EstadoPropuesta(LocalDateTime.now(), estado);
    return Propuesta.builder()
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
    if (seedData) {
      mongoTemplate.dropCollection(Usuario.class);
      mongoTemplate.dropCollection(Perfil.class);
      mongoTemplate.dropCollection(Coleccion.class);
      mongoTemplate.dropCollection(Figurita.class);
      mongoTemplate.dropCollection(Propuesta.class);
      mongoTemplate.dropCollection(Subasta.class);
      mongoTemplate.dropCollection(Calificacion.class);
    } else if (perfiles.contar() > 0) {
      enriquecimientoService.enriquecer();
      return;
    }

    Figurita messi     = Figurita.builder().numero(10).jugador("Messi").seleccion(Seleccion.ARGENTINA).posicion("Delantero").build();
    Figurita diMaria   = Figurita.builder().numero(11).jugador("Di María").seleccion(Seleccion.ARGENTINA).posicion("Extremo").build();
    Figurita lautaro   = Figurita.builder().numero(9).jugador("Lautaro").seleccion(Seleccion.ARGENTINA).posicion("Delantero").build();
    Figurita mbappe    = Figurita.builder().numero(10).jugador("Mbappé").seleccion(Seleccion.FRANCIA).posicion("Delantero").build();
    Figurita griezmann = Figurita.builder().numero(27).jugador("Griezmann").seleccion(Seleccion.FRANCIA).posicion("Mediocampista").build();
    Figurita vinicius  = Figurita.builder().numero(30).jugador("Vinicius").seleccion(Seleccion.BRASIL).posicion("Extremo").build();
    Figurita pedri     = Figurita.builder().numero(31).jugador("Pedri").seleccion(Seleccion.ESPAÑA).posicion("Mediocampista").build();
    Figurita kroos     = Figurita.builder().numero(40).jugador("Kroos").seleccion(Seleccion.ALEMANIA).posicion("Mediocampista").build();
    Figurita neymar    = Figurita.builder().numero(58).jugador("Neymar").seleccion(Seleccion.BRASIL).posicion("Delantero").build();

    figuritas.guardar(messi);
    figuritas.guardar(diMaria);
    figuritas.guardar(lautaro);
    figuritas.guardar(mbappe);
    figuritas.guardar(griezmann);
    figuritas.guardar(vinicius);
    figuritas.guardar(pedri);
    figuritas.guardar(kroos);
    figuritas.guardar(neymar);

    String idJuan   = new ObjectId().toHexString();
    String idLucas  = new ObjectId().toHexString();
    String idSofia  = new ObjectId().toHexString();
    String idMatias = new ObjectId().toHexString();

    Perfil[] cuatro = cargarPerfiles(messi, diMaria, lautaro, mbappe, griezmann, vinicius, pedri, kroos, neymar,
        idJuan, idLucas, idSofia, idMatias);
    Perfil lucas  = cuatro[0];
    Perfil sofia  = cuatro[1];
    Perfil matias = cuatro[2];
    Perfil juan   = cuatro[3];

    cargarCalificaciones(lucas, sofia, matias, juan);
    cargarPropuestas(messi, diMaria, lautaro, griezmann, mbappe, vinicius, pedri, kroos, neymar,
        idLucas, idSofia, idMatias, idJuan);
    cargarSubastas(griezmann, vinicius, pedri, kroos, neymar, mbappe, diMaria, messi, lautaro,
        idJuan, idLucas, idSofia, idMatias);
    cargarFiguritasExtra(lucas, sofia, matias, juan);
    enriquecimientoService.enriquecer();
  }

  private void cargarFiguritasExtra(Perfil lucas, Perfil sofia, Perfil matias, Perfil juan) {
    String[][] jugadores = {
        {"ARGENTINA", "Delantero", "Almada", "Defensor", "Acuña", "Delantero", "Dybala", "Mediocampista", "Mac Allister", "Defensor", "Molina", "Defensor", "Otamendi", "Mediocampista", "Palacios", "Arquero", "Rulli"},
        {"BRASIL", "Delantero", "Endrick", "Defensor", "Éder Militão", "Mediocampista", "Gerson", "Defensor", "Marquinhos", "Mediocampista", "Paquetá", "Extremo", "Raphinha", "Extremo", "Rodrygo", "Extremo", "Savinho"},
        {"FRANCIA", "Mediocampista", "Camavinga", "Extremo", "Coman", "Extremo", "Dembélé", "Defensor", "Koundé", "Arquero", "Lloris", "Arquero", "Maignan", "Mediocampista", "Rabiot", "Mediocampista", "Tchouaméni"},
        {"ESPAÑA", "Defensor", "Cucurella", "Mediocampista", "Dani Olmo", "Mediocampista", "Fabián Ruiz", "Extremo", "Ferran Torres", "Defensor", "Laporte", "Defensor", "Le Normand", "Arquero", "Navas", "Extremo", "Yamal"},
        {"ALEMANIA", "Extremo", "Gnabry", "Mediocampista", "Havertz", "Mediocampista", "Kimmich", "Mediocampista", "Musiala", "Arquero", "Neuer", "Defensor", "Rüdiger", "Extremo", "Sané", "Mediocampista", "Wirtz"}
    };

    List<List<MetodoIntercambio>> metodos = List.of(
        List.of(MetodoIntercambio.INTERCAMBIO),
        List.of(MetodoIntercambio.SUBASTA),
        List.of(MetodoIntercambio.INTERCAMBIO, MetodoIntercambio.SUBASTA)
    );

    Perfil[] perfilesExtra = { lucas, sofia, matias, juan };
    Coleccion[] cols = {
        lucas.getColeccion(),
        sofia.getColeccion(),
        matias.getColeccion(),
        juan.getColeccion()
    };

    int contador = 0;
    for (String[] sel : jugadores) {
      Seleccion seleccion = Seleccion.valueOf(sel[0]);
      for (int i = 0; i < 8; i++) {
        String posicion = sel[1 + i * 2];
        String nombre   = sel[2 + i * 2];
        Figurita fig = Figurita.builder().numero(i + 1).jugador(nombre).seleccion(seleccion).posicion(posicion).build();
        figuritas.guardar(fig);
        Coleccion col = cols[contador % cols.length];
        String perfilId = perfilesExtra[contador % perfilesExtra.length].getId();
        col.getRepetidas().add(new FiguritaIntercambiable(fig, 2, metodos.get(contador % metodos.size()), perfilId));
        contador++;
      }
    }

    for (Coleccion col : cols) colecciones.guardar(col);
  }

  private Perfil[] cargarPerfiles(Figurita messi, Figurita diMaria, Figurita lautaro,
                                  Figurita mbappe, Figurita griezmann, Figurita vinicius,
                                  Figurita pedri, Figurita kroos, Figurita neymar,
                                  String juanId, String lucasId, String sofiaId, String matiasId) {

    Usuario admin = new Usuario("admin", passwordEncoder.encode("admin"), Rol.ADMINISTRADOR);
    usuarios.guardar(admin);
    Coleccion coleccionAdmin = new Coleccion();
    colecciones.guardar(coleccionAdmin);
    perfiles.guardar(Perfil.builder()
        .usuario(admin)
        .nombre(admin.getNombre())
        .coleccion(coleccionAdmin)
        .build());

    // JUAN
    Coleccion coleccionJuan = new Coleccion();
    coleccionJuan.getFaltantes().add(griezmann);
    coleccionJuan.getFaltantes().add(kroos);
    colecciones.guardar(coleccionJuan);

    Usuario userJuan = new Usuario("juan_jose", passwordEncoder.encode("una contrasenia"), Rol.USUARIO);
    usuarios.guardar(userJuan);

    Perfil juan = Perfil.builder()
        .id(juanId)
        .usuario(userJuan)
        .nombre("Juan")
        .coleccion(coleccionJuan)
        .mediosDeContacto(telegram("@juan"))
        .build();
    perfiles.guardar(juan);

    coleccionJuan.getRepetidas().add(new FiguritaIntercambiable(pedri,     2, 0, List.of(MetodoIntercambio.INTERCAMBIO), juan.getId()));
    coleccionJuan.getRepetidas().add(new FiguritaIntercambiable(kroos,     2, 1, List.of(MetodoIntercambio.SUBASTA),     juan.getId()));
    coleccionJuan.getRepetidas().add(new FiguritaIntercambiable(griezmann, 2, 1, List.of(MetodoIntercambio.INTERCAMBIO), juan.getId()));
    coleccionJuan.getRepetidas().add(new FiguritaIntercambiable(lautaro,   2, 1, List.of(MetodoIntercambio.INTERCAMBIO), juan.getId()));
    colecciones.guardar(coleccionJuan);

    // LUCAS
    Coleccion coleccionLucas = new Coleccion();
    coleccionLucas.getFaltantes().add(lautaro);
    coleccionLucas.getFaltantes().add(pedri);
    coleccionLucas.getFaltantes().add(vinicius);
    colecciones.guardar(coleccionLucas);

    Usuario userLucas = new Usuario("lucas_fis", passwordEncoder.encode("gordo123"), Rol.USUARIO);
    usuarios.guardar(userLucas);

    Perfil lucas = Perfil.builder()
        .id(lucasId)
        .usuario(userLucas)
        .nombre("lucas")
        .coleccion(coleccionLucas)
        .mediosDeContacto(telegram("@lucas"))
        .build();
    perfiles.guardar(lucas);

    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(diMaria,   2, 1, List.of(MetodoIntercambio.SUBASTA),     lucas.getId()));
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(griezmann, 2, 1, List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId()));
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(mbappe,    3, 0, List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId()));
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(kroos,     2, 2, List.of(MetodoIntercambio.SUBASTA),     lucas.getId()));
    coleccionLucas.getRepetidas().add(new FiguritaIntercambiable(messi,     2, 2, List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId()));
    colecciones.guardar(coleccionLucas);

    // SOFIA
    Coleccion coleccionSofia = new Coleccion();
    coleccionSofia.getFaltantes().add(messi);
    coleccionSofia.getFaltantes().add(lautaro);
    coleccionSofia.getFaltantes().add(griezmann);
    coleccionSofia.getFaltantes().add(kroos);
    colecciones.guardar(coleccionSofia);

    Usuario userSofia = new Usuario("sofia_ape", passwordEncoder.encode("password"), Rol.USUARIO);
    usuarios.guardar(userSofia);

    Perfil sofia = Perfil.builder()
        .id(sofiaId)
        .usuario(userSofia)
        .nombre("Sofía")
        .coleccion(coleccionSofia)
        .mediosDeContacto(telegram("@sofia"))
        .build();
    perfiles.guardar(sofia);

    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(mbappe,    2, 0, List.of(MetodoIntercambio.INTERCAMBIO), sofia.getId()));
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(neymar,    1, 1, List.of(MetodoIntercambio.INTERCAMBIO), sofia.getId()));
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(pedri,     2, 1, List.of(MetodoIntercambio.SUBASTA),     sofia.getId()));
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(vinicius,  1, 1, List.of(MetodoIntercambio.SUBASTA),     sofia.getId()));
    coleccionSofia.getRepetidas().add(new FiguritaIntercambiable(griezmann, 2, 1, List.of(MetodoIntercambio.INTERCAMBIO), sofia.getId()));
    colecciones.guardar(coleccionSofia);

    // MATIAS
    Coleccion coleccionMatias = new Coleccion();
    coleccionMatias.getFaltantes().add(diMaria);
    coleccionMatias.getFaltantes().add(pedri);
    coleccionMatias.getFaltantes().add(kroos);
    colecciones.guardar(coleccionMatias);

    Usuario userMatias = new Usuario("mati_crim", passwordEncoder.encode("wordpass"), Rol.USUARIO);
    usuarios.guardar(userMatias);

    Perfil matias = Perfil.builder()
        .id(matiasId)
        .usuario(userMatias)
        .nombre("Matías")
        .coleccion(coleccionMatias)
        .mediosDeContacto(telegram("@matias"))
        .build();
    perfiles.guardar(matias);

    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(vinicius, 1, 1, List.of(MetodoIntercambio.INTERCAMBIO), matias.getId()));
    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(messi,    2, 1, List.of(MetodoIntercambio.INTERCAMBIO), matias.getId()));
    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(lautaro,  2, 1, List.of(MetodoIntercambio.INTERCAMBIO), matias.getId()));
    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(pedri,    2, 1, List.of(MetodoIntercambio.SUBASTA),     matias.getId()));
    coleccionMatias.getRepetidas().add(new FiguritaIntercambiable(kroos,    2, 1, List.of(MetodoIntercambio.SUBASTA),     matias.getId()));
    colecciones.guardar(coleccionMatias);

    return new Perfil[]{ lucas, sofia, matias, juan };
  }

  private void cargarCalificaciones(Perfil lucas, Perfil sofia, Perfil matias, Perfil juan) {
    Perfil[] autores = { sofia, matias, juan };
    String[] descripciones = {
        "Excelente trato, muy puntual",
        "Todo perfecto, lo recomiendo",
        "Muy buena experiencia",
        "Rápido y confiable",
        "Sin problemas, volvería a intercambiar",
        "Cumplió con todo lo acordado",
        "Muy amable y responsable",
        "Intercambio sin complicaciones",
        "Respondió rápido y fue honesto",
        "Genial, 100% recomendado"
    };
    int[] valores = { 5, 4, 5, 3, 4, 5, 2, 4, 5, 3 };

    // Lucas: 30 calificaciones para probar paginación
    for (int i = 0; i < 30; i++) {
      Calificacion cal = new Calificacion(
          "CAL-" + i,
          autores[i % autores.length],
          lucas,
          valores[i % valores.length],
          descripciones[i % descripciones.length],
          "intercambio-" + i,
          MetodoIntercambio.INTERCAMBIO
      );
      lucas.agregarNuevaCalificacion(cal);
      calificaciones.guardar(cal);
    }
    perfiles.guardar(lucas);

    // Sofía: promedio 3.67
    List<Calificacion> calsSofia = List.of(
        new Calificacion("C-3", lucas,  sofia, 4, "Buena experiencia",     "2000", MetodoIntercambio.INTERCAMBIO),
        new Calificacion("C-4", matias, sofia, 3, "Normal, sin problemas", "2001", MetodoIntercambio.INTERCAMBIO),
        new Calificacion("C-5", juan,   sofia, 4, "Respondió rápido",      "3000", MetodoIntercambio.SUBASTA)
    );
    calsSofia.forEach(c -> { sofia.agregarNuevaCalificacion(c); calificaciones.guardar(c); });
    perfiles.guardar(sofia);

    // Matías: promedio 2.5
    List<Calificacion> calsMatias = List.of(
        new Calificacion("C-6", lucas, matias, 2, "Tardó bastante en responder", "2002", MetodoIntercambio.INTERCAMBIO),
        new Calificacion("C-7", sofia, matias, 3, "Aceptable",                   "2001", MetodoIntercambio.INTERCAMBIO)
    );
    calsMatias.forEach(c -> { matias.agregarNuevaCalificacion(c); calificaciones.guardar(c); });
    perfiles.guardar(matias);

    // Juan: promedio 1.5
    List<Calificacion> calsJuan = List.of(
        new Calificacion("C-8", lucas, juan, 1, "No cumplió con el intercambio", "3001", MetodoIntercambio.SUBASTA),
        new Calificacion("C-9", sofia, juan, 2, "Mala comunicación",             "3000", MetodoIntercambio.SUBASTA)
    );
    calsJuan.forEach(c -> { juan.agregarNuevaCalificacion(c); calificaciones.guardar(c); });
    perfiles.guardar(juan);
  }

  private void cargarPropuestas(Figurita messi,
                                Figurita diMaria,
                                Figurita lautaro,
                                Figurita griezmann,
                                Figurita mbappe,
                                Figurita vinicius,
                                Figurita pedri,
                                Figurita kroos,
                                Figurita neymar,
                                String lucasId,
                                String sofiaId,
                                String matiasId,
                                String juanId) {
    CamposPerfil sinCampos = new CamposPerfil(false);
    Perfil lucas  = perfiles.buscarPorId(lucasId,  sinCampos);
    Perfil sofia  = perfiles.buscarPorId(sofiaId,  sinCampos);
    Perfil matias = perfiles.buscarPorId(matiasId, sinCampos);
    Perfil juan   = perfiles.buscarPorId(juanId,   sinCampos);

    // PROPUESTAS BASE
    propuestas.guardar(propuesta(lucas,  sofia,  List.of(messi),             mbappe,   EstadoProceso.PENDIENTE));
    propuestas.guardar(propuesta(sofia,  matias, List.of(griezmann),         vinicius, EstadoProceso.ACEPTADO));
    propuestas.guardar(propuesta(matias, lucas,  List.of(vinicius),          diMaria,  EstadoProceso.RECHAZADO));

    // PROPUESTAS RECIBIDAS POR LUCAS
    propuestas.guardar(propuesta(sofia,  lucas,  List.of(neymar),            lautaro,  EstadoProceso.PENDIENTE));
    propuestas.guardar(propuesta(juan,   lucas,  List.of(kroos),             pedri,    EstadoProceso.PENDIENTE));
    propuestas.guardar(propuesta(matias, lucas,  List.of(messi),             vinicius, EstadoProceso.RECHAZADO));
    propuestas.guardar(propuesta(sofia,  lucas,  List.of(griezmann),         mbappe,   EstadoProceso.ACEPTADO));

    // PROPUESTAS EXTRA
    propuestas.guardar(propuesta(juan,   sofia,  List.of(pedri),             messi,    EstadoProceso.CANCELADO));
    propuestas.guardar(propuesta(lucas,  matias, List.of(griezmann, mbappe), kroos,    EstadoProceso.SELECCIONADO));
  }

  private void cargarSubastas(Figurita messi, Figurita diMaria, Figurita lautaro,
                              Figurita mbappe, Figurita griezmann, Figurita vinicius,
                              Figurita pedri, Figurita kroos, Figurita neymar,
                              String juanId, String lucasId, String sofiaId, String matiasId) {
    CamposPerfil sinCampos = new CamposPerfil(false);
    Perfil lucas  = perfiles.buscarPorId(lucasId,  sinCampos);
    Perfil sofia  = perfiles.buscarPorId(sofiaId,  sinCampos);
    Perfil matias = perfiles.buscarPorId(matiasId, sinCampos);
    Perfil juan   = perfiles.buscarPorId(juanId,   sinCampos);
    if (lucas  == null) throw new RuntimeException("Lucas es null");
    if (sofia  == null) throw new RuntimeException("Sofía es null");
    if (matias == null) throw new RuntimeException("Matías es null");
    if (juan   == null) throw new RuntimeException("Juan es null");

    // ─── MIS SUBASTAS (autor = Lucas) ────────────────────────────────────────

    // id=1 | Activa, cierra en ~45 min, 3 ofertas
    Propuesta ofertaSofia = Propuesta.builder()
        .id("o1").autor(sofia).destinatario(lucas)
        .figuritasOfrecidas(List.of(neymar, vinicius))
        .figuritaBuscada(lautaro).build();
    Propuesta ofertaMatias = Propuesta.builder()
        .id("o2").autor(matias).destinatario(lucas)
        .figuritasOfrecidas(List.of(pedri, kroos))
        .figuritaBuscada(lautaro).build();
    Propuesta ofertaJuan = Propuesta.builder()
        .id("o3").autor(juan).destinatario(lucas)
        .figuritasOfrecidas(List.of(griezmann))
        .figuritaBuscada(lautaro).build();
    ofertaSofia.seleccionar(lucas.getId());
    subastas.guardar(Subasta.builder()
        .id("1").autor(lucas)
        .fechaInicio(LocalDateTime.now())
        .fechaCierre(LocalDateTime.now().plusMinutes(45))
        .figuritaSubastada(lautaro)
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
