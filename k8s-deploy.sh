#!/bin/bash

set -e

echo "Criando o cluster com base no cluster-config.yaml..."
kind create cluster --name fastfood --config k8s/cluster-config.yaml
echo "Cluster criado com sucesso!"

sleep 5

echo "Subindo o banco de dados..."
kubectl apply -f k8s/db-deployment.yaml
kubectl apply -f k8s/db-service.yaml
echo "Banco de dados implantado com sucesso!"

sleep 5

echo "Subindo a aplicação..."
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-hpa.yaml
echo "Aplicação implantada com sucesso!"

sleep 5

echo "Instalando o Metrics Server..."
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
echo "Metrics Server instalado com sucesso!"

sleep 5

echo "Corrigindo a configuração do Metrics Server..."
kubectl patch deployment metrics-server -n kube-system --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value": "--kubelet-insecure-tls"}]'
echo "Metrics Server configurado com sucesso!"

sleep 5

echo "Subindo o serviço da aplicação..."
kubectl apply -f k8s/app-service.yaml
echo "Serviço da aplicação implantado com sucesso!"

sleep 5

echo "Aplicando Secrets e ConfigMaps..."
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml
echo "Secrets e ConfigMaps aplicados com sucesso!"

sleep 2
echo "🚀 Deploy concluído com sucesso!"
