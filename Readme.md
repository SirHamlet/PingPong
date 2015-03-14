Простой Ping-сервер
=========

### Краткое описание
Возвращает в ответ на запрос Ping количество обращений от указанного userId

### Требования для запуска
JDK 8, MongoDB (желательно, 3.0)

### Сборка
Для сборки необходимо выполнить gradle clean build или gradle clean build -x test (без тестов)
Приложение будет собрано в PingPong/build/ping-pong-full
Для запуска приложения выполнить start.sh
Для остановки - stop.sh
В настройках по умолчанию указан порт 80. Для того, чтобы его занять, необходимо иметь привилегии суперпользователя.


### Использованные библиотеки и фреймворки
DI - Spring 4
DB - MongoDB 3
Net - Netty 4
Cache - Hazelcast

### Рекомендации по масштабированию
Возможно изменение конфигурации MongoDB-кластера
Возможно горизонтальное масштабирование приложения с объединением нескольких нод Hazelcast в один кластер

### Небольшой Стресс-тест
siege -v 'http://localhost:80/handler POST {userId: 132, type: PING}' -c 1000
Результаты(ThinkPad x230  i7-3520M CPU @ 2.90GHz × 4 ):

Transactions:		       57188 hits
Availability:		      100.00 %
Elapsed time:		       29.27 secs
Data transferred:	        1.85 MB
Response time:		        0.01 secs
Transaction rate:	     1953.81 trans/sec
Throughput:		        0.06 MB/sec
Concurrency:		       23.94
Successful transactions:       57188
Failed transactions:	           0
Longest transaction:	        0.74
Shortest transaction:	        0.00
