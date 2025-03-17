CMD: 
Postgres: docker run --name exe101 -e POSTGRES_USER=exe101 -e POSTGRES_PASSWORD=12345 -e POSTGRES_DB=blindbox -p 5432:5432 -d postgres

CMD-DOCKER:
Redis: docker run -d --name eCommerce -p 6379:6379 -p 8001:8001 redis/redis-stack:latest
RabbitMQ: docker run --rm -it -p 15672:15672 -p 5672:5672 --name eCommerceRabbitMq -d rabbitmq:3-management