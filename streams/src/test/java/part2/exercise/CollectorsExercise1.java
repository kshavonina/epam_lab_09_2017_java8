package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class CollectorsExercise1 {

    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;

        public PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }

        public int getDuration() {
            return duration;
        }
    }


    // "epam" -> "Alex Ivanov 23, Semen Popugaev 25, Ivan Ivanov 33"
    @Test
    public void getEmployeesByEmployer() {
        Map<String, String> result = getEmployees().stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(JobHistoryEntry::getEmployer)
                        .map(employer -> new AbstractMap.SimpleEntry<>(employer, employee.getPerson().toString())))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.mapping(Map.Entry::getValue,
                        Collectors.joining(", "))));
    }

    @Test
    public void getTheCoolestOne() {
        Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    // просуммировать все места где он работал на определенной позиции и вернуть человека кот больше всего
    // проработал на этой позиции
    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        Map<String, Set<Employee>> positionToEmployees = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .map(JobHistoryEntry::getPosition)
                        .map(position -> new AbstractMap.SimpleEntry<>(position, employee)))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey, Collectors.mapping(Map.Entry::getValue,
                        Collectors.toSet())));

        Map<String, Person> result = positionToEmployees.entrySet().stream()
                .map(entry ->
                        new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().stream()
                                .map(employee -> new AbstractMap.SimpleEntry<>(employee.getPerson(), employee.getJobHistory().stream()
                                        .filter(o -> entry.getKey().equals(o.getPosition()))
                                        .mapToInt(o -> o.getDuration())
                                        .sum())) //set of pairs Person-Duration on required position
                                .reduce((e1, e2) -> e1.getValue() > e2.getValue() ? e1 : e2) // one pair Person-MaxDuration
                                .map(e -> e.getKey())))// Person with max duration
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().get()));

        return result;
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy

        // Second option
        // Collectors.toMap
        // iterate twice: stream...collect(...).stream()...
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "Galt", 20),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 21),
                        Arrays.asList(
                                new JobHistoryEntry(4, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 22),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 23),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 24),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 25),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 26),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Bob", "Doe", 27),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 28),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "BA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 29),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 30),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(5, "dev", "abc")
                        )),
                new Employee(
                        new Person("Bob", "White", 31),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

}
