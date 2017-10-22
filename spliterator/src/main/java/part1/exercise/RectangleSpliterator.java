package part1.exercise;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;

public class RectangleSpliterator extends Spliterators.AbstractIntSpliterator {
    private final int[][] array;
    private final int endExclusive;
    private int startInclusive;

    private RectangleSpliterator(int[][] array, int startInclusive, int endExclusive) {
        super(endExclusive - startInclusive,
                Spliterator.IMMUTABLE
                        | Spliterator.ORDERED
                        | Spliterator.SIZED
                        | Spliterator.SUBSIZED
                        | Spliterator.NONNULL);
        this.array = array;
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
    }

    public RectangleSpliterator(int[][] array) {
        this(array, 0, array.length);
    }

    private static long checkArrayAndCalcEstimatedSize(int[][] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i].length != array[0].length) {
                throw new IllegalStateException("Array is not recrangular");
            }
        }

        return array.length * array[0].length;
    }

    @Override
    public OfInt trySplit() {
        int length = endExclusive - startInclusive;

        if (length < 2) {
            return null;
        }

        RectangleSpliterator result = new RectangleSpliterator(array, startInclusive, startInclusive += length / 2);

        return result;
    }

    @Override
    public long estimateSize() {
        return endExclusive - startInclusive;
    }

    @Override
    public boolean tryAdvance(IntConsumer action) {
        if (startInclusive < endExclusive) {
            action.accept(array[startInclusive / array[0].length][startInclusive % array[0].length]);
            startInclusive++;
            return true;
        }

        return false;
    }
}


class A {

    protected String val;

    A() {
        setVal();
    }

    public void setVal() {
        val = "A";
    }
}

class B extends A {

    @Override
    public void setVal() {
        val = "B";
    }

    public static void main(String[] args) {
        System.out.println(new B().val);

    }
}