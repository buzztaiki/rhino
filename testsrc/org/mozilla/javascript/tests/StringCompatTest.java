package org.mozilla.javascript.tests;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;


@RunWith(Parameterized.class)
public class StringCompatTest {
    private Context cx;
    private ScriptableObject scope;

    private final String source;
    private final String expected;

    @Parameters(name="{index}: {0}")
    public static Object[][] data() {
        // source, expected
        return new Object[][] {
            {"test.join(['foo' + new java.lang.String('bar')])", "foobar"},
            {"test.join(['foo' + 'bar'])", "foobar"},
            {"test.join([new java.lang.String('foo') + 'bar'])", "foobar"},
            {"test.join([new java.lang.String('foobar')])", "foobar"},
            {"test.join(['foobar'])", "foobar"},

            {"test.joinValues({x: 'foo' + new java.lang.String('bar')})", "foobar"},
            {"test.joinValues({x: 'foo' + 'bar'})", "foobar"},
            {"test.joinValues({x: new java.lang.String('foo') + 'bar'})", "foobar"},
            {"test.joinValues({x: new java.lang.String('foobar')})", "foobar"},
            {"test.joinValues({x: 'foobar'})", "foobar"},

            {"o = {}; o['foo' + new java.lang.String('bar')] = 0; test.joinKeys(o)", "foobar"},
            {"o = {}; o['foo' + 'bar'] = 0; test.joinKeys(o)", "foobar"},
            {"o = {}; o[new java.lang.String('foo') + 'bar'] = 0; test.joinKeys(o)", "foobar"},
            {"o = {}; o[new java.lang.String('foobar')] = 0; test.joinKeys(o)", "foobar"},
            {"o = {}; o['foobar'] = 0; test.joinKeys(o)", "foobar"},

            {"test.join(java.util.Arrays.asList('foo' + new java.lang.String('bar')))", "foobar"},
            {"test.join(java.util.Arrays.asList('foo' + 'bar'))", "foobar"},
            {"test.join(java.util.Arrays.asList(new java.lang.String('foo') + 'bar'))", "foobar"},
            {"test.join(java.util.Arrays.asList(new java.lang.String('foobar')))", "foobar"},
            {"test.join(java.util.Arrays.asList('foobar'))", "foobar"},
        };
    }

    public StringCompatTest(String source, String expected) {
        this.source = source;
        this.expected = expected;
    }

    @Before
    public void setUp() {
        cx = Context.enter();
        scope = cx.initStandardObjects();
        ScriptableObject.putProperty(scope, "test", this);
    }

    @After
    public void tearDown() {
        Context.exit();
    }

    @Test
    public void test() {
        Object result = cx.evaluateString(scope, source, "<test>", 1, null);
        assertEquals(expected, Context.toString(result));
    }

    @Test
    public void testCompiled() {
        Script script = cx.compileString(source, "<test>", 1, null);
        Object result = script.exec(cx, scope);
        assertEquals(expected, Context.toString(result));
    }

    public String join(Collection<String> xs) {
        StringBuilder sb = new StringBuilder();
        for (String x : xs) {
            sb.append(x);
        }
        return sb.toString();
    }

    public String joinKeys(Map<String, String> map) {

        return join(map.keySet());
    }

    public String joinValues(Map<String, String> map) {
        return join(map.values());
    }
}
