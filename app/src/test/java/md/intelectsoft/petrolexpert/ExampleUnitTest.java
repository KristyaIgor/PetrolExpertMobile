package md.intelectsoft.petrolexpert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testList(){
        String elements[] = { "A", "B", "C", "D", "E" };
        List<String> list = new ArrayList<String>(Arrays.asList(elements));

        elements = new String[] { "A", "B", "C" };
        List<String> list2 = new ArrayList<String>(Arrays.asList(elements));

        System.out.println(list.containsAll(list2)); // true
        System.out.println(list2.containsAll(list)); // false
        assertEquals(list.containsAll(list2), true);
    }
}