<H1>Сборки тестового проекта:</H1>
<H3>Сборка проекта локально:</H3>
  <lu>
    <li>Установить <b>JDK 17 или выше</b></li>
    <li>Установить <b>Maven 3.9.8</b></li>
    <li>Установить базу данных Postgres</li>
    <li>Настроить базу данных: создать пользователя(<b>login: postgres, password: postgres</b>), создать базу <b>TaskManagementSystem</b></li>
    <li>Клонировать проект с GitHub</li>
    <li>Зайти в командную строку и перейти в каталог с проектом</li>
    <li>Использовать команду <b>"mvn clean install"</b> для компиляции проекта</li>
    <li>Использовать команду <b>"mvn spring-boot:run"</b> для запуска проекта</li>
  </lu>
<H3>Сборка проекта в Docker</H3>
<lu>
  <li>Установить Docker и настроить его</li>
  <li>Зайти в командную строку и перейти в каталог с проектом</li>
  <li>Использовать команду <b>"mvn clean package"</b> для сборки Jar-файла</li>
  <li>Использовать команду <b>"docker-compose up -d"</b> для создания и запуска контейнеров</li>
</lu>

