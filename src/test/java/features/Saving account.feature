# language: ru
  Функционал: Накопительный счет

    Структура сценария: Проверка итоговой ставки
      Пусть Ярик положил 50000 рублей на накопительный счет
      Когда прошло <месяц> месяцев
      То итоговая % ставка (R) составила <ставка>
        Примеры:
        | месяц | ставка |
        |   1   |   4    |
        |   2   |   4    |
        |   3   |   5    |
        |   4   |   5    |
        |   5   |   5    |
        |   6   |   6    |
        |   7   |   6    |
        |   8   |   6    |
        |   9   |   6    |
        |   10  |   6    |
        |   11  |   6    |
        |   12  |   8,5  |
        |   13  |   8,5  |
        |   18  |   8,5  |