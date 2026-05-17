# --- Etapa de build ---
# Usamos Node 22 Alpine para compilar la app con Vite
FROM node:22-alpine AS builder
WORKDIR /app

# Copiamos solo los manifests primero para cachear la instalación de dependencias
# Si src/ cambia pero package.json no, Docker reutiliza esta capa
COPY package.json package-lock.json ./
RUN npm ci

# Copiamos el resto del codigo y compilamos
COPY . .
RUN npm run build

# --- Etapa de runtime ---
# Nginx sirve archivos estaticos
FROM nginx:alpine AS production

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
