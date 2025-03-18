**О запуске приложения**

**1.** Для запуска приложения надо запустить приложение сервера (_ServerApp_), которое находится в модуле vote-server.

**2.** После чего необходимо запустить тестового клиента из модуля vote-client - запускаете класс _Main_ (возможно, несколькко экземпляров (если этого сделать не удается, то возможно вам необходимо изменить настройки вашей IDE)).

**3.** На сервере желательно выполнить команду "load filename", где filename - имя файла в главной директории.

**4.** Пользователь проходит логирование в системе, а далее может выполнять следующие команды:
         ◦ login -u=username – подключиться к серверу с указанным именем 
        пользователя (все остальные команды доступны только после выполнения 
        login)
         ◦ create topic -n=<topic> - создает новый раздел c уникальным именем 
        заданным в параметре -n
         ◦ view - показывает список уже созданных разделов в формате: <topic (votes 
        in topic=<count>)>
         ▪ опциональный параметр -t=<topic> - в этом случае команда показывает 
        список голосований в конкретном разделе
         ◦ create vote -t=<topic> - запускает создание нового голосования в разделе 
        указанном в параметре -t
         Для создания голосования (команда create vote -t=<topic>) нужно 
        последовательно запросить у пользователя:
         • название (уникальное имя)
         • тему голосования (описание)
         • количество вариантов ответа
         • варианты ответа
         ◦ view -t=<topic> -v=<vote> - отображает информацию по голосованию
         ▪ тема голосования
         ▪ варианты ответа и количество пользователей выбравших данный 
        вариант
         ◦ vote -t=<topic> -v=<vote> - запускает выбор ответа в голосовании для 
        текущего пользователя
        ◦ delete -t=topic -v=<vote> - удалить голосование с именем <vote> из <topic> 
        (удалить может только пользователь его создавший)
         ◦ exit - завершение работы программы

Также есть команды сервера:
◦ load <filename> - загрузка данных из файла
◦ save <filename> – сохранение в файл всех созданных разделов и 
принадлежащим им голосований + их результатов (в любом удобном 
формате).
◦ exit - завершение работы программы
