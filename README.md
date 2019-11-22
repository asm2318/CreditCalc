# CreditCalc
Создание кредитного калькулятора.  
Задача: программа должна выводить график погашения кредита (таблица) на основании заданных параметров.  
P - процентная ставка;  
S - сумма кредита;  
N - срок кредита в месяцах;  
D - дата выдачи;  
F - размер первоначального взноса;  
T - вид платежа.

Условия:
1. MVC - отдельные классы на каждый аспект.  
2. Структура текстового файла:  
  <процентная ставка><сумма кредита><срок кредита в месяцах><дата выдачи кредита><размер первоначального взноса><вид платежа>  
3. Аннуитетный платеж:  
  -сумма платежа-  
  payment = S*(p+(p/((1+N)^N-1),  
  где p = P/100/12 (нормализованная месячная процентная ставка);  
  -процентная составляющая-  
  Percent = p * Rem,  
  где Rem - остаток задолженности по кредиту;  
  -сумма в погашение основного долга-  
  Principal = Payment - Percent.  
4. Дифференцированный платеж.  
5. Кроме графика погашения программа должна вычислять и показывать сумму уплаченных процентов.  
6. Структура таблицы (график погашения):  
  Дата | Сумма к оплате | Проценты | Основной долг  
7. Расчеты в рублях и копейках. Округление суммы платежа. Остатки округления добавить к сумме последнего платежа.   
8. Сумма всех погашений основного долга = сумма займа.  
9. Платежи производятся ежемесячно одного и того же числа. Если числа не существует, платеж переносится на 1-е число следующего месяца. Если дата выпадает на субботу или воскресенье, платеж переносится на следующий понедельник.  

Этап 1:  
Реализовать чтение настроек из файла .csv, расчет результатов и выведение плана погашения в файле .json (1 млн. операций - менее 1 сек.).
