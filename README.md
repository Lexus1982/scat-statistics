### Проект для сбора и обработки статистических данных о сетевом трафике абонентов

##### Кратко:

Данные о сетевом трафике экспортируются платформой СКАТ DPI (https://vasexperts.ru/products/skat/)
при помощи протокола IPFIX и заданными платформой шаблонами.
 
Проект предусматривает получение этих данных от платформы и выполнение в режиме реального времени некоторых выборок из общего потока.
Агрегированные данные затем сохраняются во внешней СУБД, доступ к которым предоставляется через API проекта.

[Документация](https://github.com/alexand84/scat-statistics/wiki "Wiki")