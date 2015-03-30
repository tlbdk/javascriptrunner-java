/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.nversion.javascriptrunner;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author G48391
 */
public class JavaScriptRunnerTest {

    public static class ValueCountTest2 {
        private String value1;

        public String getValue() {
            return value1;
        }

        public void setValue(String value1) {
            this.value1 = value1 + ",Hello";
        }
    }
    
    public static class ValueCountTest {
        private ValueCountTest2 value2;
        private String value1;
        private int count1;

        public String getValue() {
            return value1;
        }

        public void setValue(String value1) {
            this.value1 = value1 + ",Hello";
        }
        
        public ValueCountTest2 getValue2() {
            return value2;
        }

        public void setValue2(ValueCountTest2 value1) {
            this.value2 = value1;
        }

        public int getCount() {
            return count1;
        }

        public void setCount(int count1) {
            this.count1 = count1;
        }
    }
    
    /**
     * Test of get method, of class JavaScriptRunner.
     * @throws java.lang.Exception
     */
    @Test
    public void testGet() throws Exception {
        System.out.println("get");
        JavaScriptRunner instance = new JavaScriptRunner();
        instance.eval("var ValueCountTest = { value: 'test', count : 10, value2: { value: 'test2' } };");
        ValueCountTest result = instance.get("ValueCountTest", ValueCountTest.class);
        assertEquals("test,Hello", result.getValue());
    }

    /**
     * Test of invoke method, of class JavaScriptRunner.
     * @throws java.lang.Exception
     */
    @Test
    public void testInvoke() throws Exception {
        System.out.println("invoke");
        JavaScriptRunner instance = new JavaScriptRunner();
        instance.eval("var test = function(test) { return { value: test, count : 10, value2: { value: 'test2' } }; };");
        ValueCountTest result = instance.invoke("test", ValueCountTest.class, "Hello World");
        assertEquals("Hello World,Hello", result.getValue());
    }
    
    /**
     * Test of invoke method, of class JavaScriptRunner.
     * @throws java.lang.Exception
     */
    @Test
    public void testInvokePassback() throws Exception {
        System.out.println("invoke");
        JavaScriptRunner instance = new JavaScriptRunner();
        instance.eval("var ValueCountTest = { value: 'test', count : 10, value2: { value: 'test2' } };");
        instance.eval("var passback = function(test) { return test; };");
        ValueCountTest result1 = instance.get("ValueCountTest", ValueCountTest.class);
        ValueCountTest result2 = instance.invoke("passback", ValueCountTest.class, result1);
        assertEquals("test,Hello,Hello", result2.getValue());
    }
}
