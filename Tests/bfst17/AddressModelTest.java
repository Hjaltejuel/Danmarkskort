package bfst17;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Hjalte on 21-04-2017.
 */
class AddressModelTest {
    static AddressModel model;
    static HashMap<String,Region> testMapRegion;
    static HashMap<String,Point2D> testMapAddress;
    static String s;
    static Point2D k;
    static Region m;


    @BeforeAll
    static void initialize(){
        model = new AddressModel();
        testMapRegion = model.getRegionToShape();
        testMapAddress = model.getAddressToCordinate();
        s = "Test";
        k = new Point2D.Double(5,5);
        m = new Region(null,null);
    }
    @Test
    void getAddressToCordinate() {
        HashMap<String,Point2D> testMapAddress = model.getAddressToCordinate();
        assertNotNull(testMapAddress);
    }

    @Test
    void getRegionToShape() {
        HashMap<String,Region> testMapRegion = model.getRegionToShape();
        assertNotNull(testMapRegion);

    }

    @Test
    void put() {
        model.put(s,k);
        assertEquals(k,testMapAddress.get(s));
        testMapAddress.clear();

    }

    @Test
    void putRegion() {
        model.putRegion(s,m);
        assertEquals(m,testMapRegion.get(s));
        testMapRegion.clear();


    }

    @Test
    void getRegion() {
        testMapRegion.put(s,m);
        Region region = model.getRegion(s);
        assertEquals(region,m);
        testMapRegion.clear();


    }

    @Test
    void getPoint2DToAddress() {
        testMapAddress.put(s,k);
        Point2D point = model.getPoint2DToAddress(s);
        assertEquals(point,k);
        testMapAddress.clear();
    }

}