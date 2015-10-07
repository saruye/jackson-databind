package com.fasterxml.jackson.databind.creators;

import java.math.BigDecimal;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;

/**
 * This unit test suite that tests use of {@link JsonCreator}
 * with "delegate" constructors and factory methods: ones that
 * take a deserializable type that is bound from JSON content.
 * This is usually done to get two-phase data binding, often using
 * {@link java.util.Map} as the intermediate form.
 */
public class TestConstructFromMap
    extends BaseMapTest
{
    static class ConstructorFromMap
    {
        int _x;
        String _y;

        @JsonCreator
        ConstructorFromMap(Map<?,?> arg)
        {
            _x = ((Number) arg.get("x")).intValue();
            _y = (String) arg.get("y");
        }
    }

    static class FactoryFromPoint
    {
        int _x, _y;

        private FactoryFromPoint(Point p) {
            _x = p.x;
            _y = p.y;
        }

        @JsonCreator
        static FactoryFromPoint createIt(Point p)
        {
            return new FactoryFromPoint(p);
        }
    }

    // Also: let's test BigDecimal-from-JSON-String factory
    static class FactoryFromDecimalString
    {
	int _value;

        private FactoryFromDecimalString(BigDecimal d) {
	    _value = d.intValue();
        }

        @JsonCreator
        static FactoryFromDecimalString whateverNameWontMatter(BigDecimal d)
        {
            return new FactoryFromDecimalString(d);
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testViaConstructor() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        ConstructorFromMap result = m.readValue
            ("{ \"x\":1, \"y\" : \"abc\" }", ConstructorFromMap.class);
        assertEquals(1, result._x);
        assertEquals("abc", result._y);
    }

    public void testViaFactory() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        FactoryFromPoint result = m.readValue("{ \"x\" : 3, \"y\" : 4 }", FactoryFromPoint.class);
        assertEquals(3, result._x);
        assertEquals(4, result._y);
    }

    public void testViaFactoryUsingString() throws Exception
    {
        ObjectMapper m = new ObjectMapper();
        FactoryFromDecimalString result = m.readValue("\"12.57\"", FactoryFromDecimalString.class);
        assertNotNull(result);
        assertEquals(12, result._value);
    }

    public static class Point  {
        public int x;

        public int y;

        /*
         * JDK 1.1 serialVersionUID
         */
        private static final long serialVersionUID = -5276940640259749850L;

        /**
         * Constructs and initializes a point at the origin
         * (0,&nbsp;0) of the coordinate space.
         *
         * @since 1.1
         */
        public Point() {
            this(0, 0);
        }

        /**
         * Constructs and initializes a point with the same location as
         * the specified <code>Point</code> object.
         *
         * @param p a point
         * @since 1.1
         */
        public Point(Point p) {
            this(p.x, p.y);
        }

        /**
         * Constructs and initializes a point at the specified
         * {@code (x,y)} location in the coordinate space.
         *
         * @param x the X coordinate of the newly constructed <code>Point</code>
         * @param y the Y coordinate of the newly constructed <code>Point</code>
         * @since 1.0
         */
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
