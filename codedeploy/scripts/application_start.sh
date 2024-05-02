#!/usr/bin/env bash

# 실행중인 컨테이너 확인
running=$(docker ps -a --format "{{.ID}}" --filter "name=custom-master-cd" | wc -l)

# shellcheck disable=SC2086
if [ $running -eq 1 ]; then
    docker stop custom-master-cd
    docker rm custom-master-cd
fi

# 새 이미지 가져와 실행
docker pull jisoo0609/custom-master:latest
docker run -d --name custom-master-cd -p 8080:8080 jisoo0609/custom-master:latest