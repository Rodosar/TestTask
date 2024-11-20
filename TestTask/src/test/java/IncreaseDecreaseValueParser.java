import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncreaseDecreaseValueParser {

    private static final String FILE_PATH = "C:\\Users\\user\\Desktop\\testapp.log";
    private static final String PATTERN_INC_SUM = "increase'\\s*:\\s*'([^']+)'";
    private static final String PATTERN_DEC_SUM = "decrease'\\s*:\\s*'([^']+)'";
    private static final String PATTERN_TURNOVER_INCREASE = "Обороты по зачислению\\s((\\d+))";
    private static final String PATTERN_TURNOVER_DECREASE = "Обороты по списанию\\s((\\d+))";

    private static String LOGS = null;

    static {
        try {
            LOGS = readFileAsString();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int sumRowsInc = sumRows(LOGS, PATTERN_INC_SUM);
        int sumRowsDec = sumRows(LOGS, PATTERN_DEC_SUM);

        String turnoverIncResult = compareValues(sumRowsInc, PATTERN_TURNOVER_INCREASE);
        String turnoverDecResult = compareValues(sumRowsDec, PATTERN_TURNOVER_DECREASE);

        System.out.println("\nСумма всех значений из колонки 'Приход' = " + sumRowsInc);
        System.out.println("Сумма всех значений из колонки 'Расход' = " + sumRowsDec);
        System.out.println("\nОбороты по зачислению: " + turnoverIncResult + sumRowsInc + " сумме колонок по приходам");
        System.out.println("Обороты по списанию: " + turnoverDecResult + sumRowsDec + " сумме колонок по расходам");
    }

    // Читаем файл и сохраняем его содержимое в строку
    private static String readFileAsString() throws IOException {
        Path path = Paths.get(IncreaseDecreaseValueParser.FILE_PATH);
        return new String(Files.readAllBytes(path));
    }

    // Подсчитываем сумму строк переданной колонки
    private static int sumRows(String logs, String pattern) {
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(logs);
        int sum = 0;

        while (matcher.find()) {
            sum += Integer.parseInt(matcher.group(1));
        }
        return sum;
    }

    // Сравниваем оборот с суммой колонки и возвращаем оборот со знаком = или !=
    private static String compareValues(int sumRows, String turnoverPattern) {
        Optional<Integer> turnoverValue = findTurnoverValue(turnoverPattern);

        if (turnoverValue.isEmpty()) {
            return "Значение оборота не найдено!";
        }

        int sumTurnovers = turnoverValue.get();
        return (sumTurnovers == sumRows) ? sumTurnovers + " = " : sumTurnovers + " != ";
    }

    // Поиск в логах оборота по списанию или зачислению
    private static Optional<Integer> findTurnoverValue(String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(LOGS);

        if (matcher.find()) {
            return Optional.of(Integer.parseInt(matcher.group(1)));
        }
        return Optional.empty();
    }
}