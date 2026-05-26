package app.config;

import app.model.entities.Figurita;
import app.model.entities.Seleccion;
import java.util.List;

public class FiguritasQatar2022 {
  public static List<Figurita> todas() {
    // ── ARGENTINA ────────────────────────────────────────────────────────────────
    Figurita arg01 = Figurita.builder().id("ARG-1").numero(1).jugador("Damián Martínez").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg02 = Figurita.builder().id("ARG-2").numero(2).jugador("Juan Foyth").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg03 = Figurita.builder().id("ARG-3").numero(3).jugador("Nicolás Tagliafico").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg04 = Figurita.builder().id("ARG-4").numero(4).jugador("Gonzalo Montiel").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg05 = Figurita.builder().id("ARG-5").numero(5).jugador("Lisandro Martínez").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg06 = Figurita.builder().id("ARG-6").numero(6).jugador("Germán Pezzella").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg07 = Figurita.builder().id("ARG-7").numero(7).jugador("Rodrigo De Paul").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg08 = Figurita.builder().id("ARG-8").numero(8).jugador("Enzo Fernández").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg09 = Figurita.builder().id("ARG-9").numero(9).jugador("Julián Álvarez").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg10 = Figurita.builder().id("ARG-10").numero(10).jugador("Lionel Messi").seleccion(Seleccion.ARGENTINA).build();
    Figurita arg11 = Figurita.builder().id("ARG-11").numero(11).jugador("Ángel Di María").seleccion(Seleccion.ARGENTINA).build();

// ── FRANCIA ──────────────────────────────────────────────────────────────────
    Figurita fra01 = Figurita.builder().id("FRA-1").numero(1).jugador("Hugo Lloris").seleccion(Seleccion.FRANCIA).build();
    Figurita fra02 = Figurita.builder().id("FRA-2").numero(2).jugador("Benjamin Pavard").seleccion(Seleccion.FRANCIA).build();
    Figurita fra03 = Figurita.builder().id("FRA-3").numero(3).jugador("Lucas Hernández").seleccion(Seleccion.FRANCIA).build();
    Figurita fra04 = Figurita.builder().id("FRA-4").numero(4).jugador("Raphaël Varane").seleccion(Seleccion.FRANCIA).build();
    Figurita fra05 = Figurita.builder().id("FRA-5").numero(5).jugador("Jules Koundé").seleccion(Seleccion.FRANCIA).build();
    Figurita fra06 = Figurita.builder().id("FRA-6").numero(6).jugador("Adrien Rabiot").seleccion(Seleccion.FRANCIA).build();
    Figurita fra07 = Figurita.builder().id("FRA-7").numero(7).jugador("Antoine Griezmann").seleccion(Seleccion.FRANCIA).build();
    Figurita fra08 = Figurita.builder().id("FRA-8").numero(8).jugador("Aurélien Tchouaméni").seleccion(Seleccion.FRANCIA).build();
    Figurita fra09 = Figurita.builder().id("FRA-9").numero(9).jugador("Olivier Giroud").seleccion(Seleccion.FRANCIA).build();
    Figurita fra10 = Figurita.builder().id("FRA-10").numero(10).jugador("Kylian Mbappé").seleccion(Seleccion.FRANCIA).build();
    Figurita fra11 = Figurita.builder().id("FRA-11").numero(11).jugador("Ousmane Dembélé").seleccion(Seleccion.FRANCIA).build();

// ── BRASIL ───────────────────────────────────────────────────────────────────
    Figurita bra01 = Figurita.builder().id("BRA-1").numero(1).jugador("Alisson").seleccion(Seleccion.BRASIL).build();
    Figurita bra02 = Figurita.builder().id("BRA-2").numero(2).jugador("Danilo").seleccion(Seleccion.BRASIL).build();
    Figurita bra03 = Figurita.builder().id("BRA-3").numero(3).jugador("Alex Sandro").seleccion(Seleccion.BRASIL).build();
    Figurita bra04 = Figurita.builder().id("BRA-4").numero(4).jugador("Marquinhos").seleccion(Seleccion.BRASIL).build();
    Figurita bra05 = Figurita.builder().id("BRA-5").numero(5).jugador("Thiago Silva").seleccion(Seleccion.BRASIL).build();
    Figurita bra06 = Figurita.builder().id("BRA-6").numero(6).jugador("Fred").seleccion(Seleccion.BRASIL).build();
    Figurita bra07 = Figurita.builder().id("BRA-7").numero(7).jugador("Lucas Paquetá").seleccion(Seleccion.BRASIL).build();
    Figurita bra08 = Figurita.builder().id("BRA-8").numero(8).jugador("Casemiro").seleccion(Seleccion.BRASIL).build();
    Figurita bra09 = Figurita.builder().id("BRA-9").numero(9).jugador("Richarlison").seleccion(Seleccion.BRASIL).build();
    Figurita bra10 = Figurita.builder().id("BRA-10").numero(10).jugador("Neymar Jr.").seleccion(Seleccion.BRASIL).build();
    Figurita bra11 = Figurita.builder().id("BRA-11").numero(11).jugador("Vinícius Jr.").seleccion(Seleccion.BRASIL).build();

// ── ESPAÑA ───────────────────────────────────────────────────────────────────
    Figurita esp01 = Figurita.builder().id("ESP-1").numero(1).jugador("Unai Simón").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp02 = Figurita.builder().id("ESP-2").numero(2).jugador("César Azpilicueta").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp03 = Figurita.builder().id("ESP-3").numero(3).jugador("Jordi Alba").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp04 = Figurita.builder().id("ESP-4").numero(4).jugador("Pau Torres").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp05 = Figurita.builder().id("ESP-5").numero(5).jugador("Aymeric Laporte").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp06 = Figurita.builder().id("ESP-6").numero(6).jugador("Gavi").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp07 = Figurita.builder().id("ESP-7").numero(7).jugador("Álvaro Morata").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp08 = Figurita.builder().id("ESP-8").numero(8).jugador("Koke").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp09 = Figurita.builder().id("ESP-9").numero(9).jugador("Marco Asensio").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp10 = Figurita.builder().id("ESP-10").numero(10).jugador("Pedri").seleccion(Seleccion.ESPAÑA).build();
    Figurita esp11 = Figurita.builder().id("ESP-11").numero(11).jugador("Ferran Torres").seleccion(Seleccion.ESPAÑA).build();

// ── ALEMANIA ─────────────────────────────────────────────────────────────────
    Figurita ger01 = Figurita.builder().id("GER-1").numero(1).jugador("Manuel Neuer").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger02 = Figurita.builder().id("GER-2").numero(2).jugador("Benjamin Henrichs").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger03 = Figurita.builder().id("GER-3").numero(3).jugador("David Raum").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger04 = Figurita.builder().id("GER-4").numero(4).jugador("Antonio Rüdiger").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger05 = Figurita.builder().id("GER-5").numero(5).jugador("Niklas Süle").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger06 = Figurita.builder().id("GER-6").numero(6).jugador("Joshua Kimmich").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger07 = Figurita.builder().id("GER-7").numero(7).jugador("Kai Havertz").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger08 = Figurita.builder().id("GER-8").numero(8).jugador("Leon Goretzka").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger09 = Figurita.builder().id("GER-9").numero(9).jugador("Niclas Füllkrug").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger10 = Figurita.builder().id("GER-10").numero(10).jugador("Serge Gnabry").seleccion(Seleccion.ALEMANIA).build();
    Figurita ger11 = Figurita.builder().id("GER-11").numero(11).jugador("Leroy Sané").seleccion(Seleccion.ALEMANIA).build();

// ── PORTUGAL ─────────────────────────────────────────────────────────────────
    Figurita por01 = Figurita.builder().id("POR-1").numero(1).jugador("Diogo Costa").seleccion(Seleccion.PORTUGAL).build();
    Figurita por02 = Figurita.builder().id("POR-2").numero(2).jugador("João Cancelo").seleccion(Seleccion.PORTUGAL).build();
    Figurita por03 = Figurita.builder().id("POR-3").numero(3).jugador("Raphaël Guerreiro").seleccion(Seleccion.PORTUGAL).build();
    Figurita por04 = Figurita.builder().id("POR-4").numero(4).jugador("Rúben Dias").seleccion(Seleccion.PORTUGAL).build();
    Figurita por05 = Figurita.builder().id("POR-5").numero(5).jugador("Danilo Pereira").seleccion(Seleccion.PORTUGAL).build();
    Figurita por06 = Figurita.builder().id("POR-6").numero(6).jugador("William Carvalho").seleccion(Seleccion.PORTUGAL).build();
    Figurita por07 = Figurita.builder().id("POR-7").numero(7).jugador("Cristiano Ronaldo").seleccion(Seleccion.PORTUGAL).build();
    Figurita por08 = Figurita.builder().id("POR-8").numero(8).jugador("Bruno Fernandes").seleccion(Seleccion.PORTUGAL).build();
    Figurita por09 = Figurita.builder().id("POR-9").numero(9).jugador("André Silva").seleccion(Seleccion.PORTUGAL).build();
    Figurita por10 = Figurita.builder().id("POR-10").numero(10).jugador("Bernardo Silva").seleccion(Seleccion.PORTUGAL).build();
    Figurita por11 = Figurita.builder().id("POR-11").numero(11).jugador("João Félix").seleccion(Seleccion.PORTUGAL).build();

// ── PAÍSES BAJOS ─────────────────────────────────────────────────────────────
    Figurita ned01 = Figurita.builder().id("NED-1").numero(1).jugador("Andries Noppert").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned02 = Figurita.builder().id("NED-2").numero(2).jugador("Denzel Dumfries").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned03 = Figurita.builder().id("NED-3").numero(3).jugador("Daley Blind").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned04 = Figurita.builder().id("NED-4").numero(4).jugador("Virgil van Dijk").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned05 = Figurita.builder().id("NED-5").numero(5).jugador("Nathan Aké").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned06 = Figurita.builder().id("NED-6").numero(6).jugador("Frenkie de Jong").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned07 = Figurita.builder().id("NED-7").numero(7).jugador("Steven Bergwijn").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned08 = Figurita.builder().id("NED-8").numero(8).jugador("Teun Koopmeiners").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned09 = Figurita.builder().id("NED-9").numero(9).jugador("Memphis Depay").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned10 = Figurita.builder().id("NED-10").numero(10).jugador("Davy Klaassen").seleccion(Seleccion.PAISES_BAJOS).build();
    Figurita ned11 = Figurita.builder().id("NED-11").numero(11).jugador("Cody Gakpo").seleccion(Seleccion.PAISES_BAJOS).build();

// ── CROACIA ──────────────────────────────────────────────────────────────────
    Figurita cro01 = Figurita.builder().id("CRO-1").numero(1).jugador("Dominik Livaković").seleccion(Seleccion.CROACIA).build();
    Figurita cro02 = Figurita.builder().id("CRO-2").numero(2).jugador("Josip Juranović").seleccion(Seleccion.CROACIA).build();
    Figurita cro03 = Figurita.builder().id("CRO-3").numero(3).jugador("Borna Sosa").seleccion(Seleccion.CROACIA).build();
    Figurita cro04 = Figurita.builder().id("CRO-4").numero(4).jugador("Joško Gvardiol").seleccion(Seleccion.CROACIA).build();
    Figurita cro05 = Figurita.builder().id("CRO-5").numero(5).jugador("Dejan Lovren").seleccion(Seleccion.CROACIA).build();
    Figurita cro06 = Figurita.builder().id("CRO-6").numero(6).jugador("Marcelo Brozović").seleccion(Seleccion.CROACIA).build();
    Figurita cro07 = Figurita.builder().id("CRO-7").numero(7).jugador("Ivan Perišić").seleccion(Seleccion.CROACIA).build();
    Figurita cro08 = Figurita.builder().id("CRO-8").numero(8).jugador("Mateo Kovačić").seleccion(Seleccion.CROACIA).build();
    Figurita cro09 = Figurita.builder().id("CRO-9").numero(9).jugador("Andrej Kramarić").seleccion(Seleccion.CROACIA).build();
    Figurita cro10 = Figurita.builder().id("CRO-10").numero(10).jugador("Luka Modrić").seleccion(Seleccion.CROACIA).build();
    Figurita cro11 = Figurita.builder().id("CRO-11").numero(11).jugador("Nikola Vlašić").seleccion(Seleccion.CROACIA).build();

// ── MARRUECOS ────────────────────────────────────────────────────────────────
    Figurita mar01 = Figurita.builder().id("MAR-1").numero(1).jugador("Yassine Bounou").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar02 = Figurita.builder().id("MAR-2").numero(2).jugador("Achraf Hakimi").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar03 = Figurita.builder().id("MAR-3").numero(3).jugador("Noussair Mazraoui").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar04 = Figurita.builder().id("MAR-4").numero(4).jugador("Romain Saïss").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar05 = Figurita.builder().id("MAR-5").numero(5).jugador("Nayef Aguerd").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar06 = Figurita.builder().id("MAR-6").numero(6).jugador("Sofyan Amrabat").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar07 = Figurita.builder().id("MAR-7").numero(7).jugador("Hakim Ziyech").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar08 = Figurita.builder().id("MAR-8").numero(8).jugador("Azzedine Ounahi").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar09 = Figurita.builder().id("MAR-9").numero(9).jugador("Youssef En-Nesyri").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar10 = Figurita.builder().id("MAR-10").numero(10).jugador("Abdelhamid Sabiri").seleccion(Seleccion.MARRUECOS).build();
    Figurita mar11 = Figurita.builder().id("MAR-11").numero(11).jugador("Sofiane Boufal").seleccion(Seleccion.MARRUECOS).build();

// ── INGLATERRA ───────────────────────────────────────────────────────────────
    Figurita eng01 = Figurita.builder().id("ENG-1").numero(1).jugador("Jordan Pickford").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng02 = Figurita.builder().id("ENG-2").numero(2).jugador("Kyle Walker").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng03 = Figurita.builder().id("ENG-3").numero(3).jugador("Luke Shaw").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng04 = Figurita.builder().id("ENG-4").numero(4).jugador("John Stones").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng05 = Figurita.builder().id("ENG-5").numero(5).jugador("Harry Maguire").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng06 = Figurita.builder().id("ENG-6").numero(6).jugador("Declan Rice").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng07 = Figurita.builder().id("ENG-7").numero(7).jugador("Jack Grealish").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng08 = Figurita.builder().id("ENG-8").numero(8).jugador("Jude Bellingham").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng09 = Figurita.builder().id("ENG-9").numero(9).jugador("Harry Kane").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng10 = Figurita.builder().id("ENG-10").numero(10).jugador("Raheem Sterling").seleccion(Seleccion.INGLATERRA).build();
    Figurita eng11 = Figurita.builder().id("ENG-11").numero(11).jugador("Bukayo Saka").seleccion(Seleccion.INGLATERRA).build();

// ── SENEGAL ──────────────────────────────────────────────────────────────────
    Figurita sen01 = Figurita.builder().id("SEN-1").numero(1).jugador("Édouard Mendy").seleccion(Seleccion.SENEGAL).build();
    Figurita sen02 = Figurita.builder().id("SEN-2").numero(2).jugador("Youssouf Sabaly").seleccion(Seleccion.SENEGAL).build();
    Figurita sen03 = Figurita.builder().id("SEN-3").numero(3).jugador("Abdou Diallo").seleccion(Seleccion.SENEGAL).build();
    Figurita sen04 = Figurita.builder().id("SEN-4").numero(4).jugador("Kalidou Koulibaly").seleccion(Seleccion.SENEGAL).build();
    Figurita sen05 = Figurita.builder().id("SEN-5").numero(5).jugador("Pape Abou Cissé").seleccion(Seleccion.SENEGAL).build();
    Figurita sen06 = Figurita.builder().id("SEN-6").numero(6).jugador("Nampalys Mendy").seleccion(Seleccion.SENEGAL).build();
    Figurita sen07 = Figurita.builder().id("SEN-7").numero(7).jugador("Ismaïla Sarr").seleccion(Seleccion.SENEGAL).build();
    Figurita sen08 = Figurita.builder().id("SEN-8").numero(8).jugador("Idrissa Gueye").seleccion(Seleccion.SENEGAL).build();
    Figurita sen09 = Figurita.builder().id("SEN-9").numero(9).jugador("Boulaye Dia").seleccion(Seleccion.SENEGAL).build();
    Figurita sen10 = Figurita.builder().id("SEN-10").numero(10).jugador("Sadio Mané").seleccion(Seleccion.SENEGAL).build();
    Figurita sen11 = Figurita.builder().id("SEN-11").numero(11).jugador("Famara Diédhiou").seleccion(Seleccion.SENEGAL).build();

// ── ESTADOS UNIDOS ───────────────────────────────────────────────────────────
    Figurita usa01 = Figurita.builder().id("USA-1").numero(1).jugador("Matt Turner").seleccion(Seleccion.EEUU).build();
    Figurita usa02 = Figurita.builder().id("USA-2").numero(2).jugador("Sergiño Dest").seleccion(Seleccion.EEUU).build();
    Figurita usa03 = Figurita.builder().id("USA-3").numero(3).jugador("Antonee Robinson").seleccion(Seleccion.EEUU).build();
    Figurita usa04 = Figurita.builder().id("USA-4").numero(4).jugador("Walker Zimmerman").seleccion(Seleccion.EEUU).build();
    Figurita usa05 = Figurita.builder().id("USA-5").numero(5).jugador("Tim Ream").seleccion(Seleccion.EEUU).build();
    Figurita usa06 = Figurita.builder().id("USA-6").numero(6).jugador("Yunus Musah").seleccion(Seleccion.EEUU).build();
    Figurita usa07 = Figurita.builder().id("USA-7").numero(7).jugador("Weston McKennie").seleccion(Seleccion.EEUU).build();
    Figurita usa08 = Figurita.builder().id("USA-8").numero(8).jugador("Tyler Adams").seleccion(Seleccion.EEUU).build();
    Figurita usa09 = Figurita.builder().id("USA-9").numero(9).jugador("Josh Sargent").seleccion(Seleccion.EEUU).build();
    Figurita usa10 = Figurita.builder().id("USA-10").numero(10).jugador("Christian Pulisic").seleccion(Seleccion.EEUU).build();
    Figurita usa11 = Figurita.builder().id("USA-11").numero(11).jugador("Timothy Weah").seleccion(Seleccion.EEUU).build();

// ── ECUADOR ──────────────────────────────────────────────────────────────────
    Figurita ecu01 = Figurita.builder().id("ECU-1").numero(1).jugador("Hernán Galíndez").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu02 = Figurita.builder().id("ECU-2").numero(2).jugador("Angelo Preciado").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu03 = Figurita.builder().id("ECU-3").numero(3).jugador("Pervis Estupiñán").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu04 = Figurita.builder().id("ECU-4").numero(4).jugador("Robert Arboleda").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu05 = Figurita.builder().id("ECU-5").numero(5).jugador("Piero Hincapié").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu06 = Figurita.builder().id("ECU-6").numero(6).jugador("Carlos Gruezo").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu07 = Figurita.builder().id("ECU-7").numero(7).jugador("Romario Ibarra").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu08 = Figurita.builder().id("ECU-8").numero(8).jugador("Moisés Caicedo").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu09 = Figurita.builder().id("ECU-9").numero(9).jugador("Enner Valencia").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu10 = Figurita.builder().id("ECU-10").numero(10).jugador("Gonzalo Plata").seleccion(Seleccion.ECUADOR).build();
    Figurita ecu11 = Figurita.builder().id("ECU-11").numero(11).jugador("Michael Estrada").seleccion(Seleccion.ECUADOR).build();

// ── QATAR ────────────────────────────────────────────────────────────────────
    Figurita qat01 = Figurita.builder().id("QAT-1").numero(1).jugador("Saad Al Sheeb").seleccion(Seleccion.QATAR).build();
    Figurita qat02 = Figurita.builder().id("QAT-2").numero(2).jugador("Pedro Miguel").seleccion(Seleccion.QATAR).build();
    Figurita qat03 = Figurita.builder().id("QAT-3").numero(3).jugador("Homam Ahmed").seleccion(Seleccion.QATAR).build();
    Figurita qat04 = Figurita.builder().id("QAT-4").numero(4).jugador("Bassam Al-Rawi").seleccion(Seleccion.QATAR).build();
    Figurita qat05 = Figurita.builder().id("QAT-5").numero(5).jugador("Tarek Salman").seleccion(Seleccion.QATAR).build();
    Figurita qat06 = Figurita.builder().id("QAT-6").numero(6).jugador("Abdulaziz Hatem").seleccion(Seleccion.QATAR).build();
    Figurita qat07 = Figurita.builder().id("QAT-7").numero(7).jugador("Akram Afif").seleccion(Seleccion.QATAR).build();
    Figurita qat08 = Figurita.builder().id("QAT-8").numero(8).jugador("Karim Boudiaf").seleccion(Seleccion.QATAR).build();
    Figurita qat09 = Figurita.builder().id("QAT-9").numero(9).jugador("Almoez Ali").seleccion(Seleccion.QATAR).build();
    Figurita qat10 = Figurita.builder().id("QAT-10").numero(10).jugador("Hassan Al-Haydos").seleccion(Seleccion.QATAR).build();
    Figurita qat11 = Figurita.builder().id("QAT-11").numero(11).jugador("Ismail Mohamad").seleccion(Seleccion.QATAR).build();

// ── MÉXICO ───────────────────────────────────────────────────────────────────
    Figurita mex01 = Figurita.builder().id("MEX-1").numero(1).jugador("Guillermo Ochoa").seleccion(Seleccion.MEXICO).build();
    Figurita mex02 = Figurita.builder().id("MEX-2").numero(2).jugador("Jorge Sánchez").seleccion(Seleccion.MEXICO).build();
    Figurita mex03 = Figurita.builder().id("MEX-3").numero(3).jugador("Jesús Gallardo").seleccion(Seleccion.MEXICO).build();
    Figurita mex04 = Figurita.builder().id("MEX-4").numero(4).jugador("César Montes").seleccion(Seleccion.MEXICO).build();
    Figurita mex05 = Figurita.builder().id("MEX-5").numero(5).jugador("Johan Vásquez").seleccion(Seleccion.MEXICO).build();
    Figurita mex06 = Figurita.builder().id("MEX-6").numero(6).jugador("Édson Álvarez").seleccion(Seleccion.MEXICO).build();
    Figurita mex07 = Figurita.builder().id("MEX-7").numero(7).jugador("Hirving Lozano").seleccion(Seleccion.MEXICO).build();
    Figurita mex08 = Figurita.builder().id("MEX-8").numero(8).jugador("Héctor Herrera").seleccion(Seleccion.MEXICO).build();
    Figurita mex09 = Figurita.builder().id("MEX-9").numero(9).jugador("Raúl Jiménez").seleccion(Seleccion.MEXICO).build();
    Figurita mex10 = Figurita.builder().id("MEX-10").numero(10).jugador("Alexis Vega").seleccion(Seleccion.MEXICO).build();
    Figurita mex11 = Figurita.builder().id("MEX-11").numero(11).jugador("Orbelín Pineda").seleccion(Seleccion.MEXICO).build();

// ── POLONIA ──────────────────────────────────────────────────────────────────
    Figurita pol01 = Figurita.builder().id("POL-1").numero(1).jugador("Wojciech Szczęsny").seleccion(Seleccion.POLONIA).build();
    Figurita pol02 = Figurita.builder().id("POL-2").numero(2).jugador("Matty Cash").seleccion(Seleccion.POLONIA).build();
    Figurita pol03 = Figurita.builder().id("POL-3").numero(3).jugador("Bartosz Bereszyński").seleccion(Seleccion.POLONIA).build();
    Figurita pol04 = Figurita.builder().id("POL-4").numero(4).jugador("Kamil Glik").seleccion(Seleccion.POLONIA).build();
    Figurita pol05 = Figurita.builder().id("POL-5").numero(5).jugador("Jan Bednarek").seleccion(Seleccion.POLONIA).build();
    Figurita pol06 = Figurita.builder().id("POL-6").numero(6).jugador("Krystian Bielik").seleccion(Seleccion.POLONIA).build();
    Figurita pol07 = Figurita.builder().id("POL-7").numero(7).jugador("Kamil Grosicki").seleccion(Seleccion.POLONIA).build();
    Figurita pol08 = Figurita.builder().id("POL-8").numero(8).jugador("Piotr Zieliński").seleccion(Seleccion.POLONIA).build();
    Figurita pol09 = Figurita.builder().id("POL-9").numero(9).jugador("Robert Lewandowski").seleccion(Seleccion.POLONIA).build();
    Figurita pol10 = Figurita.builder().id("POL-10").numero(10).jugador("Grzegorz Krychowiak").seleccion(Seleccion.POLONIA).build();
    Figurita pol11 = Figurita.builder().id("POL-11").numero(11).jugador("Jakub Kamiński").seleccion(Seleccion.POLONIA).build();

// ── AUSTRALIA ────────────────────────────────────────────────────────────────
    Figurita aus01 = Figurita.builder().id("AUS-1").numero(1).jugador("Mathew Ryan").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus02 = Figurita.builder().id("AUS-2").numero(2).jugador("Nathaniel Atkinson").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus03 = Figurita.builder().id("AUS-3").numero(3).jugador("Aziz Behich").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus04 = Figurita.builder().id("AUS-4").numero(4).jugador("Harry Souttar").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus05 = Figurita.builder().id("AUS-5").numero(5).jugador("Kye Rowles").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus06 = Figurita.builder().id("AUS-6").numero(6).jugador("Jackson Irvine").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus07 = Figurita.builder().id("AUS-7").numero(7).jugador("Mathew Leckie").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus08 = Figurita.builder().id("AUS-8").numero(8).jugador("Aaron Mooy").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus09 = Figurita.builder().id("AUS-9").numero(9).jugador("Adam Taggart").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus10 = Figurita.builder().id("AUS-10").numero(10).jugador("Ajdin Hrustic").seleccion(Seleccion.AUSTRALIA).build();
    Figurita aus11 = Figurita.builder().id("AUS-11").numero(11).jugador("Mitchell Duke").seleccion(Seleccion.AUSTRALIA).build();

// ── DINAMARCA ────────────────────────────────────────────────────────────────
    Figurita den01 = Figurita.builder().id("DEN-1").numero(1).jugador("Kasper Schmeichel").seleccion(Seleccion.DINAMARCA).build();
    Figurita den02 = Figurita.builder().id("DEN-2").numero(2).jugador("Henrik Dalsgaard").seleccion(Seleccion.DINAMARCA).build();
    Figurita den03 = Figurita.builder().id("DEN-3").numero(3).jugador("Jens Stryger Larsen").seleccion(Seleccion.DINAMARCA).build();
    Figurita den04 = Figurita.builder().id("DEN-4").numero(4).jugador("Simon Kjær").seleccion(Seleccion.DINAMARCA).build();
    Figurita den05 = Figurita.builder().id("DEN-5").numero(5).jugador("Andreas Christensen").seleccion(Seleccion.DINAMARCA).build();
    Figurita den06 = Figurita.builder().id("DEN-6").numero(6).jugador("Thomas Delaney").seleccion(Seleccion.DINAMARCA).build();
    Figurita den07 = Figurita.builder().id("DEN-7").numero(7).jugador("Joakim Mæhle").seleccion(Seleccion.DINAMARCA).build();
    Figurita den08 = Figurita.builder().id("DEN-8").numero(8).jugador("Pierre-Emile Højbjerg").seleccion(Seleccion.DINAMARCA).build();
    Figurita den09 = Figurita.builder().id("DEN-9").numero(9).jugador("Jonas Wind").seleccion(Seleccion.DINAMARCA).build();
    Figurita den10 = Figurita.builder().id("DEN-10").numero(10).jugador("Christian Eriksen").seleccion(Seleccion.DINAMARCA).build();
    Figurita den11 = Figurita.builder().id("DEN-11").numero(11).jugador("Andreas Cornelius").seleccion(Seleccion.DINAMARCA).build();

// ── TÚNEZ ────────────────────────────────────────────────────────────────────
    Figurita tun01 = Figurita.builder().id("TUN-1").numero(1).jugador("Aymen Dahmen").seleccion(Seleccion.TUNEZ).build();
    Figurita tun02 = Figurita.builder().id("TUN-2").numero(2).jugador("Montassar Talbi").seleccion(Seleccion.TUNEZ).build();
    Figurita tun03 = Figurita.builder().id("TUN-3").numero(3).jugador("Ali Maaloul").seleccion(Seleccion.TUNEZ).build();
    Figurita tun04 = Figurita.builder().id("TUN-4").numero(4).jugador("Dylan Bronn").seleccion(Seleccion.TUNEZ).build();
    Figurita tun05 = Figurita.builder().id("TUN-5").numero(5).jugador("Yassine Meriah").seleccion(Seleccion.TUNEZ).build();
    Figurita tun06 = Figurita.builder().id("TUN-6").numero(6).jugador("Aïssa Laïdouni").seleccion(Seleccion.TUNEZ).build();
    Figurita tun07 = Figurita.builder().id("TUN-7").numero(7).jugador("Wahbi Khazri").seleccion(Seleccion.TUNEZ).build();
    Figurita tun08 = Figurita.builder().id("TUN-8").numero(8).jugador("Ellyes Skhiri").seleccion(Seleccion.TUNEZ).build();
    Figurita tun09 = Figurita.builder().id("TUN-9").numero(9).jugador("Seifeddine Jaziri").seleccion(Seleccion.TUNEZ).build();
    Figurita tun10 = Figurita.builder().id("TUN-10").numero(10).jugador("Hannibal Mejbri").seleccion(Seleccion.TUNEZ).build();
    Figurita tun11 = Figurita.builder().id("TUN-11").numero(11).jugador("Naïm Sliti").seleccion(Seleccion.TUNEZ).build();

// ── COSTA RICA ───────────────────────────────────────────────────────────────
    Figurita crc01 = Figurita.builder().id("CRC-1").numero(1).jugador("Keylor Navas").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc02 = Figurita.builder().id("CRC-2").numero(2).jugador("Keysher Fuller").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc03 = Figurita.builder().id("CRC-3").numero(3).jugador("Bryan Oviedo").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc04 = Figurita.builder().id("CRC-4").numero(4).jugador("Francisco Calvo").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc05 = Figurita.builder().id("CRC-5").numero(5).jugador("Óscar Duarte").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc06 = Figurita.builder().id("CRC-6").numero(6).jugador("Celso Borges").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc07 = Figurita.builder().id("CRC-7").numero(7).jugador("Johan Venegas").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc08 = Figurita.builder().id("CRC-8").numero(8).jugador("Yeltsin Tejeda").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc09 = Figurita.builder().id("CRC-9").numero(9).jugador("Joel Campbell").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc10 = Figurita.builder().id("CRC-10").numero(10).jugador("Bryan Ruiz").seleccion(Seleccion.COSTA_RICA).build();
    Figurita crc11 = Figurita.builder().id("CRC-11").numero(11).jugador("Anthony Contreras").seleccion(Seleccion.COSTA_RICA).build();

// ── JAPÓN ────────────────────────────────────────────────────────────────────
    Figurita jpn01 = Figurita.builder().id("JPN-1").numero(1).jugador("Shuichi Gonda").seleccion(Seleccion.JAPON).build();
    Figurita jpn02 = Figurita.builder().id("JPN-2").numero(2).jugador("Hiroki Sakai").seleccion(Seleccion.JAPON).build();
    Figurita jpn03 = Figurita.builder().id("JPN-3").numero(3).jugador("Yuto Nagatomo").seleccion(Seleccion.JAPON).build();
    Figurita jpn04 = Figurita.builder().id("JPN-4").numero(4).jugador("Ko Itakura").seleccion(Seleccion.JAPON).build();
    Figurita jpn05 = Figurita.builder().id("JPN-5").numero(5).jugador("Maya Yoshida").seleccion(Seleccion.JAPON).build();
    Figurita jpn06 = Figurita.builder().id("JPN-6").numero(6).jugador("Wataru Endo").seleccion(Seleccion.JAPON).build();
    Figurita jpn07 = Figurita.builder().id("JPN-7").numero(7).jugador("Takefusa Kubo").seleccion(Seleccion.JAPON).build();
    Figurita jpn08 = Figurita.builder().id("JPN-8").numero(8).jugador("Hidemasa Morita").seleccion(Seleccion.JAPON).build();
    Figurita jpn09 = Figurita.builder().id("JPN-9").numero(9).jugador("Daizen Maeda").seleccion(Seleccion.JAPON).build();
    Figurita jpn10 = Figurita.builder().id("JPN-10").numero(10).jugador("Daichi Kamada").seleccion(Seleccion.JAPON).build();
    Figurita jpn11 = Figurita.builder().id("JPN-11").numero(11).jugador("Ritsu Doan").seleccion(Seleccion.JAPON).build();

// ── BÉLGICA ──────────────────────────────────────────────────────────────────
    Figurita bel01 = Figurita.builder().id("BEL-1").numero(1).jugador("Thibaut Courtois").seleccion(Seleccion.BELGICA).build();
    Figurita bel02 = Figurita.builder().id("BEL-2").numero(2).jugador("Toby Alderweireld").seleccion(Seleccion.BELGICA).build();
    Figurita bel03 = Figurita.builder().id("BEL-3").numero(3).jugador("Yannick Carrasco").seleccion(Seleccion.BELGICA).build();
    Figurita bel04 = Figurita.builder().id("BEL-4").numero(4).jugador("Jan Vertonghen").seleccion(Seleccion.BELGICA).build();
    Figurita bel05 = Figurita.builder().id("BEL-5").numero(5).jugador("Thomas Meunier").seleccion(Seleccion.BELGICA).build();
    Figurita bel06 = Figurita.builder().id("BEL-6").numero(6).jugador("Axel Witsel").seleccion(Seleccion.BELGICA).build();
    Figurita bel07 = Figurita.builder().id("BEL-7").numero(7).jugador("Kevin De Bruyne").seleccion(Seleccion.BELGICA).build();
    Figurita bel08 = Figurita.builder().id("BEL-8").numero(8).jugador("Youri Tielemans").seleccion(Seleccion.BELGICA).build();
    Figurita bel09 = Figurita.builder().id("BEL-9").numero(9).jugador("Romelu Lukaku").seleccion(Seleccion.BELGICA).build();
    Figurita bel10 = Figurita.builder().id("BEL-10").numero(10).jugador("Eden Hazard").seleccion(Seleccion.BELGICA).build();
    Figurita bel11 = Figurita.builder().id("BEL-11").numero(11).jugador("Leandro Trossard").seleccion(Seleccion.BELGICA).build();

// ── CANADÁ ───────────────────────────────────────────────────────────────────
    Figurita can01 = Figurita.builder().id("CAN-1").numero(1).jugador("Milan Borjan").seleccion(Seleccion.CANADA).build();
    Figurita can02 = Figurita.builder().id("CAN-2").numero(2).jugador("Richie Laryea").seleccion(Seleccion.CANADA).build();
    Figurita can03 = Figurita.builder().id("CAN-3").numero(3).jugador("Alphonso Davies").seleccion(Seleccion.CANADA).build();
    Figurita can04 = Figurita.builder().id("CAN-4").numero(4).jugador("Steven Vitória").seleccion(Seleccion.CANADA).build();
    Figurita can05 = Figurita.builder().id("CAN-5").numero(5).jugador("Kamal Miller").seleccion(Seleccion.CANADA).build();
    Figurita can06 = Figurita.builder().id("CAN-6").numero(6).jugador("Stephen Eustáquio").seleccion(Seleccion.CANADA).build();
    Figurita can07 = Figurita.builder().id("CAN-7").numero(7).jugador("Jonathan David").seleccion(Seleccion.CANADA).build();
    Figurita can08 = Figurita.builder().id("CAN-8").numero(8).jugador("Atiba Hutchinson").seleccion(Seleccion.CANADA).build();
    Figurita can09 = Figurita.builder().id("CAN-9").numero(9).jugador("Cyle Larin").seleccion(Seleccion.CANADA).build();
    Figurita can10 = Figurita.builder().id("CAN-10").numero(10).jugador("Junior Hoilett").seleccion(Seleccion.CANADA).build();
    Figurita can11 = Figurita.builder().id("CAN-11").numero(11).jugador("Tajon Buchanan").seleccion(Seleccion.CANADA).build();

// ── ARABIA SAUDITA ───────────────────────────────────────────────────────────
    Figurita ksa01 = Figurita.builder().id("KSA-1").numero(1).jugador("Mohammed Al-Owais").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa02 = Figurita.builder().id("KSA-2").numero(2).jugador("Sultan Al-Ghannam").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa03 = Figurita.builder().id("KSA-3").numero(3).jugador("Yasser Al-Shahrani").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa04 = Figurita.builder().id("KSA-4").numero(4).jugador("Ali Al-Bulaihi").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa05 = Figurita.builder().id("KSA-5").numero(5).jugador("Abdulelah Al-Amri").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa06 = Figurita.builder().id("KSA-6").numero(6).jugador("Ali Al-Hassan").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa07 = Figurita.builder().id("KSA-7").numero(7).jugador("Salman Al-Faraj").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa08 = Figurita.builder().id("KSA-8").numero(8).jugador("Sami Al-Najei").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa09 = Figurita.builder().id("KSA-9").numero(9).jugador("Saleh Al-Shehri").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa10 = Figurita.builder().id("KSA-10").numero(10).jugador("Salem Al-Dawsari").seleccion(Seleccion.ARABIA_SAUDITA).build();
    Figurita ksa11 = Figurita.builder().id("KSA-11").numero(11).jugador("Firas Al-Buraikan").seleccion(Seleccion.ARABIA_SAUDITA).build();

// ── GALES ────────────────────────────────────────────────────────────────────
    Figurita wal01 = Figurita.builder().id("WAL-1").numero(1).jugador("Wayne Hennessey").seleccion(Seleccion.GALES).build();
    Figurita wal02 = Figurita.builder().id("WAL-2").numero(2).jugador("Connor Roberts").seleccion(Seleccion.GALES).build();
    Figurita wal03 = Figurita.builder().id("WAL-3").numero(3).jugador("Neco Williams").seleccion(Seleccion.GALES).build();
    Figurita wal04 = Figurita.builder().id("WAL-4").numero(4).jugador("Ben Davies").seleccion(Seleccion.GALES).build();
    Figurita wal05 = Figurita.builder().id("WAL-5").numero(5).jugador("Joe Rodon").seleccion(Seleccion.GALES).build();
    Figurita wal06 = Figurita.builder().id("WAL-6").numero(6).jugador("Joe Allen").seleccion(Seleccion.GALES).build();
    Figurita wal07 = Figurita.builder().id("WAL-7").numero(7).jugador("Daniel James").seleccion(Seleccion.GALES).build();
    Figurita wal08 = Figurita.builder().id("WAL-8").numero(8).jugador("Jonny Williams").seleccion(Seleccion.GALES).build();
    Figurita wal09 = Figurita.builder().id("WAL-9").numero(9).jugador("Kieffer Moore").seleccion(Seleccion.GALES).build();
    Figurita wal10 = Figurita.builder().id("WAL-10").numero(10).jugador("Gareth Bale").seleccion(Seleccion.GALES).build();
    Figurita wal11 = Figurita.builder().id("WAL-11").numero(11).jugador("Harry Wilson").seleccion(Seleccion.GALES).build();

// ── IRÁN ─────────────────────────────────────────────────────────────────────
    Figurita irn01 = Figurita.builder().id("IRN-1").numero(1).jugador("Alireza Beiranvand").seleccion(Seleccion.IRAN).build();
    Figurita irn02 = Figurita.builder().id("IRN-2").numero(2).jugador("Sadegh Moharrami").seleccion(Seleccion.IRAN).build();
    Figurita irn03 = Figurita.builder().id("IRN-3").numero(3).jugador("Ehsan Hajsafi").seleccion(Seleccion.IRAN).build();
    Figurita irn04 = Figurita.builder().id("IRN-4").numero(4).jugador("Rouzbeh Cheshmi").seleccion(Seleccion.IRAN).build();
    Figurita irn05 = Figurita.builder().id("IRN-5").numero(5).jugador("Majid Hosseini").seleccion(Seleccion.IRAN).build();
    Figurita irn06 = Figurita.builder().id("IRN-6").numero(6).jugador("Saeid Ezatolahi").seleccion(Seleccion.IRAN).build();
    Figurita irn07 = Figurita.builder().id("IRN-7").numero(7).jugador("Alireza Jahanbakhsh").seleccion(Seleccion.IRAN).build();
    Figurita irn08 = Figurita.builder().id("IRN-8").numero(8).jugador("Ahmad Nourollahi").seleccion(Seleccion.IRAN).build();
    Figurita irn09 = Figurita.builder().id("IRN-9").numero(9).jugador("Mehdi Taremi").seleccion(Seleccion.IRAN).build();
    Figurita irn10 = Figurita.builder().id("IRN-10").numero(10).jugador("Ali Gholizadeh").seleccion(Seleccion.IRAN).build();
    Figurita irn11 = Figurita.builder().id("IRN-11").numero(11).jugador("Sardar Azmoun").seleccion(Seleccion.IRAN).build();

// ── SERBIA ───────────────────────────────────────────────────────────────────
    Figurita srb01 = Figurita.builder().id("SRB-1").numero(1).jugador("Vanja Milinković-Savić").seleccion(Seleccion.SERBIA).build();
    Figurita srb02 = Figurita.builder().id("SRB-2").numero(2).jugador("Strahinja Pavlović").seleccion(Seleccion.SERBIA).build();
    Figurita srb03 = Figurita.builder().id("SRB-3").numero(3).jugador("Miloš Veljković").seleccion(Seleccion.SERBIA).build();
    Figurita srb04 = Figurita.builder().id("SRB-4").numero(4).jugador("Nikola Milenković").seleccion(Seleccion.SERBIA).build();
    Figurita srb05 = Figurita.builder().id("SRB-5").numero(5).jugador("Filip Mladenović").seleccion(Seleccion.SERBIA).build();
    Figurita srb06 = Figurita.builder().id("SRB-6").numero(6).jugador("Nemanja Gudelj").seleccion(Seleccion.SERBIA).build();
    Figurita srb07 = Figurita.builder().id("SRB-7").numero(7).jugador("Filip Kostić").seleccion(Seleccion.SERBIA).build();
    Figurita srb08 = Figurita.builder().id("SRB-8").numero(8).jugador("Sergej Milinković-Savić").seleccion(Seleccion.SERBIA).build();
    Figurita srb09 = Figurita.builder().id("SRB-9").numero(9).jugador("Aleksandar Mitrović").seleccion(Seleccion.SERBIA).build();
    Figurita srb10 = Figurita.builder().id("SRB-10").numero(10).jugador("Dušan Tadić").seleccion(Seleccion.SERBIA).build();
    Figurita srb11 = Figurita.builder().id("SRB-11").numero(11).jugador("Dušan Vlahović").seleccion(Seleccion.SERBIA).build();

// ── SUIZA ────────────────────────────────────────────────────────────────────
    Figurita sui01 = Figurita.builder().id("SUI-1").numero(1).jugador("Yann Sommer").seleccion(Seleccion.SUIZA).build();
    Figurita sui02 = Figurita.builder().id("SUI-2").numero(2).jugador("Silvan Widmer").seleccion(Seleccion.SUIZA).build();
    Figurita sui03 = Figurita.builder().id("SUI-3").numero(3).jugador("Ricardo Rodríguez").seleccion(Seleccion.SUIZA).build();
    Figurita sui04 = Figurita.builder().id("SUI-4").numero(4).jugador("Nico Elvedi").seleccion(Seleccion.SUIZA).build();
    Figurita sui05 = Figurita.builder().id("SUI-5").numero(5).jugador("Manuel Akanji").seleccion(Seleccion.SUIZA).build();
    Figurita sui06 = Figurita.builder().id("SUI-6").numero(6).jugador("Denis Zakaria").seleccion(Seleccion.SUIZA).build();
    Figurita sui07 = Figurita.builder().id("SUI-7").numero(7).jugador("Breel Embolo").seleccion(Seleccion.SUIZA).build();
    Figurita sui08 = Figurita.builder().id("SUI-8").numero(8).jugador("Remo Freuler").seleccion(Seleccion.SUIZA).build();
    Figurita sui09 = Figurita.builder().id("SUI-9").numero(9).jugador("Haris Seferović").seleccion(Seleccion.SUIZA).build();
    Figurita sui10 = Figurita.builder().id("SUI-10").numero(10).jugador("Granit Xhaka").seleccion(Seleccion.SUIZA).build();
    Figurita sui11 = Figurita.builder().id("SUI-11").numero(11).jugador("Xherdan Shaqiri").seleccion(Seleccion.SUIZA).build();

// ── CAMERÚN ──────────────────────────────────────────────────────────────────
    Figurita cmr01 = Figurita.builder().id("CMR-1").numero(1).jugador("André Onana").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr02 = Figurita.builder().id("CMR-2").numero(2).jugador("Collins Fai").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr03 = Figurita.builder().id("CMR-3").numero(3).jugador("Nouhou Tolo").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr04 = Figurita.builder().id("CMR-4").numero(4).jugador("Nico Nkoulou").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr05 = Figurita.builder().id("CMR-5").numero(5).jugador("Jean-Charles Castelletto").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr06 = Figurita.builder().id("CMR-6").numero(6).jugador("Martin Hongla").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr07 = Figurita.builder().id("CMR-7").numero(7).jugador("Stéphane Bahoken").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr08 = Figurita.builder().id("CMR-8").numero(8).jugador("André-Frank Zambo Anguissa").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr09 = Figurita.builder().id("CMR-9").numero(9).jugador("Vincent Aboubakar").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr10 = Figurita.builder().id("CMR-10").numero(10).jugador("Eric Maxim Choupo-Moting").seleccion(Seleccion.CAMERUN).build();
    Figurita cmr11 = Figurita.builder().id("CMR-11").numero(11).jugador("Karl Toko Ekambi").seleccion(Seleccion.CAMERUN).build();

// ── GHANA ────────────────────────────────────────────────────────────────────
    Figurita gha01 = Figurita.builder().id("GHA-1").numero(1).jugador("Lawrence Ati-Zigi").seleccion(Seleccion.GHANA).build();
    Figurita gha02 = Figurita.builder().id("GHA-2").numero(2).jugador("Tariq Lamptey").seleccion(Seleccion.GHANA).build();
    Figurita gha03 = Figurita.builder().id("GHA-3").numero(3).jugador("Gideon Mensah").seleccion(Seleccion.GHANA).build();
    Figurita gha04 = Figurita.builder().id("GHA-4").numero(4).jugador("Daniel Amartey").seleccion(Seleccion.GHANA).build();
    Figurita gha05 = Figurita.builder().id("GHA-5").numero(5).jugador("Alexander Djiku").seleccion(Seleccion.GHANA).build();
    Figurita gha06 = Figurita.builder().id("GHA-6").numero(6).jugador("Thomas Partey").seleccion(Seleccion.GHANA).build();
    Figurita gha07 = Figurita.builder().id("GHA-7").numero(7).jugador("Mohammed Kudus").seleccion(Seleccion.GHANA).build();
    Figurita gha08 = Figurita.builder().id("GHA-8").numero(8).jugador("André Ayew").seleccion(Seleccion.GHANA).build();
    Figurita gha09 = Figurita.builder().id("GHA-9").numero(9).jugador("Inaki Williams").seleccion(Seleccion.GHANA).build();
    Figurita gha10 = Figurita.builder().id("GHA-10").numero(10).jugador("Jordan Ayew").seleccion(Seleccion.GHANA).build();
    Figurita gha11 = Figurita.builder().id("GHA-11").numero(11).jugador("Osman Bukari").seleccion(Seleccion.GHANA).build();

// ── URUGUAY ──────────────────────────────────────────────────────────────────
    Figurita uru01 = Figurita.builder().id("URU-1").numero(1).jugador("Sergio Rochet").seleccion(Seleccion.URUGUAY).build();
    Figurita uru02 = Figurita.builder().id("URU-2").numero(2).jugador("Guillermo Varela").seleccion(Seleccion.URUGUAY).build();
    Figurita uru03 = Figurita.builder().id("URU-3").numero(3).jugador("Mathías Olivera").seleccion(Seleccion.URUGUAY).build();
    Figurita uru04 = Figurita.builder().id("URU-4").numero(4).jugador("José María Giménez").seleccion(Seleccion.URUGUAY).build();
    Figurita uru05 = Figurita.builder().id("URU-5").numero(5).jugador("Diego Godín").seleccion(Seleccion.URUGUAY).build();
    Figurita uru06 = Figurita.builder().id("URU-6").numero(6).jugador("Rodrigo Bentancur").seleccion(Seleccion.URUGUAY).build();
    Figurita uru07 = Figurita.builder().id("URU-7").numero(7).jugador("Facundo Pellistri").seleccion(Seleccion.URUGUAY).build();
    Figurita uru08 = Figurita.builder().id("URU-8").numero(8).jugador("Federico Valverde").seleccion(Seleccion.URUGUAY).build();
    Figurita uru09 = Figurita.builder().id("URU-9").numero(9).jugador("Darwin Núñez").seleccion(Seleccion.URUGUAY).build();
    Figurita uru10 = Figurita.builder().id("URU-10").numero(10).jugador("Giorgian De Arrascaeta").seleccion(Seleccion.URUGUAY).build();
    Figurita uru11 = Figurita.builder().id("URU-11").numero(11).jugador("Luis Suárez").seleccion(Seleccion.URUGUAY).build();

// ── COREA DEL SUR ────────────────────────────────────────────────────────────
    Figurita kor01 = Figurita.builder().id("KOR-1").numero(1).jugador("Kim Seung-gyu").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor02 = Figurita.builder().id("KOR-2").numero(2).jugador("Kim Moon-hwan").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor03 = Figurita.builder().id("KOR-3").numero(3).jugador("Kim Jin-su").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor04 = Figurita.builder().id("KOR-4").numero(4).jugador("Kim Min-jae").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor05 = Figurita.builder().id("KOR-5").numero(5).jugador("Jung Woo-young").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor06 = Figurita.builder().id("KOR-6").numero(6).jugador("Hwang In-beom").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor07 = Figurita.builder().id("KOR-7").numero(7).jugador("Son Heung-min").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor08 = Figurita.builder().id("KOR-8").numero(8).jugador("Lee Jae-sung").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor09 = Figurita.builder().id("KOR-9").numero(9).jugador("Cho Gue-sung").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor10 = Figurita.builder().id("KOR-10").numero(10).jugador("Lee Kang-in").seleccion(Seleccion.COREA_DEL_SUR).build();
    Figurita kor11 = Figurita.builder().id("KOR-11").numero(11).jugador("Hwang Hee-chan").seleccion(Seleccion.COREA_DEL_SUR).build();

    return List.of(
        arg01, arg02, arg03, arg04, arg05, arg06, arg07, arg08, arg09, arg10, arg11,
        fra01, fra02, fra03, fra04, fra05, fra06, fra07, fra08, fra09, fra10, fra11,
        bra01, bra02, bra03, bra04, bra05, bra06, bra07, bra08, bra09, bra10, bra11,
        esp01, esp02, esp03, esp04, esp05, esp06, esp07, esp08, esp09, esp10, esp11,
        ger01, ger02, ger03, ger04, ger05, ger06, ger07, ger08, ger09, ger10, ger11,
        por01, por02, por03, por04, por05, por06, por07, por08, por09, por10, por11,
        ned01, ned02, ned03, ned04, ned05, ned06, ned07, ned08, ned09, ned10, ned11,
        cro01, cro02, cro03, cro04, cro05, cro06, cro07, cro08, cro09, cro10, cro11,
        mar01, mar02, mar03, mar04, mar05, mar06, mar07, mar08, mar09, mar10, mar11,
        eng01, eng02, eng03, eng04, eng05, eng06, eng07, eng08, eng09, eng10, eng11,
        sen01, sen02, sen03, sen04, sen05, sen06, sen07, sen08, sen09, sen10, sen11,
        usa01, usa02, usa03, usa04, usa05, usa06, usa07, usa08, usa09, usa10, usa11,
        ecu01, ecu02, ecu03, ecu04, ecu05, ecu06, ecu07, ecu08, ecu09, ecu10, ecu11,
        qat01, qat02, qat03, qat04, qat05, qat06, qat07, qat08, qat09, qat10, qat11,
        mex01, mex02, mex03, mex04, mex05, mex06, mex07, mex08, mex09, mex10, mex11,
        pol01, pol02, pol03, pol04, pol05, pol06, pol07, pol08, pol09, pol10, pol11,
        aus01, aus02, aus03, aus04, aus05, aus06, aus07, aus08, aus09, aus10, aus11,
        den01, den02, den03, den04, den05, den06, den07, den08, den09, den10, den11,
        tun01, tun02, tun03, tun04, tun05, tun06, tun07, tun08, tun09, tun10, tun11,
        crc01, crc02, crc03, crc04, crc05, crc06, crc07, crc08, crc09, crc10, crc11,
        jpn01, jpn02, jpn03, jpn04, jpn05, jpn06, jpn07, jpn08, jpn09, jpn10, jpn11,
        bel01, bel02, bel03, bel04, bel05, bel06, bel07, bel08, bel09, bel10, bel11,
        can01, can02, can03, can04, can05, can06, can07, can08, can09, can10, can11,
        ksa01, ksa02, ksa03, ksa04, ksa05, ksa06, ksa07, ksa08, ksa09, ksa10, ksa11,
        wal01, wal02, wal03, wal04, wal05, wal06, wal07, wal08, wal09, wal10, wal11,
        irn01, irn02, irn03, irn04, irn05, irn06, irn07, irn08, irn09, irn10, irn11,
        srb01, srb02, srb03, srb04, srb05, srb06, srb07, srb08, srb09, srb10, srb11,
        sui01, sui02, sui03, sui04, sui05, sui06, sui07, sui08, sui09, sui10, sui11,
        cmr01, cmr02, cmr03, cmr04, cmr05, cmr06, cmr07, cmr08, cmr09, cmr10, cmr11,
        gha01, gha02, gha03, gha04, gha05, gha06, gha07, gha08, gha09, gha10, gha11,
        uru01, uru02, uru03, uru04, uru05, uru06, uru07, uru08, uru09, uru10, uru11,
        kor01, kor02, kor03, kor04, kor05, kor06, kor07, kor08, kor09, kor10, kor11
    );
  }


}
