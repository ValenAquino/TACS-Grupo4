package app;

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

    private Propuesta propuesta(String id, Perfil autor, Perfil destino,
                                List<Figurita> figuritas, Figurita buscada, EstadoProceso estado) {
        return new Propuesta(id, autor, destino, figuritas, buscada,
            new ArrayList<>(List.of(new EstadoPropuesta(LocalDateTime.now(), estado))));
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

        cargarPerfiles(messi, diMaria, lautaro, mbappe, griezmann, vinicius, pedri, kroos, neymar);
        cargarCalificaciones();
        cargarPropuestas(messi, diMaria, griezmann, mbappe, vinicius);
        cargarSubastas(griezmann, vinicius, pedri, kroos, neymar, mbappe, diMaria, messi, lautaro);
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
                                Figurita pedri, Figurita kroos, Figurita neymar) {
      // Juan
      Coleccion coleccionJuan = new Coleccion();
      FiguritaIntercambiable interPedri = new FiguritaIntercambiable(pedri, 1, List.of(MetodoIntercambio.INTERCAMBIO), "1003");
      coleccionJuan.getRepetidas().add(interPedri);
      coleccionJuan.getFaltantes().add(pedri);
      coleccionJuan.getFaltantes().add(kroos);
      colecciones.guardar(coleccionJuan);
      Usuario user =  new Usuario("u-1003",  Rol.USUARIO, "juan_jose","una contrasenia");
      usuarios.guardar(user);
      Perfil juan = Perfil.builder()
          .id("1003").usuario(user)
          .nombre("Juan").coleccion(coleccionJuan)
          .mediosDeContacto(telegram("@juan")).build();
      perfiles.guardar(juan);
        // Lucas
      Coleccion coleccionLucas = new Coleccion();
      FiguritaIntercambiable interMessi   = new FiguritaIntercambiable(messi,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), "1000");
      FiguritaIntercambiable interLautaro   = new FiguritaIntercambiable(lautaro,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), "1000");
      FiguritaIntercambiable interGriezmann1   = new FiguritaIntercambiable(griezmann,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), "1000");
      FiguritaIntercambiable interMbappe   = new FiguritaIntercambiable(mbappe,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), "1000");
      FiguritaIntercambiable interDiMaria = new FiguritaIntercambiable(diMaria, 2, 2,List.of(MetodoIntercambio.SUBASTA),     "1000");
      coleccionLucas.getRepetidas().add(interMessi);
      coleccionLucas.getRepetidas().add(interDiMaria);
      coleccionLucas.getRepetidas().add(interLautaro);
      coleccionLucas.getRepetidas().add(interGriezmann1);
      coleccionLucas.getRepetidas().add(interMbappe);
      coleccionLucas.getFaltantes().add(mbappe);
      coleccionLucas.getFaltantes().add(vinicius);
      colecciones.guardar(coleccionLucas);


      user = new Usuario("u-1000",  Rol.USUARIO,"lucas_fis","gordo123");
      usuarios.guardar(user);
      Perfil lucas = Perfil.builder()
          .id("1000").usuario(user)
          .nombre("lucas").coleccion(coleccionLucas)
          .mediosDeContacto(telegram("@lucas")).build();

      Calificacion calificacion = new Calificacion("40002", juan, lucas, 4, "dasda", "612431", MetodoIntercambio.INTERCAMBIO);
      List<Calificacion> calificaciones = new ArrayList<>();
      calificaciones.add(calificacion);
      this.calificaciones.guardar(calificacion);
      perfiles.guardar(lucas);

      // Sofía
      Coleccion coleccionSofia = new Coleccion();
      FiguritaIntercambiable interMbappe2    = new FiguritaIntercambiable(mbappe,    2, List.of(MetodoIntercambio.INTERCAMBIO), "1001");
      FiguritaIntercambiable interGriezmann = new FiguritaIntercambiable(griezmann, 1, List.of(MetodoIntercambio.SUBASTA),     "1001");
      FiguritaIntercambiable interNeymar = new FiguritaIntercambiable(neymar, 1, List.of(MetodoIntercambio.INTERCAMBIO), "1001");
      coleccionSofia.getRepetidas().add(interMbappe2);
      coleccionSofia.getRepetidas().add(interGriezmann);
      coleccionSofia.getRepetidas().add(interNeymar);
      coleccionSofia.getFaltantes().add(messi);
      coleccionSofia.getFaltantes().add(lautaro);
      colecciones.guardar(coleccionSofia);
      user = new Usuario("u-1001", Rol.USUARIO,"sofia_ape","password");
      usuarios.guardar(user);

      perfiles.guardar(Perfil.builder()
        .id("1001").usuario(user)
        .nombre("Sofía").coleccion(coleccionSofia)
        .mediosDeContacto(telegram("@sofia")).build());

      // Matías
      Coleccion coleccionMatias = new Coleccion();
      FiguritaIntercambiable interVinicius = new FiguritaIntercambiable(vinicius, 1, List.of(MetodoIntercambio.INTERCAMBIO), "1002");
      coleccionMatias.getRepetidas().add(interVinicius);
      coleccionMatias.getFaltantes().add(pedri);
      coleccionMatias.getFaltantes().add(kroos);
      colecciones.guardar(coleccionMatias);
      user = new Usuario("u-1002",  Rol.USUARIO,"mati_crim","wordpass");
      usuarios.guardar(user);
      perfiles.guardar(Perfil.builder()
        .id("1002").usuario(user)
        .nombre("Matías").coleccion(coleccionMatias)
        .mediosDeContacto(telegram("@matias")).build());


    }

    private void cargarCalificaciones() {
        Perfil lucas  = perfiles.buscarPorId("1000");
        Perfil sofia  = perfiles.buscarPorId("1001");
        Perfil matias = perfiles.buscarPorId("1002");
        Perfil juan   = perfiles.buscarPorId("1003");

      if (lucas == null || sofia == null || matias == null || juan == null) return;

        // Lucas: recibe 5 y 4 → promedio 4.5 → 5 estrellas
      lucas.agregarNuevaCalificacion(new Calificacion("C-1", sofia, lucas, 5, "Excelente trato, muy rápido", "2000", MetodoIntercambio.INTERCAMBIO));
      lucas.agregarNuevaCalificacion(new Calificacion("C-2", matias, lucas, 4, "Todo bien, lo recomiendo", "2002", MetodoIntercambio.INTERCAMBIO));

        // Sofía: recibe 4, 3 y 4 → promedio 3.67 → 4 estrellas
      sofia.agregarNuevaCalificacion(new Calificacion("C-3", lucas, sofia,  4, "Buena experiencia", "2000", MetodoIntercambio.INTERCAMBIO));
      sofia.agregarNuevaCalificacion(new Calificacion("C-4", matias, sofia, 3, "Normal, sin problemas", "2001", MetodoIntercambio.INTERCAMBIO));
      sofia.agregarNuevaCalificacion(new Calificacion("C-5", juan, sofia, 4, "Respondió rápido", "3000", MetodoIntercambio.SUBASTA));

        // Matías: recibe 2 y 3 → promedio 2.5 → 3 estrellas
      matias.agregarNuevaCalificacion(new Calificacion("C-6", lucas, matias,  2, "Tardó bastante en responder", "2002", MetodoIntercambio.INTERCAMBIO));
      matias.agregarNuevaCalificacion(new Calificacion("C-7", sofia, matias,  3, "Aceptable", "2001", MetodoIntercambio.INTERCAMBIO));

        // Juan: recibe 1 y 2 → promedio 1.5 → 2 estrellas
      juan.agregarNuevaCalificacion(new Calificacion("C-8", lucas, juan,  1, "No cumplió con el intercambio", "3001", MetodoIntercambio.SUBASTA));
      juan.agregarNuevaCalificacion(new Calificacion("C-9", sofia, juan,  2, "Mala comunicación", "3000", MetodoIntercambio.SUBASTA));
    }

    private void cargarPropuestas(Figurita messi, Figurita diMaria,
                                  Figurita griezmann, Figurita mbappe, Figurita vinicius) {
        Perfil lucas  = perfiles.buscarPorId("1000");
        Perfil sofia  = perfiles.buscarPorId("1001");
        Perfil matias = perfiles.buscarPorId("1002");

      List<Figurita> figuritas = new ArrayList<>();
      figuritas.add(messi);

      propuestas.guardar(propuesta("2000", lucas, sofia, figuritas, mbappe, EstadoProceso.PENDIENTE));

      figuritas = new ArrayList<>();
      figuritas.add(griezmann);
      propuestas.guardar(propuesta("2001", sofia, matias, figuritas, vinicius, EstadoProceso.ACEPTADO));

      figuritas = new ArrayList<>();
      figuritas.add(vinicius);
      propuestas.guardar(propuesta("2002", matias, lucas, figuritas, diMaria, EstadoProceso.RECHAZADO));
    }

    private void cargarSubastas(Figurita messi, Figurita diMaria, Figurita lautaro,
                                Figurita mbappe, Figurita griezmann, Figurita vinicius,
                                Figurita pedri, Figurita kroos, Figurita neymar) {

        Perfil lucas  = perfiles.buscarPorId("1000");
        Perfil sofia  = perfiles.buscarPorId("1001");
        Perfil matias = perfiles.buscarPorId("1002");
        Perfil juan   = perfiles.buscarPorId("1003");
        if (lucas == null) throw new RuntimeException("Lucas es null");
        if (sofia == null) throw new RuntimeException("Sofía es null");
        if (matias == null) throw new RuntimeException("Matías es null");
        if (juan == null) throw new RuntimeException("Juan es null");

        // ─── MIS SUBASTAS (autor = Lucas) ────────────────────────────────────────

        // id=1 | Activa, cierra en ~45 min, 3 ofertas
        Propuesta ofertaSofia = Propuesta.builder()
            .id("o1").autor(sofia)
            .destinatario(lucas)
            .figuritasOfrecidas(List.of(neymar, vinicius))
            .figuritaBuscada(mbappe)
            .build();

        Propuesta ofertaPedro = Propuesta.builder()
            .id("o2").autor(matias)
            .destinatario(lucas)
            .figuritasOfrecidas(List.of(pedri, kroos))
            .figuritaBuscada(mbappe)
            .build();

        Propuesta ofertaLu = Propuesta.builder()
            .id("o3").autor(juan)
            .destinatario(lucas)
            .figuritasOfrecidas(List.of(griezmann, lautaro))
            .figuritaBuscada(mbappe)
            .build();

        ofertaSofia.seleccionar(lucas.getId());

        Subasta subasta1 = Subasta.builder()
            .id("1").autor(lucas)
            .fechaInicio(LocalDateTime.now())
            .fechaCierre(LocalDateTime.now().plusMinutes(45))
            .figuritaSubastada(mbappe)
            .ofertas(new ArrayList<>(List.of(ofertaSofia, ofertaPedro, ofertaLu)))
            .build();

        subastas.guardar(subasta1);

        // id=2 | Activa, cierra en 2 días, sin ofertas
        Subasta subasta2 = Subasta.builder()
            .id("2").autor(lucas)
            .fechaInicio(LocalDateTime.now())
            .fechaCierre(LocalDateTime.now().plusDays(2))
            .figuritaSubastada(pedri)
            .build();

        subastas.guardar(subasta2);

        // id=3 | Finalizada hace 2 días, ganador: matias, sin calificar
        Propuesta ofertaGanadora3 = Propuesta.builder()
            .id("o4").autor(matias)
            .destinatario(lucas)
            .figuritasOfrecidas(List.of(messi, lautaro))
            .figuritaBuscada(diMaria)
            .build();
        Subasta subasta3 = Subasta.builder()
            .id("3").autor(lucas)
            .fechaInicio(LocalDateTime.now().minusDays(2))
            .fechaCierre(LocalDateTime.now())
            .figuritaSubastada(diMaria)
            .ofertas(new ArrayList<>(List.of(ofertaGanadora3)))
            .build();

        ofertaGanadora3.aceptar(lucas.getId());
        subastas.guardar(subasta3);

        // id=7 | Finalizada hace 5 días, ganador: sofia, ya calificada
        Propuesta ofertaGanadora7 = Propuesta.builder()
            .id("o5").autor(sofia)
            .destinatario(lucas)
            .figuritasOfrecidas(List.of(pedri))
            .figuritaBuscada(griezmann)
            .build();
        Subasta subasta7 = Subasta.builder()
            .id("7").autor(lucas)
            .fechaInicio(LocalDateTime.now().minusDays(5))
            .fechaCierre(LocalDateTime.now())
            .figuritaSubastada(griezmann)
            .ofertas(new ArrayList<>(List.of(ofertaGanadora7)))
            .build();
        subastas.guardar(subasta7);

        // ─── SUBASTAS DONDE LUCAS PARTICIPÓ (autor = otro perfil) ────────────────

        // id=4 | Activa, cierra en 2h, oferta de lucas SELECCIONADA
        Propuesta ofertaLucas4 = Propuesta.builder()
            .id("o6").autor(lucas)
            .destinatario(sofia)
            .figuritasOfrecidas(List.of(griezmann, kroos))
            .figuritaBuscada(vinicius)
            .build();
        Subasta subasta4 = Subasta.builder()
            .id("4").autor(sofia)
            .fechaInicio(LocalDateTime.now())
            .fechaCierre(LocalDateTime.now().plusHours(2))
            .figuritaSubastada(vinicius)
            .ofertas(new ArrayList<>(List.of(ofertaLucas4)))
            .build();
        ofertaLucas4.seleccionar(sofia.getId());
        subastas.guardar(subasta4);

        // id=5 | Activa, cierra en 1 día, oferta de lucas RECHAZADA
        Propuesta ofertaLucas5 = Propuesta.builder()
            .id("o7").autor(lucas)
            .destinatario(matias)
            .figuritasOfrecidas(List.of(diMaria, messi))
            .figuritaBuscada(messi)
            .build();
        Subasta subasta5 = Subasta.builder()
            .id("5").autor(matias)
            .fechaInicio(LocalDateTime.now())
            .fechaCierre(LocalDateTime.now().plusDays(1))
            .figuritaSubastada(messi)
            .ofertas(new ArrayList<>(List.of(ofertaLucas5)))
            .build();

        subastas.guardar(subasta5);

        // id=8 | Finalizada hace 5 días, oferta de lucas ACEPTADA, ya calificada
        Propuesta ofertaLucas8 = Propuesta.builder()
            .id("o9").autor(lucas)
            .destinatario(sofia)
            .figuritasOfrecidas(List.of(kroos))
            .figuritaBuscada(griezmann)
            .build();
        Propuesta ofertaJuan1 = Propuesta.builder()
            .id("o10").autor(juan)
            .destinatario(sofia)
            .figuritasOfrecidas(List.of(kroos))
            .figuritaBuscada(griezmann)
            .build();
        Subasta subasta8 = Subasta.builder().id("8").autor(sofia)
            .fechaInicio(LocalDateTime.now().minusDays(5))
            .fechaCierre(LocalDateTime.now())
            .figuritaSubastada(griezmann)
            .ofertas(new ArrayList<>(List.of(ofertaLucas8,ofertaJuan1)))
            .build();

        ofertaJuan1.aceptar(sofia.getId());
        Calificacion calificacion = new Calificacion("202914", lucas, sofia,  2, "asda", "8",MetodoIntercambio.SUBASTA);
        sofia.agregarNuevaCalificacion(calificacion);
        subastas.guardar(subasta8);
        calificaciones.guardar(calificacion);
        perfiles.guardar(sofia);

        // ─── SUBASTAS DONDE LUCAS NO PARTICIPÓ ────────────────

        // id=6 | Finalizada hace 5 días, oferta de lucas ACEPTADA, sin calificar
        Subasta subasta6 = Subasta.builder().id("6").autor(juan)
            .fechaInicio(LocalDateTime.now().minusDays(5))
            .fechaCierre(LocalDateTime.now())
            .figuritaSubastada(neymar)
            .build();
        subastas.guardar(subasta6);
    }
}