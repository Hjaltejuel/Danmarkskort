package bfst17.KDTrees;

import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by Mads on 16/05/2017.
 */
public class CityNamesKDTreeTest {
    CityNamesKDTree testTree;
    @Before
    public void setUp() throws Exception {
        testTree = new CityNamesKDTree();
    }

    @Test
    public void getInRange() throws Exception {
        CityNamesKDTree.CityNameTreeNode by1 = testTree.new CityNameTreeNode("By1", new Point2D.Double(1,4));
        CityNamesKDTree.CityNameTreeNode by2 = testTree.new CityNameTreeNode("By2", new Point2D.Double(2,2));
        CityNamesKDTree.CityNameTreeNode by3 = testTree.new CityNameTreeNode("By3", new Point2D.Double(5,8));

        testTree.insert(by1);
        testTree.insert(by2);
        testTree.insert(by3);

        HashSet<TreeNode> byer = testTree.getInRange(new Rectangle2D.Double(0, 0, 4, 5));

        //Kun by1 og by2 burde findes
        assertTrue((byer.size() == 2) && (byer.contains(by1) && byer.contains(by2)));


        //Ingen byer
        byer = testTree.getInRange(new Rectangle2D.Double(0, 0, 1, 1));
        assertTrue(byer.size() == 0);

        //Alle byer
        byer = testTree.getInRange(new Rectangle2D.Double(0, 0, 10, 10));
        assertTrue((byer.size() == 3) && (byer.contains(by1) && byer.contains(by2) && byer.contains(by3)));

    }

}