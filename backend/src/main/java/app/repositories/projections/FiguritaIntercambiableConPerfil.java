package app.repositories.projections;

import app.model.entities.FiguritaIntercambiable;

public record FiguritaIntercambiableConPerfil(
    FiguritaIntercambiable figurita,
    ResumenPerfil perfil
) {
}
