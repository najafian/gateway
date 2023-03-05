. .env
export EUREKA_URL=$EUREKA_URL
export ISSUER_URI=$ISSUER_URI
export GATEWAY_DOCKER_PORT=$GATEWAY_DOCKER_PORT
mvn clean package

docker-compose -f docker-compose.yml down
docker rmi gateway_gateway
docker-compose -f docker-compose.yml up --build -d

mvn clean
