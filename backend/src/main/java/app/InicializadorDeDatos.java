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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InicializadorDeDatos implements CommandLineRunner {

    private final RepositorioPerfiles perfiles;
    private final RepositorioPropuestas propuestas;
    private final RepositorioSubastas subastas;
    private final RepositorioFiguritas figuritas;
    private final RepositorioColecciones colecciones;
    private final RepositorioFiguritasIntercambiables intercambiables;

    public InicializadorDeDatos(RepositorioPerfiles perfiles,
                                RepositorioPropuestas propuestas,
                                RepositorioSubastas subastas,
                                RepositorioColecciones colecciones,
                                RepositorioFiguritas figuritas,
                                RepositorioFiguritasIntercambiables intercambiables) {
        this.perfiles = perfiles;
        this.propuestas = propuestas;
        this.subastas = subastas;
        this.colecciones = colecciones;
        this.figuritas = figuritas;
        this.intercambiables = intercambiables;
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
        Figurita griezmann = new Figurita("FRA-7", 7, "Griezmann", Seleccion.FRANCIA, "Mediocampista");
        Figurita vinicius = new Figurita("BRA-10", 10, "Vinicius", Seleccion.BRASIL, "Extremo");
        Figurita pedri = new Figurita("ESP-10", 10, "Pedri", Seleccion.ESPAÑA, "Mediocampista");
        Figurita kroos = new Figurita("GER-8", 8, "Kroos", Seleccion.ALEMANIA, "Mediocampista");
      Figurita neymar = new Figurita("BRA-11", 10, "Neymar", Seleccion.BRASIL, "Delantero");

        figuritas.guardar(messi);
        figuritas.guardar(diMaria);
        figuritas.guardar(lautaro);
        figuritas.guardar(vinicius);
      figuritas.guardar(neymar);

        cargarPerfiles(messi, diMaria, lautaro, mbappe, griezmann, vinicius, pedri, kroos, neymar);
        cargarCalificaciones();
        cargarPropuestas(messi, diMaria, griezmann, mbappe, vinicius);
        cargarSubastas(griezmann, vinicius);
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
        FiguritaIntercambiable interMessi   = new FiguritaIntercambiable(messi,   3, List.of(MetodoIntercambio.INTERCAMBIO), "1000");
        FiguritaIntercambiable interDiMaria = new FiguritaIntercambiable(diMaria, 2, List.of(MetodoIntercambio.SUBASTA),     "1000");
        coleccionLucas.getRepetidas().add(interMessi);
        coleccionLucas.getRepetidas().add(interDiMaria);
        coleccionLucas.getFaltantes().add(mbappe);
        coleccionLucas.getFaltantes().add(vinicius);
        intercambiables.guardar(interMessi);
        intercambiables.guardar(interDiMaria);
        colecciones.guardar(coleccionLucas);
        perfiles.guardar(new Perfil("1000", new Usuario("u-1000",  Rol.USUARIO), "Lucas",
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
        perfiles.guardar(new Perfil("1001", new Usuario("u-1001", Rol.USUARIO), "Sofía",
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
        perfiles.guardar(new Perfil("1002", new Usuario("u-1002",  Rol.USUARIO), "Matías",
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
        perfiles.guardar(new Perfil("1003", new Usuario("u-1003",  Rol.USUARIO), "Juan",
            coleccionJuan, telegram("@juan"), new ArrayList<>()));
    }

    private void cargarCalificaciones() {
        Perfil lucas  = perfiles.buscarPorId("1000");
        Perfil sofia  = perfiles.buscarPorId("1001");
        Perfil matias = perfiles.buscarPorId("1002");
        Perfil juan   = perfiles.buscarPorId("1003");

        // Lucas: recibe 5 y 4 → promedio 4.5 → 5 estrellas
      lucas.getCalificaciones().add(new Calificacion("C-1", sofia, 5, "Excelente trato, muy rápido", null, null));
      lucas.getCalificaciones().add(new Calificacion("C-2", matias, 4, "Todo bien, lo recomiendo", null, null));

        // Sofía: recibe 4, 3 y 4 → promedio 3.67 → 4 estrellas
      sofia.getCalificaciones().add(new Calificacion("C-3", lucas, 4, "Buena experiencia", null, null));
      sofia.getCalificaciones().add(new Calificacion("C-4", matias, 3, "Normal, sin problemas", null, null));
      sofia.getCalificaciones().add(new Calificacion("C-5", juan, 4, "Respondió rápido", null, null));

        // Matías: recibe 2 y 3 → promedio 2.5 → 3 estrellas
      matias.getCalificaciones().add(new Calificacion("C-6", lucas, 2, "Tardó bastante en responder", null, null));
      matias.getCalificaciones().add(new Calificacion("C-7", sofia, 3, "Aceptable", null, null));

        // Juan: recibe 1 y 2 → promedio 1.5 → 2 estrellas
      juan.getCalificaciones().add(new Calificacion("C-8", lucas, 1, "No cumplió con el intercambio", null, null));
      juan.getCalificaciones().add(new Calificacion("C-9", sofia, 2, "Mala comunicación", null, null));
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

    private void cargarSubastas(Figurita griezmann, Figurita vinicius) {
        Perfil sofia  = perfiles.buscarPorId("1001");
        Perfil matias = perfiles.buscarPorId("1002");

      List<Figurita> figuritas = new ArrayList<>();
      figuritas.add(vinicius);
      Propuesta unaOferta = new Propuesta("4000", matias, sofia, figuritas, griezmann);
      figuritas = new ArrayList<>();
      figuritas.add(vinicius);
      Propuesta otraOfertaDistinta = new Propuesta("4000", matias, sofia, figuritas, griezmann);

      List<Propuesta> propuestas = new ArrayList<>();
      propuestas.add(unaOferta);
      propuestas.add(otraOfertaDistinta);

      Subasta unaSubasta = new Subasta("3000", sofia,
          LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(1).plusMinutes(20).plusSeconds(33),
          griezmann, propuestas, new ArrayList<>(), 0, false);

      subastas.guardar(unaSubasta);

        subastas.guardar(new Subasta("3001", matias,
            LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1),
            vinicius, new ArrayList<>(), new ArrayList<>(), 0, false));
    }
}