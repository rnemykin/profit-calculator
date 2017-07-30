# language: ru
Функционал: Ранжирование групп продуктов

  Структура сценария: Проверка ранга для пакета продуктов
    Допустим есть предложение с пакетом продуктов <пакет>
    Когда сумма профита по продукту равна <профит_продукта>
    И максимальная ставка равна <ставка>
    А сумма профита по опции Мультикарты равна <профит_опции>
    То значение ранга по данному предложению равно <ранг>
    Примеры:
      | пакет                   | профит_продукта | профит_опции | ставка | ранг    |
      | SAVING_ACCOUNT:2,CARD:1 | 1087470         | 0            | 9,2    | 1311229 |
      | SAVING_ACCOUNT:2,CARD:1 | 1044358         | 11680        | 8,2    | 1239227 |
      | SAVING_ACCOUNT:2,CARD:1 | 1044358         | 8760         | 8,2    | 1237183 |
      | SAVING_ACCOUNT:2,CARD:1 | 1044358         | 2920         | 8,2    | 1233095 |
      | SAVING_ACCOUNT:2        | 1044358         | 0            | 8,2    | 1201051 |
      | DEPOSIT:1               | 26205           | 0            | 6,75   | 55094   |
      | SAVING_ACCOUNT:1        | 21112           | 0            | 7,3    | 54278   |
