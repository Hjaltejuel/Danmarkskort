package bfst17;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Hjalte on 21-04-2017.
 */
class AddressTest {
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
}