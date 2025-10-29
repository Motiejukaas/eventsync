#!/bin/sh
set -eu

# Cloud Run sets PORT; default to 8080 if not provided
: "${PORT:=8080}"

# Render nginx config from template with the current PORT
if [ -f /etc/nginx/templates/default.conf.template ]; then
  envsubst '\$PORT' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf
fi

# Start nginx in the foreground
exec nginx -g 'daemon off;'
