package bfst17;

import bfst17.AddressHandling.Address;
import groovy.util.GroovyTestCase;
import org.junit.Test;


/**
 * Created by Hjalte on 21-04-2017.
 */

class AddressTest extends GroovyTestCase {
    Address.Builder b = new Address.Builder();

    @Test
    void testToString() {
        b.city("test");
        b.floor("is");
        b.house("This");
        b.postcode("a");
        Address address = b.build();
        String s = "This, is. a test";
        assertEquals(address.toString(),s);

    }

    @Test
    void parse() {
        String s = "Havnevej 3, 2500 København";
        String k = "islandsBrygge";
        String p = "Fasanvej 5 4212 BB";
        String f = "im a test";
        Address a = Address.parse(s);
        Address b = Address.parse(k);
        Address c = Address.parse(p);
        Address d = Address.parse(f);

        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(c);
        assertNotNull(d);

        assertEquals(s,a.toString());
        assertEquals(k,b.toString());
        assertEquals("",c.toString());
        assertEquals(f,d.toString());
    }

}
