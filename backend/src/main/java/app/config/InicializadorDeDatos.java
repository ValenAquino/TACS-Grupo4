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
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                                RepositorioUsuarios usuarios, RepositorioCalificacion calificaciones) {
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

    private Propuesta propuesta( Perfil autor, Perfil destino,
                                List<Figurita> figuritas, Figurita buscada, EstadoProceso estado) {
      EstadoPropuesta estadoActual = new EstadoPropuesta(
          LocalDateTime.now(),
          estado
      );

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
        Figurita messi = new Figurita("ARG-10", 10, "Messi", Seleccion.ARGENTINA, "Delantero");
        Figurita diMaria = new Figurita("ARG-11", 11, "Di María", Seleccion.ARGENTINA, "Extremo");
        Figurita lautaro = new Figurita("ARG-9", 9, "Lautaro", Seleccion.ARGENTINA, "Delantero");
        Figurita mbappe = new Figurita("FRA-10", 10, "Mbappé", Seleccion.FRANCIA, "Delantero");
        Figurita griezmann = new Figurita("FRA-7", 27, "Griezmann", Seleccion.FRANCIA, "Mediocampista");
        Figurita vinicius = new Figurita("BRA-10", 30, "Vinicius", Seleccion.BRASIL, "Extremo");
        Figurita pedri = new Figurita("ESP-10", 31, "Pedri", Seleccion.ESPAÑA, "Mediocampista");
        Figurita kroos = new Figurita("GER-8", 40, "Kroos", Seleccion.ALEMANIA, "Mediocampista");
        Figurita neymar = new Figurita("BRA-11", 58, "Neymar", Seleccion.BRASIL, "Delantero");

        figuritas.guardar(messi);
        figuritas.guardar(diMaria);
        figuritas.guardar(lautaro);
        figuritas.guardar(vinicius);
        figuritas.guardar(neymar);

        String idJuan   = new ObjectId().toHexString();
        String idLucas  = new ObjectId().toHexString();
        String idSofia  = new ObjectId().toHexString();
        String idMatias = new ObjectId().toHexString();

        cargarPerfiles(messi, diMaria, lautaro, mbappe, griezmann, vinicius, pedri, kroos, neymar, idJuan, idLucas, idSofia, idMatias);
        cargarPropuestas(messi, diMaria, griezmann, mbappe, vinicius, idLucas, idSofia, idMatias, idJuan);
        cargarSubastas(griezmann, vinicius, pedri, kroos, neymar, mbappe, diMaria, messi, lautaro, idJuan, idLucas, idSofia, idMatias);
        cargarFiguritasExtra(); // comentar para desactivar datos de prueba de paginación
    }

    private void cargarFiguritasExtra() {
        String[][] jugadores = {
            {"ARG", "ARGENTINA", "Delantero", "Almada", "Defensor", "Acuña", "Delantero", "Dybala", "Mediocampista", "Mac Allister", "Defensor", "Molina", "Defensor", "Otamendi", "Mediocampista", "Palacios", "Arquero", "Rulli"},
            {"BRA", "BRASIL", "Delantero", "Endrick", "Defensor", "Éder Militão", "Mediocampista", "Gerson", "Defensor", "Marquinhos", "Mediocampista", "Paquetá", "Extremo", "Raphinha", "Extremo", "Rodrygo", "Extremo", "Savinho"},
            {"FRA", "FRANCIA", "Mediocampista", "Camavinga", "Extremo", "Coman", "Extremo", "Dembélé", "Defensor", "Koundé", "Arquero", "Lloris", "Arquero", "Maignan", "Mediocampista", "Rabiot", "Mediocampista", "Tchouaméni"},
            {"ESP", "ESPAÑA", "Defensor", "Cucurella", "Mediocampista", "Dani Olmo", "Mediocampista", "Fabián Ruiz", "Extremo", "Ferran Torres", "Defensor", "Laporte", "Defensor", "Le Normand", "Arquero", "Navas", "Extremo", "Yamal"},
            {"GER", "ALEMANIA", "Extremo", "Gnabry", "Mediocampista", "Havertz", "Mediocampista", "Kimmich", "Mediocampista", "Musiala", "Arquero", "Neuer", "Defensor", "Rüdiger", "Extremo", "Sané", "Mediocampista", "Wirtz"}
        };
        MetodoIntercambio[] metodos = {
            MetodoIntercambio.INTERCAMBIO,
            MetodoIntercambio.SUBASTA
        };
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
                MetodoIntercambio metodo = metodos[contador % metodos.length];
                String perfilId = perfilIds[contador % perfilIds.length];
                //TODO: intercambiables.guardar(new FiguritaIntercambiable(fig, 2, List.of(metodo), perfilId));
                contador++;
            }
        }
    }

    private void cargarPerfiles(Figurita messi, Figurita diMaria, Figurita lautaro,
                                Figurita mbappe, Figurita griezmann, Figurita vinicius,
                                Figurita pedri, Figurita kroos, Figurita neymar,
                                String juanId, String lucasId, String sofiaId, String matiasId) {
      PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      Usuario user = new Usuario("admin", passwordEncoder.encode("admin"), Rol.ADMINISTRADOR);
      usuarios.guardar(user);
      Coleccion coleccionAdmin = new Coleccion();
      Perfil perfilAdmin = Perfil.builder()
          .usuario(user).nombre(user.getNombre())
          .coleccion(coleccionAdmin)
          .build();

      colecciones.guardar(coleccionAdmin);
      perfiles.guardar(perfilAdmin);

      // Juan
      Coleccion coleccionJuan = new Coleccion();
      colecciones.guardar(coleccionJuan);
      user =  new Usuario("juan_jose",passwordEncoder.encode("una contrasenia"), Rol.USUARIO);
      usuarios.guardar(user);
      Perfil juan = Perfil.builder()
          .id(juanId)
          .usuario(user)
          .nombre("Juan").coleccion(coleccionJuan)
          .mediosDeContacto(telegram("@juan")).build();
      perfiles.guardar(juan);

      FiguritaIntercambiable interPedri = new FiguritaIntercambiable(pedri, 1, List.of(MetodoIntercambio.INTERCAMBIO), juan.getId());
      coleccionJuan.getRepetidas().add(interPedri);
      coleccionJuan.getFaltantes().add(pedri);
      coleccionJuan.getFaltantes().add(kroos);
      colecciones.guardar(coleccionJuan);

        // Lucas
      Coleccion coleccionLucas = new Coleccion();
      colecciones.guardar(coleccionLucas);

      user = new Usuario("lucas_fis",passwordEncoder.encode("gordo123"), Rol.USUARIO);
      usuarios.guardar(user);
      Perfil lucas = Perfil.builder()
          .id(lucasId)
          .usuario(user)
          .nombre("lucas").coleccion(coleccionLucas)
          .mediosDeContacto(telegram("@lucas")).build();

      Calificacion calificacion = Calificacion.builder()
          .autor(juan).destinatario(lucas).valor(4)
          .descripcion("dasda").transaccionId("1123")
          .tipoTransaccion(MetodoIntercambio.INTERCAMBIO)
          .build();
      this.calificaciones.guardar(calificacion);
      perfiles.guardar(lucas);

      FiguritaIntercambiable interMessi   = new FiguritaIntercambiable(messi,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId());
      FiguritaIntercambiable interLautaro   = new FiguritaIntercambiable(lautaro,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId());
      FiguritaIntercambiable interGriezmann1   = new FiguritaIntercambiable(griezmann,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId());
      FiguritaIntercambiable interMbappe   = new FiguritaIntercambiable(mbappe,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), lucas.getId());
      FiguritaIntercambiable interDiMaria = new FiguritaIntercambiable(diMaria, 2, 2,List.of(MetodoIntercambio.SUBASTA),     lucas.getId());
      coleccionLucas.getRepetidas().add(interMessi);
      coleccionLucas.getRepetidas().add(interDiMaria);
      coleccionLucas.getRepetidas().add(interLautaro);
      coleccionLucas.getRepetidas().add(interGriezmann1);
      coleccionLucas.getRepetidas().add(interMbappe);
      coleccionLucas.getFaltantes().add(mbappe);
      coleccionLucas.getFaltantes().add(vinicius);
      colecciones.guardar(coleccionLucas);

      // Sofía
      Coleccion coleccionSofia = new Coleccion();
      colecciones.guardar(coleccionSofia);
      user = new Usuario("sofia_ape",passwordEncoder.encode("password"), Rol.USUARIO);
      usuarios.guardar(user);
      Perfil sofia = Perfil.builder()
          .id(sofiaId)
          .usuario(user)
          .nombre("Sofía").coleccion(coleccionSofia)
          .mediosDeContacto(telegram("@sofia")).build();
      perfiles.guardar(sofia);

      FiguritaIntercambiable interMbappe2    = new FiguritaIntercambiable(mbappe,    2, List.of(MetodoIntercambio.INTERCAMBIO), sofia.getId());
      FiguritaIntercambiable interGriezmann = new FiguritaIntercambiable(griezmann, 1, List.of(MetodoIntercambio.SUBASTA),     sofia.getId());
      FiguritaIntercambiable interNeymar = new FiguritaIntercambiable(neymar, 1, List.of(MetodoIntercambio.INTERCAMBIO), sofia.getId());
      coleccionSofia.getRepetidas().add(interMbappe2);
      coleccionSofia.getRepetidas().add(interGriezmann);
      coleccionSofia.getRepetidas().add(interNeymar);
      coleccionSofia.getFaltantes().add(messi);
      coleccionSofia.getFaltantes().add(lautaro);
      colecciones.guardar(coleccionSofia);

      // Matías
      Coleccion coleccionMatias = new Coleccion();
      colecciones.guardar(coleccionMatias);
      user = new Usuario("mati_crim",passwordEncoder.encode("wordpass"), Rol.USUARIO);
      usuarios.guardar(user);
      Perfil matias = Perfil.builder()
          .id(matiasId)
          .usuario(user)
          .nombre("Matías").coleccion(coleccionMatias)
          .mediosDeContacto(telegram("@matias")).build();
      perfiles.guardar(matias);

      FiguritaIntercambiable interVinicius = new FiguritaIntercambiable(vinicius, 1, List.of(MetodoIntercambio.INTERCAMBIO), matias.getId());
      coleccionMatias.getRepetidas().add(interVinicius);
      coleccionMatias.getFaltantes().add(pedri);
      coleccionMatias.getFaltantes().add(kroos);
      colecciones.guardar(coleccionMatias);

      this.cargarCalificaciones(lucas, sofia, matias, juan);
    }

    private void cargarCalificaciones(Perfil lucas, Perfil sofia, Perfil matias, Perfil juan) {
      calificaciones.guardar(Calificacion.builder()
          .autor(sofia).destinatario(lucas).valor(5)
          .descripcion("Excelente trato, muy rápido").transaccionId("2000")
          .tipoTransaccion(MetodoIntercambio.INTERCAMBIO).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(matias).destinatario(lucas).valor(4)
          .descripcion("Todo bien, lo recomiendo").transaccionId("2002")
          .tipoTransaccion(MetodoIntercambio.INTERCAMBIO).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(lucas).destinatario(sofia).valor(4)
          .descripcion("Buena experiencia").transaccionId("2000")
          .tipoTransaccion(MetodoIntercambio.INTERCAMBIO).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(matias).destinatario(sofia).valor(3)
          .descripcion("Normal, sin problemas").transaccionId("2001")
          .tipoTransaccion(MetodoIntercambio.INTERCAMBIO).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(juan).destinatario(sofia).valor(4)
          .descripcion("Respondió rápido").transaccionId("3000")
          .tipoTransaccion(MetodoIntercambio.SUBASTA).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(lucas).destinatario(matias).valor(2)
          .descripcion("Tardó bastante en responder").transaccionId("2002")
          .tipoTransaccion(MetodoIntercambio.INTERCAMBIO).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(sofia).destinatario(matias).valor(3)
          .descripcion("Aceptable").transaccionId("2001")
          .tipoTransaccion(MetodoIntercambio.INTERCAMBIO).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(lucas).destinatario(juan).valor(1)
          .descripcion("No cumplió con el intercambio").transaccionId("3001")
          .tipoTransaccion(MetodoIntercambio.SUBASTA).build());

      calificaciones.guardar(Calificacion.builder()
          .autor(sofia).destinatario(juan).valor(2)
          .descripcion("Mala comunicación").transaccionId("3000")
          .tipoTransaccion(MetodoIntercambio.SUBASTA).build());
    }

    private void cargarPropuestas(Figurita messi, Figurita diMaria,
                                  Figurita griezmann, Figurita mbappe, Figurita vinicius,
                                  String lucasId, String sofiaId, String matiasId, String juanId) {
      CamposPerfil sinCampos = new CamposPerfil(false);
      Perfil lucas  = perfiles.buscarPorId(lucasId, sinCampos);
      Perfil sofia  = perfiles.buscarPorId(sofiaId, sinCampos);
      Perfil matias = perfiles.buscarPorId(matiasId, sinCampos);

      List<Figurita> figuritas = new ArrayList<>();
      figuritas.add(messi);

      propuestas.guardar(propuesta(lucas, sofia, figuritas, mbappe, EstadoProceso.PENDIENTE));

      figuritas = new ArrayList<>();
      figuritas.add(griezmann);
      propuestas.guardar(propuesta(sofia, matias, figuritas, vinicius, EstadoProceso.ACEPTADO));

      figuritas = new ArrayList<>();
      figuritas.add(vinicius);
      propuestas.guardar(propuesta(matias, lucas, figuritas, diMaria, EstadoProceso.RECHAZADO));
    }

    private void cargarSubastas(Figurita messi, Figurita diMaria, Figurita lautaro,
                                Figurita mbappe, Figurita griezmann, Figurita vinicius,
                                Figurita pedri, Figurita kroos, Figurita neymar,
                                String juanId, String lucasId, String sofiaId, String matiasId) {

      CamposPerfil sinCampos = new CamposPerfil(false);
      Perfil lucas  = perfiles.buscarPorId(lucasId, sinCampos);
      Perfil sofia  = perfiles.buscarPorId(sofiaId, sinCampos);
      Perfil matias = perfiles.buscarPorId(matiasId, sinCampos);
      Perfil juan   = perfiles.buscarPorId(juanId, sinCampos);
      if (lucas == null) throw new RuntimeException("Lucas es null");
      if (sofia == null) throw new RuntimeException("Sofía es null");
      if (matias == null) throw new RuntimeException("Matías es null");
      if (juan == null) throw new RuntimeException("Juan es null");

        // ─── MIS SUBASTAS (autor = Lucas) ────────────────────────────────────────

        // id=1 | Activa, cierra en ~45 min, 3 ofertas
      Propuesta ofertaSofia = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(sofia)
          .destinatario(lucas)
          .figuritasOfrecidas(List.of(neymar, vinicius))
          .figuritaBuscada(mbappe)
          .build();

      Propuesta ofertaPedro = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(matias)
          .destinatario(lucas)
          .figuritasOfrecidas(List.of(pedri, kroos))
          .figuritaBuscada(mbappe)
          .build();

      Propuesta ofertaLu = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(juan)
          .destinatario(lucas)
          .figuritasOfrecidas(List.of(griezmann, lautaro))
          .figuritaBuscada(mbappe)
          .build();

      ofertaSofia.seleccionar(lucas.getId());

      Subasta subasta1 = Subasta.builder()
          .autor(lucas)
          .fechaInicio(LocalDateTime.now())
          .fechaCierre(LocalDateTime.now().plusMinutes(45))
          .figuritaSubastada(mbappe)
          .ofertas(new ArrayList<>(List.of(ofertaSofia, ofertaPedro, ofertaLu)))
          .build();

      subastas.guardar(subasta1);

      // id=2 | Activa, cierra en 2 días, sin ofertas
      Subasta subasta2 = Subasta.builder()
          .autor(lucas)
          .fechaInicio(LocalDateTime.now())
          .fechaCierre(LocalDateTime.now().plusDays(2))
          .figuritaSubastada(pedri)
          .build();

      subastas.guardar(subasta2);

      // id=3 | Finalizada hace 2 días, ganador: matias, sin calificar
      Propuesta ofertaGanadora3 = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(matias)
          .destinatario(lucas)
          .figuritasOfrecidas(List.of(messi, lautaro))
          .figuritaBuscada(diMaria)
          .build();
      Subasta subasta3 = Subasta.builder()
          .autor(lucas)
          .fechaInicio(LocalDateTime.now().minusDays(2))
          .fechaCierre(LocalDateTime.now())
          .figuritaSubastada(diMaria)
          .ofertas(new ArrayList<>(List.of(ofertaGanadora3)))
          .build();

      ofertaGanadora3.aceptar(lucas.getId());
      subastas.guardar(subasta3);

      // id=7 | Finalizada hace 5 días, ganador: sofia, ya calificada
      Propuesta ofertaGanadora7 = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(sofia)
          .destinatario(lucas)
          .figuritasOfrecidas(List.of(pedri))
          .figuritaBuscada(griezmann)
          .build();
      Subasta subasta7 = Subasta.builder()
          .autor(lucas)
          .fechaInicio(LocalDateTime.now().minusDays(5))
          .fechaCierre(LocalDateTime.now())
          .figuritaSubastada(griezmann)
          .ofertas(new ArrayList<>(List.of(ofertaGanadora7)))
          .build();
      subastas.guardar(subasta7);

      // ─── SUBASTAS DONDE LUCAS PARTICIPÓ (autor = otro perfil) ────────────────

      // id=4 | Activa, cierra en 2h, oferta de lucas SELECCIONADA
      Propuesta ofertaLucas4 = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(lucas)
          .destinatario(sofia)
          .figuritasOfrecidas(List.of(griezmann, kroos))
          .figuritaBuscada(vinicius)
          .build();
      Subasta subasta4 = Subasta.builder()
          .autor(sofia)
          .fechaInicio(LocalDateTime.now())
          .fechaCierre(LocalDateTime.now().plusHours(2))
          .figuritaSubastada(vinicius)
          .ofertas(new ArrayList<>(List.of(ofertaLucas4)))
          .build();
      ofertaLucas4.seleccionar(sofia.getId());
      subastas.guardar(subasta4);

      // id=5 | Activa, cierra en 1 día, oferta de lucas RECHAZADA
      Propuesta ofertaLucas5 = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(lucas)
          .destinatario(matias)
          .figuritasOfrecidas(List.of(diMaria, messi))
          .figuritaBuscada(messi)
          .build();
      Subasta subasta5 = Subasta.builder()
          .autor(matias)
          .fechaInicio(LocalDateTime.now())
          .fechaCierre(LocalDateTime.now().plusDays(1))
          .figuritaSubastada(messi)
          .ofertas(new ArrayList<>(List.of(ofertaLucas5)))
          .build();

      subastas.guardar(subasta5);

      // id=8 | Finalizada hace 5 días, oferta de lucas ACEPTADA, ya calificada
      Propuesta ofertaLucas8 = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(lucas)
          .destinatario(sofia)
          .figuritasOfrecidas(List.of(kroos))
          .figuritaBuscada(griezmann)
          .build();
      Propuesta ofertaJuan1 = Propuesta.builder()
          .id(new ObjectId().toHexString()).autor(juan)
          .destinatario(sofia)
          .figuritasOfrecidas(List.of(kroos))
          .figuritaBuscada(griezmann)
          .build();
      Subasta subasta8 = Subasta.builder()
          .autor(sofia)
          .fechaInicio(LocalDateTime.now().minusDays(5))
          .fechaCierre(LocalDateTime.now())
          .figuritaSubastada(griezmann)
          .ofertas(new ArrayList<>(List.of(ofertaLucas8,ofertaJuan1)))
          .build();

      ofertaJuan1.aceptar(sofia.getId());
      Calificacion calificacion = new Calificacion(new ObjectId().toHexString(), lucas, sofia,  2, "asda", "8",MetodoIntercambio.SUBASTA);
      sofia.agregarNuevaCalificacion(calificacion);
      subastas.guardar(subasta8);
      calificaciones.guardar(calificacion);
      perfiles.guardar(sofia);

      // ─── SUBASTAS DONDE LUCAS NO PARTICIPÓ ────────────────

      // id=6 | Finalizada hace 5 días, oferta de lucas ACEPTADA, sin calificar
      Subasta subasta6 = Subasta.builder()
          .autor(juan)
          .fechaInicio(LocalDateTime.now().minusDays(5))
          .fechaCierre(LocalDateTime.now())
          .figuritaSubastada(neymar)
          .build();
      subastas.guardar(subasta6);
    }
}