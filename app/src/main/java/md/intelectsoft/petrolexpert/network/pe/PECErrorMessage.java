package md.intelectsoft.petrolexpert.network.pe;

public class PECErrorMessage {
    public static String getErrorMessage(int code){
        String msg = "";
        switch (code){
            case -1 :  msg = "Внутренняя ошибка!"; break;
            case 0 :  msg = "Успешно!"; break;
            case 1 :  msg = "Предприятия не настроено"; break;
            case 2 :  msg = "Системная ошибка от службы"; break;
            case 3 :  msg = "Устройсвто не активно"; break;
            case 4 :  msg = "К устройству не привязана касса"; break;
            case 5 :  msg = "К устройство не привязан владелец"; break;
            case 6 :  msg = "Карта не существует"; break;
            case 7 :  msg = "Карта недействительна"; break;
            case 8 :  msg = "Клиент недействителен"; break;
            case 9 :  msg = "Счет клиента не действителен"; break;
            case 10 :  msg = "Карта отключена"; break;
            case 11 :  msg = "Договоры с покупателями отключены"; break;
            case 12 :  msg = "К кассе не привязано рабочее место"; break;
            case 13 :  msg = "Ассортимент не установлен"; break;
            case 14 :  msg = "Этот товар не указан"; break;
            case 15 :  msg = "Данный ассортимент не предназначен для продажи"; break;
            case 16 :  msg = "По выбранной карте продажа в данном офисе запрещена"; break;
            case 17 :  msg = "Устройство не зарегестрировано"; break;
            case 18 :  msg = "Тип оплаты не нейзвестный"; break;
            case 19 :  msg = "У пользователя нет прав"; break;
            case 20 :  msg = "Договор с покупателем не существует"; break;
            case 21 :  msg = "Счет не закрыт"; break;
            case 22 :  msg = "Смена не открыта"; break;
            case 23 :  msg = "Смена не недействительна"; break;
            case 24 :  msg = "Ассортимент не найден"; break;
            case 25 :  msg = "Оффис не найден"; break;
            case 26 :  msg = "Не правильная дата счета"; break;
            case 27 :  msg = "Лимит превышен"; break;
            case 28 :  msg = "Счет уже существует"; break;
            case 29 :  msg = "Период заблокирован"; break;
            case 30 :  msg = "Накладная не существует"; break;
            case 9022 :  msg = "Нет подключения к контролеру"; break;
            case 9023 :  msg = "Нет подключения к базе данных"; break;
            case 1027 :  msg = "Отменено"; break;
            case 1028 :  msg = "Нет клиента"; break;
            case 1029 :  msg = "Не найден счет клиента"; break;
            case 1030 :  msg = "Нет денег на клиентском счете"; break;
            case 1031 :  msg = "Нет денег на клиентском счете, нет кредитных прав"; break;
            case 1032 :  msg = "Превышена максимальная сумма"; break;
            case 1033 :  msg = "Проблема с фискальном регистраторе"; break;
            case 1034 :  msg = "Только онлайн оплаты"; break;
            case 1035 :  msg = "Станция не существует"; break;
            case 9024 :  msg = "Пистолен не найден."; break;

            default: msg = "Unknow!"; break;
        }
        return " " + msg;
    }
}
