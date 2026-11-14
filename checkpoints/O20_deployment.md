# Checkpoint OpenShift — 14 novembre 2026

## Ambiente
- PC: LAPTOP-7NT8I0GH
- Workspace: ~/workspace/openshift-java-catalog
- Project: ettpann-dev
- OpenShift Developer Sandbox

## Preparazione complessiva
85% — fase Deployment Java.

## Repository
https://github.com/EttorePannain/openshift-java-catalog

## Build applicativa
- BuildConfig: lab-java-git
- Build: lab-java-git-1
- Sorgente Git: 6db9a1b867a85176abce124186e1e9e7e93f0d8e
- Build: Complete
- Test Maven: 3, fallimenti 0, errori 0
- Push: successful
- ImageStream: lab-java
- ImageStreamTag: lab-java:build
- Digest: sha256:7f89501b63b41884470d7b3271c85ef2037b477c9c8ab49ee5075b4c39528cfa

## Deployment
- Nome: catalogo
- Repliche: 1/1
- Rollout: successfully rolled out
- Pod osservato: catalogo-84f9dc969f-xz7l4
- Pod: Running, Ready 1/1, restart 0
- SCC osservata: restricted-v2
- UID dichiarato ed effettivo: 1002100000
- GID effettivo: 0

## Stato delle risorse
- ImageStream: creato
- BuildConfig: creata
- Immagine: pubblicata
- Deployment: operativo
- Service applicativo: non ancora creato
- Route applicativa: non ancora creata
- Pipeline Tekton applicativa: non ancora realizzata
- TaskRun diagnostico Tekton: Succeeded

## Prossimo passo
O20.5 — Creare e comprendere Service e Route.
Verificare gli endpoint HTTP dell'applicazione.
Poi completare la CI/CD con Tekton.

## Da ricordare
- Prima concetti e architettura, poi manifest e comandi.
- Verifiche CLI e UI per ogni risorsa.
- Evidenze raccolte durante il laboratorio.
- Non riprendere automaticamente le vecchie guide G01-G10.
- Il 100% riguarda la preparazione tecnica, non la
  conclusione obbligatoria del vecchio pacchetto didattico.
