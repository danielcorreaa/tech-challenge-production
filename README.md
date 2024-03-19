
# Microsserviço tech-challenge-production

Microsserviço responsável pelo gerenciamento de produção de pedidos


## Autores

- [@danielcorreaa](https://github.com/danielcorreaa)

## Stack utilizada


**Back-end:** Java, Spring Boot, Mysql, Kafka


## Documentação da API

### Atualização de pedidos em produção

#### Atualização pedidos em produção


```http
  PUT api/v1/production/ready/{orderId}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderId` | `string` | **Obrigatório**  identificador do pedido |

```http
  PUT api/v1/production/finish/{orderId}
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `orderId` | `string` | **Obrigatório**  identificador do pedido |



#### Buscar pedido em produção
```http
  GET api/v1/production/find/{orderId}
```

| Parâmetro   | Tipo  |  Descrição                                   |
| :---------- | :--------- |:------------------------------------------ |
| `orderId` | `string` | **Obrigatório**  identificador do pedido |



## Relatório RIPD
*RELATÓRIO DE IMPACTO À PROTEÇÃO DE DADOS PESSOAIS*

- [@RIPD](https://danielcorreaa.github.io/tech-challenge-production/RIPD.pdf)

## Documentação Saga

### Padrão escolhido: Coreografia 

#### Razão de utilizar a coreografia
*Escolhi o padrão coreografado para evitar deixar tudo centralizado no serviço de pedidos, no caso de acontecer alguma falha no serviço de pedidos toda a operação de notificar cliente e enviar os pedidos pagos para a cozinha seria paralizada, com a coreografia mesmo que tenha algum problema com o serviço de pedidos, a cozinha ainda recebe os pedidos com pagamentos aprovados, nao parando a produção de pedidos pagos, e os clientes recebem notificaçao de problemas com o pagamento.*

#### Desenho da solução

- [@Desenho Padrão Saga coreografado.](https://danielcorreaa.github.io/tech-challenge-production/images/saga-diagrama.png)

![Desenho Padrão Saga coreografado.](/docs/images/saga-diagrama.png)

## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-production.git
```

Entre no diretório do projeto

```bash
  cd tech-challenge-production
```

Docker

```bash
  docker compose up -d
```

No navegador

```bash
  http://localhost:8085/
```



## Deploy

### Para subir a aplicação usando kubernetes

#### Infraestrutura:

Clone o projeto com a infraestrutura

```bash
  git clone danielcorreaa/tech-challenge-infra-terraform-kubernetes
```
Entre no diretório do projeto

```bash
  cd tech-challenge-infra-terraform-kubernetes/
````

Execute os comandos

```bash   
- run: kubectl apply -f kubernetes/metrics.yaml 
- run: kubectl apply -f kubernetes/mysql/mysql-secrets.yaml 
- run: kubectl apply -f kubernetes/mysql/mysql-configmap.yaml 
- run: kubectl apply -f kubernetes/mysql/mysql-pv.yaml 
- run: kubectl apply -f kubernetes/mysql/mysql-service.yaml 
- run: kubectl apply -f kubernetes/mysql/mysql-statefulset.yaml

- run: kubectl apply -f kubernetes/kafka/kafka-configmap.yaml
- run: kubectl apply -f kubernetes/kafka/zookeeper-deployment.yaml
- run: kubectl apply -f kubernetes/kafka/zookeeper-service.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-deployment.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-service.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-ui-deployment.yaml

````

#### Aplicação:

docker hub [@repositorio](https://hub.docker.com/r/daniel36/tech-challenge-production/tags)

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-production.git
```

Entre no diretório do projeto

```bash
  cd tech-challenge-production
```

Execute os comandos
```bash   
- run: kubectl apply -f k8s/production-deployment.yaml
- run: kubectl apply -f k8s/production-service.yaml     
- run: kubectl apply -f k8s/production-hpa.yaml

````
