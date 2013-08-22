package ch.epfl.flamemaker.geometry2d;

import java.awt.geom.Line2D;
/**
 * represente un segment avec un point de depart et un point d'arrive. 
 * @author cherifyasmine
 *
 */
public final class Segment {
	private final Point begin;
	private final Point end;
	
	public Segment(Point begin, Point end) {
		this.begin = begin;
		this.end = end;
	}
	/*
	public Point getCenter() {
		return new Point((begin.x()+end.x())/2, (begin.y()+end.y())/2);
	}
	
	public Point getBegin() {
		return begin;
	}
	
	public Point getEnd() {
		return end;
	}
	
	public double getLength() {
		return Math.sqrt(Math.pow(end.x()-begin.x(), 2) + Math.pow(end.y()-begin.y(), 2));
	}
	*/
	public Segment transformation(AffineTransformation t) {
		Point newBegin = t.transformPoint(begin);
		Point newEnd = t.transformPoint(end);
		return new Segment(newBegin, newEnd);
	}
	
	public Line2D.Double getLine2D() {
		return new Line2D.Double(begin.x(), begin.y(), end.x(), end.y());
	}
	
	@Override
	public String toString() {
		return "Segment [begin=" + begin + ", end=" + end + "]";
	}
}
