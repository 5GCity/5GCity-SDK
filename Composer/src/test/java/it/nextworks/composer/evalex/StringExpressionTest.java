package it.nextworks.composer.evalex;

import it.nextworks.sdk.evalex.ExtendedExpression;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Marco Capitani on 21/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class StringExpressionTest {

    @Test
    public void testBasic() {
        List<String> used = Arrays.asList("a", "b");
        ExtendedExpression<String> e = ExtendedExpression.stringValued("IF(a+b>3, yes, no)", used);
        e.with("a", 1).with("b", 1);
        assertEquals(e.eval(), "no");
        e.with("a", 2).with("b", 4);
        assertEquals(e.eval(), "yes");
    }
}