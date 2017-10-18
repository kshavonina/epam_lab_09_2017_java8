package lambda.part2.exercise;

import data.Person;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class ArrowNotationExercise {

    @Test
    public void getAge() {
        // Person -> Integer
        final Function<Person, Integer> getAge = Person::getAge;

        assertEquals(Integer.valueOf(33), getAge.apply(new Person("", "", 33)));
    }

    private static boolean sameAges(Person p, Person q) {
        return p.getAge() ==  q.getAge();
    }

    @Test
    public void compareAges() {
        // compareAges: (Person, Person) -> boolean
        BiPredicate<Person, Person> compareAges = ArrowNotationExercise::sameAges;

        assertEquals(true, compareAges.test(new Person("a", "b", 22), new Person("c", "d", 22)));
    }

    // getFullName: Person -> String
    private static String getFullName(Person person) {
        return person.getFirstName() + " " + person.getLastName();
    }

    // ageOfPersonWithTheLongestFullName: (Person -> String) -> ((Person, Person) -> int)
    private static BiFunction<Person, Person, Integer> ageOfPersonWithTheLongestFullName(Function<Person, String> function) {
        return (o1, o2) -> function.apply(o1).length() > function.apply(o2).length() ? o1.getAge() : o2.getAge();
    }

    /*private static int ageOfPersonWithTheLongestFullName(Person p, Person q) {
        return getFullName(p).length() > getFullName(q).length() ? p.getAge() : q.getAge();
    }*/

    @Test
    public void getAgeOfPersonWithTheLongestFullName() {
        // Person -> String
        final Function<Person, String> getFullName = o -> o.getFirstName() + " " + o.getLastName();

        // (Person, Person) -> Integer
        final BiFunction<Person, Person, Integer> ageOfPersonWithTheLongestFullName =
                ageOfPersonWithTheLongestFullName(getFullName);

        assertEquals(
                Integer.valueOf(1),
                ageOfPersonWithTheLongestFullName.apply(
                        new Person("a", "b", 2),
                        new Person("aa", "b", 1)));
    }
}