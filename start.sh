docker-compose --profile all down
docker-compose --profile all build
docker rmi $(docker images -f "dangling=true" -q)
docker-compose --profile all up -d