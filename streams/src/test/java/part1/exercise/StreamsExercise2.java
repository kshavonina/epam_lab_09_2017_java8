package part1.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="https://youtu.be/kxgo7Y4cdA8">Через тернии к лямбдам, часть 1</a>
 * @see <a href="https://youtu.be/JRBWBJ6S4aU">Через тернии к лямбдам, часть 2</a>
 * @see <a href="https://youtu.be/O8oN4KSZEXE">Stream API, часть 1</a>
 * @see <a href="https://youtu.be/i0Jr2l3jrDA">Stream API, часть 2</a>
 */
public class StreamsExercise2 {

    /**
     * Преобразовать список сотрудников в отображение [компания -> множество людей, когда-либо работавших в этой компании].
     * <p>
     * Пример.
     * <p>
     * Входные данные:
     * Employees = [
     * Employee_1 = {
     * Person = {"Alex", "Doe", 32},
     * JobHistory = [
     * {2, "dev", "epam"},
     * {3, "dev", "yandex"},
     * {1, "dev", "mail"},
     * ]
     * },
     * Employee_2 = {
     * Person = {"Adam", "Smith", 25},
     * JobHistory = [
     * {1, "QA", "yandex"},
     * {2, "QA", "mail"},
     * ]
     * },
     * Employee_3 = {
     * Person = {"John", "Galt", 27},
     * JobHistory = [
     * {1, "QA", "epam"},
     * {3, "dev", "epam"},
     * ]
     * }
     * ]
     * <p>
     * Выходные данные:
     * Result = [
     * "epam" -> [
     * {"Alex", "Doe", 32},
     * {"John", "Galt", 27}
     * ],
     * "yandex" -> [
     * {"Alex", "Doe", 32},
     * {"Adam", "Smith", 25}
     * ],
     * "mail" -> [
     * {"Alex", "Doe", 32},
     * {"Adam", "Smith", 25}
     * ]
     * ]
     */
    @Test
    public void employersStuffList() {
        List<Employee> employees = getEmployees();
        Map<String, Set<Person>> result = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .map(employer -> new AbstractMap.SimpleEntry<>(employer, employee.getPerson())))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.mapping(Map.Entry::getValue,
                        Collectors.toSet())));

        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("epam", new HashSet<>(Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "Doe", 21),
                new Person("John", "White", 22),
                new Person("John", "Galt", 23),
                new Person("John", "Doe", 24),
                new Person("John", "White", 25),
                new Person("John", "Galt", 26),
                new Person("Bob", "Doe", 27),
                new Person("John", "White", 28),
                new Person("John", "Galt", 29),
                new Person("John", "Doe", 30),
                new Person("Bob", "White", 31))));
        expected.put("google", new HashSet<>(Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "Galt", 23),
                new Person("John", "Galt", 26),
                new Person("John", "Galt", 29)
        )));
        expected.put("yandex", new HashSet<>(Arrays.asList(
                new Person("John", "Doe", 21),
                new Person("John", "Doe", 24),
                new Person("Bob", "Doe", 27),
                new Person("John", "Doe", 30)
        )));
        expected.put("abc", new HashSet<>(Arrays.asList(
                new Person("John", "Doe", 21),
                new Person("John", "Doe", 24),
                new Person("Bob", "Doe", 27),
                new Person("John", "Doe", 30)
        )));
        assertEquals(expected, result);
    }

    /**
     * Преобразовать список сотрудников в отображение [компания -> множество людей, начавших свою карьеру в этой компании].
     * <p>
     * Пример.
     * <p>
     * Входные данные:
     * Employees = [
     * Employee_1 = {
     * Person = {"Alex", "Doe", 32},
     * JobHistory = [
     * {2, "dev", "epam"},
     * {3, "dev", "yandex"},
     * {1, "dev", "mail"},
     * ]
     * },
     * Employee_2 = {
     * Person = {"Adam", "Smith", 25},
     * JobHistory = [
     * {1, "QA", "yandex"},
     * {2, "QA", "mail"},
     * ]
     * },
     * Employee_3 = {
     * Person = {"John", "Galt", 27},
     * JobHistory = [
     * {1, "QA", "epam"},
     * {3, "dev", "epam"},
     * ]
     * }
     * ]
     * <p>
     * Выходные данные:
     * Result = [
     * "epam" -> [
     * {"Alex", "Doe", 32},
     * {"John", "Galt", 27}
     * ],
     * "yandex" -> [
     * {"Adam", "Smith", 25}
     * ]
     * ]
     */
    @Test
    public void indexByFirstEmployer() {
        List<Employee> employees = getEmployees();

        Map<String, Set<Person>> result = employees.stream()
                .map(employee -> new AbstractMap.SimpleEntry<>(employee.getJobHistory().get(0).getEmployer(),
                        employee.getPerson()))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.mapping(Map.Entry::getValue,
                        Collectors.toSet())));

        Map<String, Set<Person>> expected = new HashMap<>();
        expected.put("epam", new HashSet<>(Arrays.asList(
                new Person("John", "Galt", 20),
                new Person("John", "White", 22),
                new Person("John", "Galt", 23),
                new Person("John", "White", 25),
                new Person("John", "Galt", 26),
                new Person("John", "White", 28),
                new Person("John", "Galt", 29),
                new Person("Bob", "White", 31)
        )));
        expected.put("yandex", new HashSet<>(Arrays.asList(
                new Person("John", "Doe", 21),
                new Person("John", "Doe", 24),
                new Person("Bob", "Doe", 27),
                new Person("John", "Doe", 30)
        )));
        assertEquals(expected, result);
    }

    /**
     * Преобразовать список сотрудников в отображение [компания -> сотрудник, суммарно проработавший в ней наибольшее время].
     * Гарантируется, что такой сотрудник будет один.
     */
    @Test
    public void greatestExperiencePerEmployer() {
        List<Employee> employees = getEmployees();

        Map<String, Person> result = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .map(employer -> new AbstractMap.SimpleEntry<>(employer, employee)))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.mapping(Map.Entry::getValue,
                        Collectors.toSet()))) // made mapping Employer - Employees
                .entrySet().stream().map(entry ->
                        new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().stream()
                                .map(employee -> new AbstractMap.SimpleEntry<>(employee.getPerson(), employee.getJobHistory().stream()
                                        .filter(o -> entry.getKey().equals(o.getEmployer()))
                                        .mapToInt(o -> o.getDuration())
                                        .sum())) //set of pairs Person-Duration
                                .reduce((e1, e2) -> e1.getValue() > e2.getValue() ? e1 : e2) // one pair Person-MaxDuration
                                .map(e -> e.getKey())))// Person with max duration
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().get()));

        Map<String, Person> expected = new HashMap<>();
        expected.put("epam", new Person("John", "White", 28));
        expected.put("google", new Person("John", "Galt", 29));
        expected.put("yandex", new Person("John", "Doe", 30));
        expected.put("abc", new Person("John", "Doe", 30));
        assertEquals(expected, result);
    }


    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee("John", "Galt", 20,
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee("John", "Doe", 21,
                        Arrays.asList(
                                new JobHistoryEntry(4, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee("John", "White", 22,
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee("John", "Galt", 23,
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee("John", "Doe", 24,
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee("John", "White", 25,
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee("John", "Galt", 26,
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee("Bob", "Doe", 27,
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "QA", "abc"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee("John", "White", 28,
                        Collections.singletonList(
                                new JobHistoryEntry(8, "BA", "epam")
                        )),
                new Employee("John", "Galt", 29,
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(3, "dev", "google")
                        )),
                new Employee("John", "Doe", 30,
                        Arrays.asList(
                                new JobHistoryEntry(5, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(5, "dev", "abc")
                        )),
                new Employee("Bob", "White", 31,
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

}
