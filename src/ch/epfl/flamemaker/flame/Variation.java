package ch.epfl.flamemaker.flame;

import java.util.Arrays;

import java.util.List;

import ch.epfl.flamemaker.geometry2d.*;
/**
 * La classe Variation modélise les variations implémente Transformation (variation est une transformation)
 * elle est caractérisée par : 
 * l'indice de variation (int) 
 * le nom de la variation (String)
 * 
 * @author cherifyasmine
 *
 */
public abstract class Variation implements Transformation {

	private final String name;
	private final int index;
	/**
	 * Constructeur de la classe variation qui stocke les paramétres dans les champs correspondants.
	 * @param index
	 * @param name
	 */
	private Variation(int index, String name) {
		this.index = index;
		this.name = name;
	}
	/**
	 * retourne le nom de la variation
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * retourne l'index de la variation
	 * @return
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * methode de Transformation qui retorne le point p transformé
	 */
	abstract public Point transformPoint(Point p);
	/**
	 * ALL_VARIATIONS est une liste de variations, chaque variation doit  etre a la position correspondant a son index.
	 */
	public final static List<Variation> ALL_VARIATIONS = Arrays.asList(
			new Variation(0, "Linear") {

				@Override
				public Point transformPoint(Point p) {
					return new Point(p.x(), p.y());

				}
			}, new Variation(1, "Sinusoidal") {

				@Override
				public Point transformPoint(Point p) {
					return new Point(Math.sin(p.x()), Math.sin(p.y()));
				}

			}, new Variation(2, "Spherical") {

				@Override
				public Point transformPoint(Point p) {
					double r2 = Math.pow(p.r(), 2);
					double x = p.x() / r2;
					double y = p.y() / r2;
					return new Point(x, y);

				}

			}, new Variation(3, "Swirl") {
				@Override
				public Point transformPoint(Point p) {
					double r2 = Math.pow(p.r(), 2);
					double x = p.x()*Math.sin(r2) - p.y()*Math.cos(r2);
					double y = p.x()*Math.cos(r2) + p.y()*Math.sin(r2);
					return new Point(x, y);
				}

			}, new Variation(4, "Horseshoe") {
				@Override
				public Point transformPoint(Point p) {
					double r = p.r();
					double x = (p.x() - p.y())*(p.x() + p.y())/r;
					double y = 2*p.x()*p.y()/r;
					return new Point(x, y);
				}

			}, new Variation(5, "Bubble") {
				@Override
				public Point transformPoint(Point p) {
					double r2p4 = Math.pow(p.r(), 2)+4;
					double x = 4*p.x()/r2p4;
					double y = 4*p.y()/r2p4;
					return new Point(x, y);
				}

			});
}
