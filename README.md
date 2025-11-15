# TaskFlow
TasFlow - TODO список прямо в MAX


## Инструкция по запуску
1) ```newgrp docker``` - сброс сборки(на всякий)
2) ```docker build -t max-taskflow .``` - сборка контейнера
3) ```docker run max-taskflow --token=put_token_here``` - запуск веб-сервера(порт 8080)
- Для изменния порта, используйте аргумент ```-p 1234:8080```(замените 1234 на нужный вам) после ```docker run``` 
- Для того-чтобы программа могла сохранять данные, надо выделить дирректрию для работы, через ```-v /home/user/bot-data:/app/max-bot-data```
- Пример: ```docker run -p 8088:8080 -v /home/user/bot-data:/app/max-bot-data max-taskflow --token=kM7vPqR3xT9sJ_BFuN2hWioL5eYcXzVaE8rG1tKpQwHjDsOnIf6u_ZmNlAyC4bVxT0-wE9gFHiUkLSpdRj```
