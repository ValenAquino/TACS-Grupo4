# TACS 2026 1C - Grupo 5 — Frontend

Interfaz web para la aplicacion de intercambio de figuritas del Mundial. 
Permite explorar figuritas disponibles, gestionar la propia colección, proponer intercambios, participar en subastas y consultar sugerencias de perfiles afines.

## Stack tecnológico

| Componente       | Tecnología                 |
|------------------|----------------------------|
| Lenguaje         | JavaScript                 |
| Framework        | React 19                   |
| Build            | Vite 8                     |
| Routing          | React Router 7             |
| HTTP client      | Axios 1.15                 |
| Linting          | ESLint 9                   |
| Containerización | Docker (multi-stage build) |

## Levantar la aplicacion

### Con Docker Compose (desde la raiz del repositorio)

```bash
# Produccion (frontend + backend)
docker compose up frontend backend
```

### Con Docker

```bash
# Produccion
docker build -t tacs-frontend .
docker run -p 5173:5173 tacs-frontend
```

### Con npm

```bash
npm install
npm run dev
```

La aplicacion queda disponible en:

- Desarrollo: `http://localhost:5173`
- Produccion: `http://localhost:5173`

### Variable de entorno

| Variable           | Default                 | Descripción          |
|--------------------|-------------------------|----------------------|
| `VITE_BACKEND_URI` | `http://localhost:8080` | URL base del backend |

```
VITE_BACKEND_URI=http://mi-backend:8080
```

---

## Estructura del proyecto

```
src/
├── components/
│   ├── layouts/        # Layout general y Navbar
│   └── ui/             # Componentes reutilizables (cards, modales, botones, etc.)
├── hooks/              # Custom hooks (useFiguritas, useUsuarioActual)
├── services/           # Capa de comunicación con el backend (axios)
├── utils/              # Helpers
└── views/
    └── public/         # Vistas accesibles sin autenticación
```

---

## Decisiones de diseño

### Alias `@` para imports

`vite.config.js` define el alias `@` apuntando a `src/`. Esto evita rutas relativas profundas (`../../../`) y hace los imports resistentes a reestructuración de carpetas.

### Separación layouts / ui / views

Los componentes se dividen en tres capas: 
- `layouts` (estructura de página)
- `ui` (componentes genéricos sin lógica de negocio)
- `views` (páginas con lógica de negocio y composición). 
Esta separación permite reutilizar componentes `ui` en cualquier vista sin arrastrar dependencias de dominio.

### Variable de entorno con fallback

`api.js` usa `import.meta.env.VITE_BACKEND_URI || 'http://localhost:8080'` 
para que la app funcione sin configuración extra en desarrollo local y sea configurable vía variable de entorno en cualquier entorno de despliegue.
