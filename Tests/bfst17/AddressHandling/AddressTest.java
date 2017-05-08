package bfst17.AddressHandling;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Michelle on 5/8/2017.
 */
public class AddressTest {
    Address address;
    @Before
    public void setUp() throws Exception {
        address = Address.parse("VesterVang 20, 2300 Køb");

    }

    @Test
    public void getStreetAndHouseNum() throws Exception {
        assertNotNull(address.getPostcodeAndCity());
        assertEquals("2300 Køb",address.getPostcodeAndCity());


    }

    @Test
    public void getPostcodeAndCity() throws Exception {
        assertNotNull(address.getStreetAndHouseNum());
        assertEquals("VesterVang 20", address.getStreetAndHouseNum());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("VesterVang 20 2300 Køb", address.toString());

    }

    @Test
    public void parse() throws Exception {
        assertNotNull(Address.parse("København"));
        assertNotNull(Address.parse("2300 København"));
        assertNotNull(Address.parse("Vesters 2300 København"));
        assertNotNull(Address.parse("vesters 23 2300 København"));

    }

}