package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Segment;
/**
 * La classe AffineTransformationsComponent modélise le composant permettant d'afficher les composantes affines des transformations Flame caractérisant la fractale.
 * <p>Elle posséde les champs privés : le cadre du dessin, une affineTranformation, highlightedTransformationIndex qui donne l'index de la transformation à mettre en évidence.</p>
 * @see ObservableFlameBuilder
 * @see Rectangle
 * @see AffineTransformation
 * @author cherifyasmine
 *
 */
@SuppressWarnings("serial")
public class AffineTransformationsComponent extends JComponent {
	private ObservableFlameBuilder flameBuilder;
	private Rectangle frame;
	private AffineTransformation o;
	private int highlightedTransformationIndex;
	/**
	 * Le constructeur de ce composant prend en argument le bâtisseur de la fractale et le cadre. 
	 * @param flameBuilder
	 * @param frame
	 */
	public AffineTransformationsComponent(ObservableFlameBuilder flameBuilder, Rectangle frame) {
		this.flameBuilder = flameBuilder;
		this.frame = frame;
		highlightedTransformationIndex = 0;
	}
	
	public void setNewFlame(ObservableFlameBuilder flameBuilder, Rectangle frame, int index) {
		this.flameBuilder = flameBuilder;
		this.frame = frame;
		highlightedTransformationIndex = index;
		repaint();
	}
	
	/**
	 * retourne la dimension idéale pour le composant.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 150);
	}
	/**
	 * dessine le composant.
	 * met a jour le cadre et la transformation a utilise pour dessiner les points suivant la taille de la fenetre. 
	 */
	@Override
	public void paintComponent(Graphics g0) {
		Graphics2D g = (Graphics2D) g0;		
		Rectangle currentFrame = frame.expandToAspectRatio(getWidth()/(double) getHeight());
		// On met -y dans les deux transfos pour inverser directement les coordonnÈes par rapport au composant
		o = AffineTransformation.newScaling(getWidth()/currentFrame.width(), -getHeight()/currentFrame.height()).composeWith(AffineTransformation.newTranslation(-currentFrame.left(), currentFrame.bottom()));
		drawLines(g, currentFrame);
		drawTransformations(g);
	}
	/**
	 * dessine le repere dans lequel on affiche les marqueurs de transformations affines. 
	 * @param g
	 * @param currentFrame
	 */
	private void drawLines(Graphics2D g, Rectangle currentFrame) {		
		/*
		 * On dessine currentFrame.width() lignes verticales et currentFrame.height() lignes horizontales
		 * car dans le pire des cas, l'origine est confondue avec un coin de currentFrame.
		 */
		
		// On dessine les lignes verticales depuis l'origine, de chaque cote de celle-ci en meme temps.
		g.setColor(new Color(0.7f, 0.7f, 0.9f));
		for(int i=1; i<currentFrame.width(); i++) {
			g.draw(new Segment(new Point(i, currentFrame.bottom()), new Point(i, currentFrame.top())).transformation(o).getLine2D());
			g.draw(new Segment(new Point(-i, currentFrame.bottom()), new Point(-i, currentFrame.top())).transformation(o).getLine2D());
		}
		
		for(int i=1; i<currentFrame.height(); i++) {
			g.draw(new Segment(new Point(currentFrame.left(), i), new Point(currentFrame.right(), i)).transformation(o).getLine2D());
			g.draw(new Segment(new Point(currentFrame.left(), -i), new Point(currentFrame.right(), -i)).transformation(o).getLine2D());
		}
		
		// On dessine les axes principaux
		//g.setColor(new Color(0.4f, 0.2f, 0.4f));
		g.setColor(new Color(0.5f, 0f, 0.7f));
		g.draw(new Segment(new Point(currentFrame.left(), 0), new Point(currentFrame.right(), 0)).transformation(o).getLine2D());
		g.draw(new Segment(new Point(0, currentFrame.bottom()), new Point(0, currentFrame.top())).transformation(o).getLine2D());
	}
	/**
	 * dessine les marqueurs de transformations affines.
	 * @param g
	 */
	private void drawTransformations(Graphics2D g) {	
		AffineTransformation highlighted = null;
		// On dessine les transformations a l'exception de la transfo selectionnee.
		// Celle-ci est stockee dans highlighted qu'on dessine ensuite
		g.setColor(new Color(0f, 0.8f, 1f));
		for(int i=0; i<flameBuilder.transformationCount(); i++) {
			// On applique chaque transformation a la croix d'origine
			AffineTransformation at = flameBuilder.affineTransformation(i);
			// Si i == index de la transformation selectionnee, on stocke la transfo selectionnee dans highlighted
			if(i == highlightedTransformationIndex)
				highlighted = at;
			// Sinon, on affiche la croix
			else
				drawTransformationMarker(g, at);
		}
		// On dessine le marqueur selectionne (highlighted)
		g.setColor(new Color(1f, 0f, 1f));
		drawTransformationMarker(g, highlighted);
	}
	
	private void drawTransformationMarker(Graphics2D g, AffineTransformation at) {
		AffineTransformation transfo = o.composeWith(at);
		Segment[] flecheHor = new Segment[] {
			new Segment(new Point(-1, 0), new Point(1, 0)),
			new Segment(new Point(1, 0), new Point(0.9, 0.1)),
			new Segment(new Point(1, 0), new Point(0.9, -0.1))
		};
		
		for (Segment s : flecheHor) {
			g.draw(s.transformation(transfo).getLine2D());
			g.draw(s.transformation(transfo.composeWith(AffineTransformation.newRotation(Math.PI/2.0))).getLine2D());
		}
	}
	
	/**
	 * retourne l'index de la transformation a mettre en evidence <code>{@link highlightedTransofmrationIndex}</code>. 
	 * @return
	 */
	public int getHighlightedTransformationIndex() {
		return highlightedTransformationIndex;
	}
	
	/**
	 * change l'index de la transformation a mettre en evidence  <code>{@link highlightedTransformationIndex}</code> <br />
	 * repeint le composant . 
	 * @param i
	 */
	public void setHighlightedTransformationIndex(int i) {
		if(i < -1 || i > flameBuilder.transformationCount())
			throw new IllegalArgumentException("L'index n'est pas valide !");
		highlightedTransformationIndex = i;
		repaint();
	}
}
