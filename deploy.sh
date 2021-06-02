#!/bin/bash
git pull
mvn clean install
cd ui
npm i
npm run build
cd ..
docker-compose up -d --build coordinator nginx
echo 'sleep 15'
sleep 15
docker-compose up -d --build worker
docker restart nginx