package it.nextworks.sdk;


import org.junit.jupiter.api.Test;

import java.util.function.IntSupplier;
import java.util.stream.Stream;

/**
 * Created by Marco Capitani on 25/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ServiceInformationTest {

    @Test
    public void sillyTest() {
        IntSupplier s = Stream.iterate(2147483000, i -> i + 1).iterator()::next;
        for (int i = 0; i < 10000; i++) {
            int j = s.getAsInt();
            if (i % 100 == 0) {
                System.out.println(j);
            }
        }
    }
}