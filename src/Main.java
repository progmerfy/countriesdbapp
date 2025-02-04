import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
    public static void main(String[] args) {
        // Установка локали (например, для русского языка)
        Locale locale = new Locale("ru", "RU");

        // Загрузка ресурсов
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);

        // Использование строк
        String cityNameLabel = messages.getString("city.name");
        String addCityButton = messages.getString("add.city");

        System.out.println(cityNameLabel); // Вывод: Название города
        System.out.println(addCityButton); // Вывод: Добавить город
    }
}