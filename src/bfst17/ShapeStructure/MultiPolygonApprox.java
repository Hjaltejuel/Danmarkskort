package bfst17.ShapeStructure;

import bfst17.OSMData.OSMNode;
import bfst17.OSMData.OSMRelation;
import bfst17.OSMData.OSMWay;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.*;

public class MultiPolygonApprox extends PolygonApprox {
	private static final long serialVersionUID = 1L;
	byte[] pointtypes;

	Map<OSMNode, OSMWay> wayMap;
	
	public MultiPolygonApprox(List<? extends List<? extends Point2D>> rel) {
		wayMap = new HashMap<>();
		OSMRelation merged = new OSMRelation();
		mergedWays(rel);
		wayMap.forEach((key, way) -> {
			if(key == way.get(0)){
				merged.add(way);
			}
		});


		int npoints = 0;
		for (List<?> l : rel) npoints += l.size();
		coords = new float[npoints<<1];
		pointtypes = new byte[npoints];
		Arrays.fill(pointtypes, (byte) PathIterator.SEG_LINETO);
		int coord = 0;
		int point = 0;
		for (List<? extends Point2D> l : rel) {
			pointtypes[point] = (byte) PathIterator.SEG_MOVETO;
			point += l.size();
			for (Point2D p : l) {
				coords[coord++] = (float)p.getX();
				coords[coord++] = (float)p.getY();
			}
		}
		init();
	}
	
	public double distTo(Point2D p) {
		double dist = Double.MAX_VALUE;
		double px = p.getX();
		double py = p.getY();
		for (int i = 2 ; i < coords.length ; i += 2) {
			if (pointtypes[i >> i] != PathIterator.SEG_MOVETO)
				dist = Math.min(dist, Line2D.ptSegDist(coords[i-2], coords[i-1], coords[i], coords[i+1], px, py));
		}
		return dist;
	}

	public PathIterator getPathIterator(AffineTransform at, float pixelsq) {
		return new MultiPolygonApproxIterator(at, pixelsq);
	}
	
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new MultiPolygonApproxIterator(at, (float) (flatness * flatness));
	}
	
	class MultiPolygonApproxIterator extends PolygonApproxIterator {
		public MultiPolygonApproxIterator(AffineTransform _at, float _pixelsq) {
			super(_at, _pixelsq);
		}

		public void next() {
			float fx = coords[index];
			float fy = coords[index+1];
			index += 2;
			while (index < coords.length - 2 && pointtypes[(index >> 1) + 1] == PathIterator.SEG_LINETO && 
				distSq(fx, fy, coords[index], coords[index+1]) < approx) index += 2;
		}

		public int currentSegment(float[] c) {
			if (isDone()) {
	            throw new NoSuchElementException("poly approx iterator out of bounds");
	        }
	        c[0] = coords[index];
	        c[1] = coords[index+1];
	        if (at != null) {
	            at.transform(c, 0, c, 0, 1);
	        }
	        return pointtypes[index >> 1];
		}

		public int currentSegment(double[] coords) {
			throw new UnsupportedOperationException("Unexpected call to PolygonApprox.contains(Rectangle2D)");
		}
	}

	public void mergedWays(List<? extends List<? extends Point2D>> rel){
		for (List<? extends Point2D> way : rel){
			if(way != null){
				OSMWay before = wayMap.remove(way.get(0));
				OSMWay after = wayMap.remove(way.get(way.size()-1));
				OSMWay merged = new OSMWay();
				if (before != null){
					OSMWay reversedBefore = (OSMWay) before.clone();
					Collections.reverse(reversedBefore);
					if(reversedBefore.equals(after)){
						before = null;
					}
				}

				if (before != null){
					Collections.reverse(before);
					merged.addAll(before);
				}
				merged.addAll(way);
				if(after != null){
					merged.addAll(after);
				}
				wayMap.put(merged.get(0), merged);
				OSMWay reservedMerged = (OSMWay) merged.clone();
				Collections.reverse(reservedMerged);
				wayMap.put(reservedMerged.get(0), reservedMerged);
			}
		}
	}
}
