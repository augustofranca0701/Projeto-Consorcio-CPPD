# Etapa 1 — build do Angular
FROM node:18-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build --prod

# Etapa 2 — servidor Nginx
FROM nginx:stable-alpine

# Remove configuração padrão
RUN rm /etc/nginx/conf.d/default.conf

# Copia o build do Angular
COPY --from=build /app/dist/consorcio-frontend/browser /usr/share/nginx/html

# Copia configuração do NGINX
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
