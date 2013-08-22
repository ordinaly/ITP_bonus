package ch.epfl.flamemaker.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
/**
 * <p>La classe FlameBuilderPreviewComponent modŽlise le composant permettant d'afficher la fractale en cours d'Ždition.</p>
 *<p> Elle possŽde des champs privŽes necessaires au dessin de la fractale : flameBuilder, la couleur de fond, la palette, le cadre du dessin et la densite.</p> 
 *  @see ObservableFlameBuilder
 *  @see Color
 *  @see Palette
 *  @see Rectangle
 *  
 * @author cherifyasmine
 *
 */
@SuppressWarnings("serial")
public class FlameBuilderPreviewComponent extends JComponent {
	private ObservableFlameBuilder flameBuilder;
	private Color backgroundColor;
	private Palette palette;
	private Rectangle frame;
	private int density;
	/**
	 * Le constructeur initialise tout les attributs necessaires au dessin de la fractale. 
	 * @param flameBuilder
	 * @param backgroundColor
	 * @param palette
	 * @param frame
	 * @param density
	 */
	public FlameBuilderPreviewComponent(ObservableFlameBuilder flameBuilder, Color backgroundColor, Palette palette, Rectangle frame, int density) {
		this.flameBuilder = flameBuilder;
		this.backgroundColor = backgroundColor;
		this.palette = palette;
		this.frame = frame;
		this.density = density;
	}

	public void setNewFlame(ObservableFlameBuilder flameBuilder, Color backgroundColor, Palette palette, Rectangle frame, int density) {
		this.flameBuilder = flameBuilder;
		this.backgroundColor = backgroundColor;
		this.palette = palette;
		this.frame = frame;
		this.density = density;
		repaint();
	}
	
	/**
	 * retourne la dimension idŽale pour le composant.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 150);
	}
	/**
	 *  est appelŽe par Swing chaque fois que le composant doit �tre redessinŽ, p.ex. suite ˆ un redimensionnement.
	 *  Le dessin de la fractale se fait dans cette mŽthode, il faut produire une image qu'on affiche ensuite dans le composant.
	 *  
	 */
	@Override
	public void paintComponent(Graphics g0) {
		//notifyObservers(true);
		Graphics2D g = (Graphics2D) g0;
		BufferedImage fractal = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		double componentAspectRatio = (double) getWidth()/ (double) getHeight();
		FlameAccumulator acc = flameBuilder.build().compute(frame.expandToAspectRatio(componentAspectRatio), getWidth(), getHeight(), density);
		for(int i=0; i<acc.height(); i++) {
			for(int j=0; j<acc.width(); j++) {
				Color c = acc.color(palette, backgroundColor, j, i);
				fractal.setRGB(j, i, c.asPackedRGB());
			}
		}
		g.drawImage(fractal, 0, 0, null);
	}
}
