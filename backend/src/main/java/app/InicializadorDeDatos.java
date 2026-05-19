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
import app.repositories.RepositorioColecciones;
import app.repositories.RepositorioFiguritas;
import app.repositories.RepositorioFiguritasIntercambiables;
import app.repositories.RepositorioPerfiles;
import app.repositories.RepositorioPropuestas;
import app.repositories.RepositorioSubastas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import app.repositories.RepositorioUsuario;
import app.repositories.implMongo.RepositorioUsuarioMongo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


public class InicializadorDeDatos implements CommandLineRunner {

    private final RepositorioPerfiles perfiles;
    private final RepositorioPropuestas propuestas;
    private final RepositorioSubastas subastas;
    private final RepositorioFiguritas figuritas;
    private final RepositorioColecciones colecciones;
    private final RepositorioFiguritasIntercambiables intercambiables;
    private final RepositorioUsuario sesion;

    public InicializadorDeDatos(RepositorioPerfiles perfiles,
                                RepositorioPropuestas propuestas,
                                RepositorioSubastas subastas,
                                RepositorioColecciones colecciones,
                                RepositorioFiguritas figuritas,
                                RepositorioFiguritasIntercambiables intercambiables,
                                RepositorioUsuario sesion) {
        this.perfiles = perfiles;
        this.propuestas = propuestas;
        this.subastas = subastas;
        this.colecciones = colecciones;
        this.figuritas = figuritas;
        this.intercambiables = intercambiables;
        this.sesion = sesion;
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
                intercambiables.guardar(new FiguritaIntercambiable(fig, 2, List.of(metodo), perfilId));
                contador++;
            }
        }
    }

    private void cargarPerfiles(Figurita messi, Figurita diMaria, Figurita lautaro,
                                Figurita mbappe, Figurita griezmann, Figurita vinicius,
                                Figurita pedri, Figurita kroos, Figurita neymar) {
        // Lucas
        Coleccion coleccionLucas = new Coleccion();
        coleccionLucas.setId("1");
        FiguritaIntercambiable interMessi   = new FiguritaIntercambiable(messi,   3, 1,List.of(MetodoIntercambio.INTERCAMBIO), "1000");
        FiguritaIntercambiable interDiMaria = new FiguritaIntercambiable(diMaria, 2, 2,List.of(MetodoIntercambio.SUBASTA),     "1000");
        coleccionLucas.getRepetidas().add(interMessi);
        coleccionLucas.getRepetidas().add(interDiMaria);
        coleccionLucas.getFaltantes().add(mbappe);
        coleccionLucas.getFaltantes().add(vinicius);
        intercambiables.guardar(interMessi);
        intercambiables.guardar(interDiMaria);
        colecciones.guardar(coleccionLucas);
        Usuario user = new Usuario("u-1000",  Rol.USUARIO,"lucas_fis","gordo123");
        sesion.guardar(user);
        perfiles.guardar(new Perfil(user, "Lucas",
            coleccionLucas, telegram("@lucas"), new ArrayList<>()));

        // Sofía
        Coleccion coleccionSofia = new Coleccion();
        coleccionSofia.setId("2");
        FiguritaIntercambiable interMbappe    = new FiguritaIntercambiable(mbappe,    2, List.of(MetodoIntercambio.INTERCAMBIO), "1001");
        FiguritaIntercambiable interGriezmann = new FiguritaIntercambiable(griezmann, 1, List.of(MetodoIntercambio.SUBASTA),     "1001");
      FiguritaIntercambiable interNeymar = new FiguritaIntercambiable(neymar, 1, List.of(MetodoIntercambio.INTERCAMBIO), "1001");
        coleccionSofia.getRepetidas().add(interMbappe);
        coleccionSofia.getRepetidas().add(interGriezmann);
      coleccionSofia.getRepetidas().add(interNeymar);
        coleccionSofia.getFaltantes().add(messi);
        coleccionSofia.getFaltantes().add(lautaro);
        intercambiables.guardar(interMbappe);
        intercambiables.guardar(interGriezmann);
        colecciones.guardar(coleccionSofia);
        user = new Usuario("u-1001", Rol.USUARIO,"sofia_ape","password");
        sesion.guardar(user);
        perfiles.guardar(new Perfil(user, "Sofía",
            coleccionSofia, telegram("@sofia"), new ArrayList<>()));

        // Matías
        Coleccion coleccionMatias = new Coleccion();
        coleccionMatias.setId("3");
        FiguritaIntercambiable interVinicius = new FiguritaIntercambiable(vinicius, 1, List.of(MetodoIntercambio.INTERCAMBIO), "1002");
        coleccionMatias.getRepetidas().add(interVinicius);
        coleccionMatias.getFaltantes().add(pedri);
        coleccionMatias.getFaltantes().add(kroos);
        intercambiables.guardar(interVinicius);
        colecciones.guardar(coleccionMatias);
        user = new Usuario("u-1002",  Rol.USUARIO,"mati_crim","wordpass");
        sesion.guardar(user);
        perfiles.guardar(new Perfil(user, "Matías",
            coleccionMatias, telegram("@matias"), new ArrayList<>()));

        // Juan
        Coleccion coleccionJuan = new Coleccion();
        coleccionJuan.setId("4");
        FiguritaIntercambiable interPedri = new FiguritaIntercambiable(pedri, 1, List.of(MetodoIntercambio.INTERCAMBIO), "1003");
        coleccionJuan.getRepetidas().add(interPedri);
        coleccionJuan.getFaltantes().add(pedri);
        coleccionJuan.getFaltantes().add(kroos);
        intercambiables.guardar(interPedri);
        colecciones.guardar(coleccionJuan);
        user =  new Usuario("u-1003",  Rol.USUARIO, "juan_jose","una contrasenia");
        sesion.guardar(user);
        perfiles.guardar(new Perfil(user, "Juan",
            coleccionJuan, telegram("@juan"), new ArrayList<>()));
    }

    private void cargarCalificaciones() {
        Perfil lucas  = perfiles.buscarPorId("1000");
        Perfil sofia  = perfiles.buscarPorId("1001");
        Perfil matias = perfiles.buscarPorId("1002");
        Perfil juan   = perfiles.buscarPorId("1003");

      if (lucas == null || sofia == null || matias == null || juan == null) return;

        // Lucas: recibe 5 y 4 → promedio 4.5 → 5 estrellas
      lucas.getCalificaciones().add(new Calificacion("C-1", sofia, 5, "Excelente trato, muy rápido", "2000", MetodoIntercambio.INTERCAMBIO));
      lucas.getCalificaciones().add(new Calificacion("C-2", matias, 4, "Todo bien, lo recomiendo", "2002", MetodoIntercambio.INTERCAMBIO));

        // Sofía: recibe 4, 3 y 4 → promedio 3.67 → 4 estrellas
      sofia.getCalificaciones().add(new Calificacion("C-3", lucas, 4, "Buena experiencia", "2000", MetodoIntercambio.INTERCAMBIO));
      sofia.getCalificaciones().add(new Calificacion("C-4", matias, 3, "Normal, sin problemas", "2001", MetodoIntercambio.INTERCAMBIO));
      sofia.getCalificaciones().add(new Calificacion("C-5", juan, 4, "Respondió rápido", "3000", MetodoIntercambio.SUBASTA));

        // Matías: recibe 2 y 3 → promedio 2.5 → 3 estrellas
      matias.getCalificaciones().add(new Calificacion("C-6", lucas, 2, "Tardó bastante en responder", "2002", MetodoIntercambio.INTERCAMBIO));
      matias.getCalificaciones().add(new Calificacion("C-7", sofia, 3, "Aceptable", "2001", MetodoIntercambio.INTERCAMBIO));

        // Juan: recibe 1 y 2 → promedio 1.5 → 2 estrellas
      juan.getCalificaciones().add(new Calificacion("C-8", lucas, 1, "No cumplió con el intercambio", "3001", MetodoIntercambio.SUBASTA));
      juan.getCalificaciones().add(new Calificacion("C-9", sofia, 2, "Mala comunicación", "3000", MetodoIntercambio.SUBASTA));
    }

    private void cargarPropuestas(Figurita messi, Figurita diMaria,
                                  Figurita griezmann, Figurita mbappe, Figurita vinicius) {
        Perfil lucas  = perfiles.buscarPorId("1000");
        Perfil sofia  = perfiles.buscarPorId("1001");
        Perfil matias = perfiles.buscarPorId("1002");

      List<Figurita> figuritas = new ArrayList<>();
      figuritas.add(messi);

      propuestas.guardar(propuesta("2000", lucas, sofia, figuritas, mbappe, EstadoProceso.PENDIENTE));
      propuestas.guardar(propuesta("2001", lucas, sofia, figuritas, mbappe, EstadoProceso.ACEPTADO));

      figuritas = new ArrayList<>();
      figuritas.add(griezmann);
      propuestas.guardar(propuesta("2001", sofia, matias, figuritas, vinicius, EstadoProceso.ACEPTADO));

      figuritas = new ArrayList<>();
      figuritas.add(vinicius);
      propuestas.guardar(propuesta("2002", matias, lucas, figuritas, diMaria, EstadoProceso.RECHAZADO));
      propuestas.guardar(propuesta("2003", matias, lucas, figuritas, diMaria, EstadoProceso.ACEPTADO));
      propuestas.guardar(propuesta("2004", matias, lucas, figuritas, diMaria, EstadoProceso.PENDIENTE));

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
        Propuesta ofertaSofia = new Propuesta("o1", sofia, lucas,
            List.of(neymar, vinicius), mbappe);
        Propuesta ofertaPedro = new Propuesta("o2", matias, lucas,
            List.of(pedri, kroos), mbappe);
        Propuesta ofertaLu = new Propuesta("o3", juan, lucas,
            List.of(griezmann, lautaro), mbappe);

        ofertaSofia.seleccionar(lucas);

        Subasta subasta1 = new Subasta("1", lucas,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(45),
            mbappe,
            new ArrayList<>(List.of(ofertaSofia, ofertaPedro, ofertaLu)),
            new ArrayList<>(), 0, false);
        subastas.guardar(subasta1);

        // id=2 | Activa, cierra en 2 días, sin ofertas
        Subasta subasta2 = new Subasta("2", lucas,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            pedri);
        subastas.guardar(subasta2);

        // id=3 | Finalizada hace 2 días, ganador: matias, sin calificar
        Propuesta ofertaGanadora3 = new Propuesta("o4", matias, lucas,
            List.of(messi, lautaro), diMaria);
        Subasta subasta3 = new Subasta("3", lucas,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(2),
            diMaria,
            new ArrayList<>(List.of(ofertaGanadora3)),
            new ArrayList<>(), 0, true);

        ofertaGanadora3.aceptar(lucas);
        subastas.guardar(subasta3);

        // id=7 | Finalizada hace 5 días, ganador: sofia, ya calificada
        Propuesta ofertaGanadora7 = new Propuesta("o5", sofia, lucas,
            List.of(pedri), griezmann);
        Subasta subasta7 = new Subasta("7", lucas,
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now().minusDays(5),
            griezmann,
            new ArrayList<>(List.of(ofertaGanadora7)),
            new ArrayList<>(), 0, true);
        subastas.guardar(subasta7);

        // ─── SUBASTAS DONDE LUCAS PARTICIPÓ (autor = otro perfil) ────────────────

        // id=4 | Activa, cierra en 2h, oferta de lucas SELECCIONADA
        Propuesta ofertaLucas4 = new Propuesta("o6", lucas, sofia,
            List.of(griezmann, kroos), vinicius);
        Subasta subasta4 = new Subasta("4", sofia,
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(2),
            vinicius,
            new ArrayList<>(List.of(ofertaLucas4)),
            new ArrayList<>(), 0, false);
        ofertaLucas4.seleccionar(sofia);
        subastas.guardar(subasta4);

        // id=5 | Activa, cierra en 1 día, oferta de lucas RECHAZADA
        Propuesta ofertaLucas5 = new Propuesta("o7", lucas, matias,
            List.of(diMaria, messi), messi);
        Subasta subasta5 = new Subasta("5", matias,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            messi,
            new ArrayList<>(List.of(ofertaLucas5)),
            new ArrayList<>(), 0, false);
        subastas.guardar(subasta5);

        // id=8 | Finalizada hace 5 días, oferta de lucas ACEPTADA, ya calificada
        Propuesta ofertaLucas8 = new Propuesta("o9", lucas, sofia,
            List.of(kroos), griezmann);
        Propuesta ofertaJuan1 = new Propuesta("o10", juan, sofia,
            List.of(kroos), griezmann);
        Subasta subasta8 = new Subasta("8", sofia,
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now().minusDays(5),
            griezmann,
            new ArrayList<>(List.of(ofertaLucas8,ofertaJuan1)),
            new ArrayList<>(), 0, true);
        ofertaJuan1.aceptar(sofia);
        sofia.agregarNuevaCalificacion(new Calificacion("202914", lucas, 2, "asda", "8",MetodoIntercambio.SUBASTA));
        subastas.guardar(subasta8);

        // ─── SUBASTAS DONDE LUCAS NO PARTICIPÓ ────────────────

        // id=6 | Finalizada hace 5 días, oferta de lucas ACEPTADA, sin calificar
        Subasta subasta6 = new Subasta("6", juan,
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now().minusDays(5),
            neymar,
            new ArrayList<>(),
            new ArrayList<>(), 0, true);
        subastas.guardar(subasta6);
    }
}