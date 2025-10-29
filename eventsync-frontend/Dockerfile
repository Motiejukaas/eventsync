# Multi-stage Dockerfile for building the Vite frontend and serving with nginx
# Build stage: uses Node to install deps and run the production build
FROM node:18-alpine AS build
WORKDIR /app

# Optional API URL for production builds (baked into the bundle if provided)
ARG VITE_API_URL
ENV VITE_API_URL=${VITE_API_URL}

# Copy package manifests first to leverage Docker layer caching
COPY package.json package-lock.json* ./

# Install dependencies. If package-lock.json exists use `npm ci` otherwise fall back to `npm install`.
RUN if [ -f package-lock.json ]; then \
            npm ci --silent --no-audit --no-fund; \
        else \
            npm install --silent --no-audit --no-fund; \
        fi

# Copy source and build
COPY . .
RUN npm run build

# Production stage: serve built files with nginx (small, secure runtime)
FROM nginx:alpine AS final

# Copy built static files
COPY --from=build /app/dist /usr/share/nginx/html

# Include a small nginx config that supports SPA routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
