#!/bin/bash

set -eux

if [[ $# -lt 1 ]]; then
  echo "Usage:"
  echo "$0 <PG PASSWORD> <PG USERNAME=$USER> [PG PORT] [Image tag] [Image name]"
  exit 1
fi

PGPW=$1
PGUSER=${2:-$USER}
# TODO(dave): Add support for the port option.
PGPORT=${3:-5432}
PGTAG=${4:-latest}
PGNAME=${5:-"Scratch-Postgres"}

docker run \
  --name "$PGNAME" \
  -e "POSTGRES_PASSWORD=$PGPW" \
  -e "POSTGRES_USER=$PGUSER" \
  -p "${PGPORT}:5432" \
  -d \
  postgres:${PGTAG}
