package lambda.part3.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"WeakerAccess"})
public class Mapping {

    private static class MapHelper<T> {

        private final List<T> list;

        public MapHelper(List<T> list) {
            this.list = list;
        }

        public List<T> getList() {
            return list;
        }

        // ([T], T -> R) -> [R]
        public <R> MapHelper<R> map(Function<T, R> f) {
            List<R> resultList = new ArrayList<>(list.size());
            list.forEach(t -> resultList.add(f.apply(t)));
            return new MapHelper<R>(resultList);
        }

        // ([T], T -> [R]) -> [R]
        public <R> MapHelper<R> flatMap(Function<T, List<R>> f) {
            List<R> resultList = new ArrayList<>(list.size());
            list.forEach(t -> resultList.addAll(f.apply(t)));
            return new MapHelper<R>(resultList);
        }
    }

    private List<JobHistoryEntry> changeQaToQa(List<JobHistoryEntry> jhe) {
        return new MapHelper<>(jhe).map(e -> e.getPosition().equals("qa") ? e.withPosition("QA") : e).getList();
    }

    private List<JobHistoryEntry> addOneYear(List<JobHistoryEntry> jhe) {
        return new MapHelper<>(jhe).map(e -> e.withDuration(e.getDuration() + 1)).getList();
    }

    @Test
    public void mapping() {
        List<Employee> employees = getEmployees();

        List<Employee> mappedEmployees = new MapHelper<>(employees)
                .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(e -> e.withJobHistory(changeQaToQa(e.getJobHistory())))
                .getList();
                /*
                Изменить имя всех сотрудников на John .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                Добавить всем сотрудникам 1 год опыта .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                Заменить все qa на QA
                */

        List<Employee> expectedResult = getEmployee();

        assertEquals(mappedEmployees, expectedResult);
    }

    private List<Employee> getEmployee() {
        return getEmployees();
    }

    private static class LazyMapHelper<T, R> {
        private final List<T> list;
        private final Function<T, R> function;

        public LazyMapHelper(List<T> list, Function<T, R> function) {
            this.list = list;
            this.function = function;
        }

        public static <T> LazyMapHelper<T, T> from(List<T> list) {
            return new LazyMapHelper<>(list, Function.identity());
        }

        public List<R> force() {
            return new MapHelper<>(list).map(function).getList();
        }

        public <R2> LazyMapHelper<T, R2> map(Function<R, R2> f) {
            return new LazyMapHelper<>(list, function.andThen(f));
        }
    }

    private static class LazyFlatMapHelper<T, R> {

        private final List<T> list;
        private final Function<T, List<R>> mapper;

        private LazyFlatMapHelper(List<T> list, Function<T, List<R>> mapper) {
            this.list = list;
            this.mapper = mapper;
        }

        public static <T> LazyFlatMapHelper<T, T> from(List<T> list) {
            return new LazyFlatMapHelper<>(list, Collections::singletonList);
        }

        public <U> LazyFlatMapHelper<T, U> flatMap(Function<R, List<U>> remapper) {
            return new LazyFlatMapHelper<>(list, mapper.andThen(result -> force(result, remapper)));
            /*return new LazyFlatMapHelper<>(list, mapper.andThen(result -> result.stream()
                    .flatMap(element -> remapper.apply(element).stream())
                    .collect(Collectors.toList())));*/
        }

        public List<R> force() {
            return force(list, mapper);
        }

        private <A, B> List<B> force(List<A> list, Function<A, List<B>> mapper) {
            List<B> result = new ArrayList<>(list.size());
            list.forEach(element -> result.addAll(mapper.apply(element)));
            return result;
        }
    }

    @Test
    public void lazyFlatMapping() {
        List<Employee> employees = getEmployees();
        List<JobHistoryEntry> force = LazyFlatMapHelper.from(employees)
                                                       .flatMap(Employee::getJobHistory)
                                                       .force();

        List<Character> force1 = LazyFlatMapHelper.from(employees)
                                                  .flatMap(Employee::getJobHistory)
                                                  .flatMap(entry -> entry.getPosition()
                                                                         .chars()
                                                                         .mapToObj(sym -> (char) sym)
                                                                         .collect(Collectors.toList()))
                                                  .force();


        System.out.println();

    }

    @Test
    public void lazyMapping() {
        List<Employee> employees = getEmployees();

        List<Employee> mappedEmployees = LazyMapHelper.from(employees)
                .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(e -> e.withJobHistory(changeQaToQa(e.getJobHistory())))
                .force();
                /*
                Изменить имя всех сотрудников на John .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                Добавить всем сотрудникам 1 год опыта .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                Заменить все qu на QA
                */

        List<Employee> expectedResult = getEmployee();

        assertEquals(mappedEmployees, expectedResult);
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
            new Employee(
                new Person("a", "Galt", 30),
                Arrays.asList(
                        new JobHistoryEntry(2, "dev", "epam"),
                        new JobHistoryEntry(1, "dev", "google")
                )),
            new Employee(
                new Person("b", "Doe", 40),
                Arrays.asList(
                        new JobHistoryEntry(3, "qa", "yandex"),
                        new JobHistoryEntry(1, "qa", "epam"),
                        new JobHistoryEntry(1, "dev", "abc")
                )),
            new Employee(
                new Person("c", "White", 50),
                Collections.singletonList(
                        new JobHistoryEntry(5, "qa", "epam")
                ))
        );
    }
}
