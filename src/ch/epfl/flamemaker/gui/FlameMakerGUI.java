package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.PresetFlame;
import ch.epfl.flamemaker.flame.StorableFlame;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.flame.PresetFlame.TransfoListObserver;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlameMakerGUI {
	private final Set<FlameListObserver> observers;
	
	private final AffineTransformationsComponent affineTransfoComponent;
	private final FlameBuilderPreviewComponent fractalPreviewComponent;
	private final JFormattedTextField[] variationsFields;
	private final JScrollPane scrollTransfoPanel;
	private final JScrollPane scrollFlamePanel; 

	// selectedFlameIndex observable
	private List<PresetFlame> flameList;
	private JList<String> listFlamesJList;
	private PresetFlame flame;
	private int selectedFlameIndex;
	
	// Densité d'image
	private final int DENSITY = 15;
	
	public FlameMakerGUI() {
		observers = new HashSet<FlameListObserver>();
		flameList = generatePresetFlames(true);
		setCurrentFlame(0);
		
		affineTransfoComponent = new AffineTransformationsComponent(flame.getObservableFlameBuilder(), flame.getFrame());
		fractalPreviewComponent = new FlameBuilderPreviewComponent(flame.getObservableFlameBuilder(), Color.BLACK, flame.getPalette(), flame.getFrame(), DENSITY);
		
		variationsFields = new JFormattedTextField[Variation.ALL_VARIATIONS.size()];
		scrollTransfoPanel = new JScrollPane(flame.getListTransfos());
		scrollFlamePanel = new JScrollPane();
	}

	public void addObserver(FlameListObserver o) {
		observers.add(o);
	}

	public void removeObserver(FlameListObserver o) {
		observers.remove(o);
	}

	public void notifyObservers() {
		for (FlameListObserver o : observers) {
			o.update();
		}
	}
	
	private void checkIndexFlame(int index) {
		if(index < 0 || index >= flameList.size())
			throw new IllegalArgumentException("La Flame selectionnee n'existe pas ! (Invalid index)");
	}
	
	private void addFlameObservers(PresetFlame f) {
		f.addObserver(new TransfoListObserver() {
			@Override
			public void update() {
				affineTransfoComponent.setHighlightedTransformationIndex(flame.getSelectedTransformationIndex());
				setVariationWeights();
			}
		});
		
		f.getObservableFlameBuilder().addObserver(new ObservableFlameBuilder.Observer() {
			@Override
			public void update() {
				affineTransfoComponent.repaint();
				fractalPreviewComponent.repaint();
			}
		});
	}
	
	private List<PresetFlame> readFlameList(String path) {
		File f = new File(path);
		if(f.isFile() && f.canRead()) {
			FileInputStream input = null;
			ObjectInputStream restore = null;
			List<PresetFlame> restoreFlameList = new ArrayList<PresetFlame>();
			try {
				input = new FileInputStream(f);
				restore = new ObjectInputStream(input);
				//System.out.println(input.available());
				if(input.available() == 0)
					throw new UnsupportedOperationException("Le fichier est vide !");
				while(input.available() > 0) {
					PresetFlame flame = ((StorableFlame) restore.readObject()).restoreFlame();
					restoreFlameList.add(flame);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally {
				try {
					restore.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return restoreFlameList;
		}
		// TODO POPUP pour indiquer que le fichier n'existe pas/n'est pas lisible
		return flameList;
	}
	
	private List<PresetFlame> openFlameList() {
		return readFlameList("flamelist.sav");
	}
	
	private void saveFlameList() {
		File file = new File("flamelist.sav");
		ObjectOutputStream save = null;
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			save = new ObjectOutputStream(output);
			for(PresetFlame flame : flameList) {
				StorableFlame sFlame = new StorableFlame(flame);
				save.writeObject(sFlame);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				save.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<PresetFlame> generatePresetFlames(boolean defaultList) {
		List<PresetFlame> definedFlames = new ArrayList<PresetFlame>();
		if(defaultList) {
			// Shark
			ArrayList<FlameTransformation> sharkTransfos = new ArrayList<FlameTransformation>();
			sharkTransfos.add(new FlameTransformation(new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8), new double[] { 1, 0.1, 0, 0, 0, 0 }));
			sharkTransfos.add(new FlameTransformation(new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), new double[] { 0, 0, 0, 0, 0.8, 1 }));
			sharkTransfos.add(new FlameTransformation(new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9), new double[] { 1, 0, 0, 0, 0, 0 }));
			PresetFlame shark = new PresetFlame("Shark", sharkTransfos, new Rectangle(new Point(-0.25, 0.0), 5, 4), InterpolatedPalette.generateRGBPal());
	
			// Turbulence
			ArrayList<FlameTransformation> turbulenceTransfos = new ArrayList<FlameTransformation>();
			turbulenceTransfos.add(new FlameTransformation(new AffineTransformation(0.712487, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7), new double[] { 0.5, 0, 0, 0.4, 0, 0 }));
			turbulenceTransfos.add(new FlameTransformation(new AffineTransformation(0.3731079, -0.6462417, 0.4, 0.6462414, 0.3731076, 0.3), new double[] { 1, 0, 0.1, 0, 0, 0 }));
			turbulenceTransfos.add(new FlameTransformation(new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3), new double[] { 1, 0, 0, 0, 0, 0 }));
			PresetFlame turbulence = new PresetFlame("Turbulence", turbulenceTransfos, new Rectangle(new Point(0.1, 0.1), 3, 3), InterpolatedPalette.generateRGBPal());
			
			definedFlames.add(shark);
			definedFlames.add(turbulence);
		} 
		else {
			definedFlames = openFlameList();
		}
		
		for(int i=0; i<definedFlames.size(); i++) {
			addFlameObservers(definedFlames.get(i));
		}
		
		return definedFlames;
	}
	
	private void setCurrentFlame(int index) {
		checkIndexFlame(index);
		selectedFlameIndex = index;
		flame = flameList.get(index);
		notifyObservers();
	}

	private void setVariationWeights() {
		for(int i=0; i<variationsFields.length; i++) {
			double variationWeight = flame.getObservableFlameBuilder().variationWeight(flame.getSelectedTransformationIndex(), Variation.ALL_VARIATIONS.get(i));
			variationsFields[i].setValue(variationWeight);
		}
	}

	public void composeSelectedTransformation(AffineTransformation at) {
		AffineTransformation a = flame.getObservableFlameBuilder().affineTransformation(flame.getSelectedTransformationIndex());
		flame.getObservableFlameBuilder().setAffineTransformation(flame.getSelectedTransformationIndex(), at.composeWith(a));
	}

	private void addModificationListener(final JButton button, final JFormattedTextField textField,	final TransformModifier modifier) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double value = ((Number) textField.getValue()).doubleValue();
				modifier.addEffect(value);
			}
		});
	}

	private interface TransformModifier {
		public void addEffect(double value);
	}
	
	public void start() {
		// Creation du GUI
		final JFrame mainFrame = new JFrame("Flame Maker GUI");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		mainFrame.getContentPane().setLayout(new BorderLayout());

		// Menu Panel
		JPanel menuPanel = generateMenuPanel();
				
		// Top Panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 2));

		// Transfo preview Panel
		JPanel transfoPreviewPanel = new JPanel();
		transfoPreviewPanel.setLayout(new BorderLayout());
		transfoPreviewPanel.add(affineTransfoComponent);

		// Fractal preview panel
		final JPanel fractalPreviewPanel = new JPanel();
		fractalPreviewPanel.setLayout(new BorderLayout());
		fractalPreviewPanel.add(fractalPreviewComponent);

		// Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

		// Flame List Panel
		JPanel flameListPanel = generateFlameListPanel(mainFrame);
		
		// Liste Transformations Panel
		JPanel transfoListPanel = generateListTransfoPanel();
		
		// Flame edit panel
		JPanel flameEditPanel = generateFlameEditPanel();
			
		// Imbriquer les elements
		mainFrame.getContentPane().add(menuPanel, BorderLayout.NORTH);
		mainFrame.getContentPane().add(topPanel, BorderLayout.CENTER);
		mainFrame.getContentPane().add(bottomPanel, BorderLayout.PAGE_END);
				
		topPanel.add(transfoPreviewPanel);
		topPanel.add(fractalPreviewPanel);

		bottomPanel.add(flameListPanel);
		bottomPanel.add(transfoListPanel);
		bottomPanel.add(flameEditPanel);

		// Bordures et labels
		Border transfoBorder = BorderFactory.createTitledBorder("Transformations affines");
		Border fractalBorder = BorderFactory.createTitledBorder("Fractale");

		// fractal et transfo imbrication
		transfoPreviewPanel.setBorder(transfoBorder);
		fractalPreviewPanel.setBorder(fractalBorder);

		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	private void setNewFlamesJList() {
		listFlamesJList = new JList<String>(new FlameListModel());
		listFlamesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listFlamesJList.setVisibleRowCount(5);
		listFlamesJList.setSelectedIndex(selectedFlameIndex);
		listFlamesJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				setCurrentFlame(listFlamesJList.getSelectedIndex());
			}
		});
		scrollFlamePanel.setViewportView(listFlamesJList);
	}
	
	private JPanel generateMenuPanel() {
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.LINE_AXIS));
		
		JLabel mainLabel = new JLabel("  Bienvenue dans Flame Maker !  ");
		
		JButton openButton = new JButton("Ouvrir");
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flameList = generatePresetFlames(false);
				setCurrentFlame(0);
				setNewFlamesJList();
			}
		});
		
		JButton saveButton = new JButton("Sauvegarder");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFlameList();
			}
		});
		
		JButton resetButton = new JButton("Par defaut");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flameList = generatePresetFlames(true);
				setCurrentFlame(0);
				setNewFlamesJList();
			}
		});
		
		menuPanel.add(mainLabel);
		menuPanel.add(openButton);
		menuPanel.add(saveButton);
		menuPanel.add(resetButton);
		
		return menuPanel;
	}
	
	private JPanel generateFlameListPanel(final JFrame mainFrame) {
		/*
		 * LISTE DES FLAMES PREDEFINIES
		 */
		// Flame List Panel
		final JPanel flameListPanel = new JPanel();
		flameListPanel.setLayout(new BoxLayout(flameListPanel,BoxLayout.PAGE_AXIS));

		// Boutons list Panel
		JPanel flameListButtonsPanel = new JPanel();
		flameListButtonsPanel.setLayout(new GridLayout(2, 2));
		
		setNewFlamesJList();
		
		// Ajout d'un observateur
		// Liste Flames Observer
		addObserver(new FlameListObserver() {
			@Override
			public void update() {
				fractalPreviewComponent.setNewFlame(flame.getObservableFlameBuilder(), Color.BLACK, flame.getPalette(), flame.getFrame(), 15);
				affineTransfoComponent.setNewFlame(flame.getObservableFlameBuilder(), flame.getFrame(), flame.getSelectedTransformationIndex());
				scrollTransfoPanel.setViewportView(flame.getListTransfos());
				setVariationWeights();
			}
		});

		// Boutons ajout/suppression
		final JButton newFlameButton = new JButton("Nouvelle");
		final JButton removeFlameButton = new JButton("Supprimer");
		final JButton copyFlameButton = new JButton("Copier");
		final JButton renameFlameButton = new JButton("Renommer");

		// ActionListeners
		newFlameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int flameSize = listFlamesJList.getModel().getSize();
				String name = "";
				JLabel dialogLabel = new JLabel(
						"Donnez un nom a votre fractale :\n");
				while (name != null && name.length() < 1) {
					name = (String) JOptionPane.showInputDialog(mainFrame,
							dialogLabel, "Nom de la Flame",
							JOptionPane.PLAIN_MESSAGE, null, null,
							"Nouvelle fractale");
					if ((name == null) || (name.length() < 1))
						dialogLabel.setText("Le champ doit contenir quelque chose !");
				}
				if ((name != null) && (name.length() > 0)) {
					((FlameListModel) listFlamesJList.getModel()).addFlame(PresetFlame.newEmptyFlame(name));
					listFlamesJList.setSelectedIndex(flameSize);
					if (flameSize == 1)
						removeFlameButton.setEnabled(true);
				}
			}
		});

		removeFlameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int flameSize = listFlamesJList.getModel().getSize();
				if (flameSize == 1)
					throw new UnsupportedOperationException("Impossible de supprimer la derniere Flame !");
				int removeIndex = listFlamesJList.getSelectedIndex();
				listFlamesJList.setSelectedIndex(removeIndex == flameSize - 1 ? removeIndex - 1 : removeIndex + 1);
				((FlameListModel) listFlamesJList.getModel()).removeFlame(removeIndex);
				if (flameSize == 2)
					removeFlameButton.setEnabled(false);
			}
		});

		copyFlameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int flameSize = listFlamesJList.getModel().getSize();
				int index = listFlamesJList.getSelectedIndex();
				PresetFlame f = flameList.get(index).clone();
				f.setName(f.getName() + " - Copie");
				((FlameListModel) listFlamesJList.getModel()).addFlame(f);
				listFlamesJList.setSelectedIndex(flameSize);
			}
		});

		renameFlameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = "";
				String currentName = (String) listFlamesJList.getSelectedValue();
				int index = listFlamesJList.getSelectedIndex();
				JLabel dialogLabel = new JLabel("Renommer la fractale :\n");
				while (name != null && name.length() < 1) {
					name = (String) JOptionPane.showInputDialog(mainFrame, dialogLabel, "Nom de la Flame", JOptionPane.PLAIN_MESSAGE, null, null, currentName);
					if ((name == null) || (name.length() < 1))
						dialogLabel.setText("Le champ doit contenir quelque chose !");
				}
				((FlameListModel) listFlamesJList.getModel()).setElement(index, name);
				listFlamesJList.setSelectedIndex(index);
			}
		});
		
		// flame list panel imbrication
		Border flameListBorder = BorderFactory.createTitledBorder("Liste des Flames");
		flameListPanel.setBorder(flameListBorder);
		flameListPanel.add(scrollFlamePanel);
		flameListPanel.add(flameListButtonsPanel);
		flameListButtonsPanel.add(newFlameButton);
		flameListButtonsPanel.add(removeFlameButton);
		flameListButtonsPanel.add(copyFlameButton);
		flameListButtonsPanel.add(renameFlameButton);
		
		return flameListPanel;
	}
	
	private JPanel generateListTransfoPanel() {
		JPanel transfoListPanel = new JPanel();
		transfoListPanel.setLayout(new BorderLayout());

		// Boutons Liste Transformations Panel
		JPanel transfoListButtonsPanel = new JPanel();
		transfoListButtonsPanel.setLayout(new GridLayout(1, 2));

		// Boutons ajout/suppression
		final JButton addTransfoButton = new JButton("Ajouter");
		final JButton removeTransfoButton = new JButton("Supprimer");

		// ActionListeners
		addTransfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int transfoSize = flame.getListTransfos().getModel().getSize();
				((PresetFlame.TransformationsListModel) flame.getListTransfos().getModel()).addTransformation();
				flame.getListTransfos().setSelectedIndex(transfoSize);
				if (transfoSize == 1)
					removeTransfoButton.setEnabled(true);
			}
		});

		removeTransfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int transfoSize = flame.getListTransfos().getModel().getSize();
				if (transfoSize == 1)
					throw new UnsupportedOperationException("Impossible de supprimer la derniere transformation !");
				int removeIndex = flame.getListTransfos().getSelectedIndex();
				flame.getListTransfos().setSelectedIndex(removeIndex == transfoSize - 1 ? removeIndex - 1 : removeIndex + 1);
				((PresetFlame.TransformationsListModel) flame.getListTransfos().getModel()).removeTransformation(removeIndex);
				if (transfoSize == 2)
					removeTransfoButton.setEnabled(false);
			}
		});
		
		// transfo list panel imbrication
		Border transfoListBorder = BorderFactory.createTitledBorder("Transformations");
		transfoListPanel.setBorder(transfoListBorder);
		transfoListPanel.add(scrollTransfoPanel, BorderLayout.CENTER);
		transfoListPanel.add(transfoListButtonsPanel, BorderLayout.PAGE_END);
		transfoListButtonsPanel.add(addTransfoButton);
		transfoListButtonsPanel.add(removeTransfoButton);
		
		return transfoListPanel;
	}
	
	private JPanel generateFlameEditPanel() {
		JPanel flameEditPanel = new JPanel();
		flameEditPanel.setLayout(new BoxLayout(flameEditPanel, BoxLayout.PAGE_AXIS));
		// flame edit panel imbrication
		Border flameEditBorder = BorderFactory.createTitledBorder("Transformation courante");
		flameEditPanel.setBorder(flameEditBorder);
		/*
		 * flame affine edit panel
		 */
		JPanel flameAffineEditPanel = new JPanel();
		GroupLayout flameAffineEditLayout = new GroupLayout(flameAffineEditPanel);
		flameAffineEditPanel.setLayout(flameAffineEditLayout);
		/*
		 * PLACEMENT DES ELEMENTS DE D'EDITION DE LA TRANSFORMATION FLAME COURANTE
		 */
		// GroupLayout pour affine edit panel
		flameAffineEditLayout.setAutoCreateGaps(true);
		flameAffineEditLayout.setAutoCreateContainerGaps(true);

		// Labels et boutons pour flame affine edit panel
		JLabel translationEdit = new JLabel("Translation");
		JLabel rotateEdit = new JLabel("Rotation");
		JLabel scalingEdit = new JLabel("Dilatation");
		JLabel shearEdit = new JLabel("Transvection");

		// Champs edition affine edit panel
		DecimalFormat textFieldsFormat = new DecimalFormat("#0.##");
		final JFormattedTextField translationValue = new JFormattedTextField(textFieldsFormat);
		translationValue.setHorizontalAlignment(JTextField.RIGHT);
		translationValue.setValue(0.1);
		final JFormattedTextField rotationValue = new JFormattedTextField(textFieldsFormat);
		rotationValue.setHorizontalAlignment(JTextField.RIGHT);
		rotationValue.setValue(15);
		final JFormattedTextField scalingValue = new JFormattedTextField(textFieldsFormat);
		scalingValue.setHorizontalAlignment(JTextField.RIGHT);
		scalingValue.setValue(1.05);
		scalingValue.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				JFormattedTextField current = (JFormattedTextField) input;
				JFormattedTextField.AbstractFormatter formatter = scalingValue.getFormatter();
				String stringValue = ((JFormattedTextField) input).getText();
				Number numericValue = null;
				try {
					numericValue = (Number) formatter.stringToValue(stringValue);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(numericValue.intValue() == 0) {
					current.setValue(current.getValue());
					return false;
				}
				current.setValue(numericValue);
				return true;
			}
		});
		
		/*
		 * CREATION DES BOUTONS D'EDITION DE LA TRANSFORMATION FLAME COURANTE
		 * Utilisation du patron Strategy
		 */
		final JFormattedTextField shearValue = new JFormattedTextField(textFieldsFormat);
		shearValue.setHorizontalAlignment(JTextField.RIGHT);
		shearValue.setValue(0.1);
		
		JButton translationLeftArrow = new JButton("\u2190");
		addModificationListener(translationLeftArrow, translationValue,	new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newTranslation(-value, 0));
					}
				});

		JButton translationRightArrow = new JButton("\u2192");
		addModificationListener(translationRightArrow, translationValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newTranslation(value, 0));
					}
				});

		JButton translationUpArrow = new JButton("\u2191");
		addModificationListener(translationUpArrow, translationValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newTranslation(0, value));
					}
				});

		JButton translationDownArrow = new JButton("\u2193");
		addModificationListener(translationDownArrow, translationValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newTranslation(0, -value));
					}
				});

		JButton rotateLeftArrow = new JButton("\u21BA");
		addModificationListener(rotateLeftArrow, rotationValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						AffineTransformation at = flame.getObservableFlameBuilder().affineTransformation(flame.getSelectedTransformationIndex()).composeWith(AffineTransformation.newRotation(value* Math.PI/(double)180));
						flame.getObservableFlameBuilder().setAffineTransformation(flame.getSelectedTransformationIndex(), at);
					}
				});

		JButton rotateRightArrow = new JButton("\u21BB");
		addModificationListener(rotateRightArrow, rotationValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						AffineTransformation at = flame.getObservableFlameBuilder().affineTransformation(flame.getSelectedTransformationIndex()).composeWith(AffineTransformation.newRotation(-value* Math.PI/(double)180));
						flame.getObservableFlameBuilder().setAffineTransformation(flame.getSelectedTransformationIndex(), at);
					}
				});

		JButton horPlusArrow = new JButton("+ \u2194");
		addModificationListener(horPlusArrow, scalingValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newScaling(value, 1));
					}
				});

		JButton horMinusArrow = new JButton("- \u2194");
		addModificationListener(horMinusArrow, scalingValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newScaling(1 / value, 1));
					}
				});

		JButton verPlusArrow = new JButton("+ \u2195");
		addModificationListener(verPlusArrow, scalingValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newScaling(1, value));
					}
				});

		JButton verMinusArrow = new JButton("- \u2195");
		addModificationListener(verMinusArrow, scalingValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newScaling(1, 1 / value));
					}
				});

		JButton shearLeftArrow = new JButton("\u2190");
		addModificationListener(shearLeftArrow, shearValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newShearX(-value));
					}
				});

		JButton shearRightArrow = new JButton("\u2192");
		addModificationListener(shearRightArrow, shearValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newShearX(value));
					}
				});

		JButton shearUpArrow = new JButton("\u2191");
		addModificationListener(shearUpArrow, shearValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newShearY(value));
					}
				});

		JButton shearDownArrow = new JButton("\u2193");
		addModificationListener(shearDownArrow, shearValue, new TransformModifier() {
					@Override
					public void addEffect(double value) {
						composeSelectedTransformation(AffineTransformation.newShearY(-value));
					}
				});

		
		flameAffineEditLayout.setHorizontalGroup(flameAffineEditLayout
			.createSequentialGroup()
				.addGroup(flameAffineEditLayout
					.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(translationEdit)
						.addComponent(rotateEdit)
						.addComponent(scalingEdit)
						.addComponent(shearEdit)
				)
				.addGroup(flameAffineEditLayout
					.createParallelGroup()
						.addComponent(translationValue)
						.addComponent(rotationValue)
						.addComponent(scalingValue)
						.addComponent(shearValue)
				)
				.addGroup(flameAffineEditLayout
					.createParallelGroup()
						.addComponent(translationLeftArrow)
						.addComponent(rotateLeftArrow)
						.addComponent(horPlusArrow)
						.addComponent(shearLeftArrow)
				)
				.addGroup(flameAffineEditLayout
					.createParallelGroup()
						.addComponent(translationRightArrow)
						.addComponent(rotateRightArrow)
						.addComponent(horMinusArrow)
						.addComponent(shearRightArrow)
				)
				.addGroup(flameAffineEditLayout
					.createParallelGroup()
						.addComponent(translationUpArrow)
						.addComponent(verPlusArrow)
						.addComponent(shearUpArrow))
				.addGroup(flameAffineEditLayout
					.createParallelGroup()
						.addComponent(translationDownArrow)
						.addComponent(verMinusArrow)
						.addComponent(shearDownArrow)
				)
		);

		flameAffineEditLayout.setVerticalGroup(flameAffineEditLayout
			.createSequentialGroup()
				.addGroup(flameAffineEditLayout
					.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(translationEdit)
						.addComponent(translationValue)
						.addComponent(translationLeftArrow)
						.addComponent(translationRightArrow)
						.addComponent(translationUpArrow)
						.addComponent(translationDownArrow)
				)
				.addGroup(flameAffineEditLayout
					.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rotateEdit)
						.addComponent(rotationValue)
						.addComponent(rotateLeftArrow)
						.addComponent(rotateRightArrow)
				)
				.addGroup(flameAffineEditLayout
					.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(scalingEdit)
						.addComponent(scalingValue)
						.addComponent(horPlusArrow)
						.addComponent(horMinusArrow)
						.addComponent(verPlusArrow)
						.addComponent(verMinusArrow)
				)
				.addGroup(flameAffineEditLayout
					.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(shearEdit)
						.addComponent(shearValue)
						.addComponent(shearLeftArrow)
						.addComponent(shearRightArrow)
						.addComponent(shearUpArrow)
						.addComponent(shearDownArrow)
				)
		);
		
		/*
		 * CREATION DES CHAMPS DE VARIATION DES POIDS
		 * les labels sont stockes dans un tableau, les textfields sont stockes dans un tableau en attribut
		 */
		JPanel flameAffineVariationPanel = new JPanel();
		GroupLayout flameAffineVariationLayout = new GroupLayout(flameAffineVariationPanel);
		flameAffineVariationPanel.setLayout(flameAffineVariationLayout);
		List<Variation> variations = Variation.ALL_VARIATIONS;
		JLabel[] variationsLabels = new JLabel[variations.size()];
		for(int i=0; i<variations.size(); i++) {
			variationsLabels[i] = new JLabel(variations.get(i).getName());
			final JFormattedTextField current = new JFormattedTextField(textFieldsFormat);
			current.setHorizontalAlignment(JTextField.RIGHT);
			double value = flame.getObservableFlameBuilder().variationWeight(flame.getSelectedTransformationIndex(), variations.get(i));
			current.setValue(value);
			
			final Variation v = Variation.ALL_VARIATIONS.get(i);
			current.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					double newValue = ((Number) current.getValue()).doubleValue();
					flame.getObservableFlameBuilder().setVariationWeight(flame.getSelectedTransformationIndex(), v, newValue);
				}
			});
			variationsFields[i] = current;
		}
		
		/*
		 * PLACEMENT DES ELEMENTS DE VARIATION DES POIDS DANS UN GROUPE
		 */
		flameAffineVariationLayout.setAutoCreateGaps(true);
		flameAffineVariationLayout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup horizontalWeightGroup = flameAffineVariationLayout.createSequentialGroup();
		for(int i=0; i<3; i++) {
			GroupLayout.ParallelGroup horizontalSub1 = flameAffineVariationLayout.createParallelGroup(GroupLayout.Alignment.TRAILING);
			GroupLayout.ParallelGroup horizontalSub2 = flameAffineVariationLayout.createParallelGroup(GroupLayout.Alignment.TRAILING);
			for(int j=i; j<i+4; j+=3) {
				horizontalSub1 = horizontalSub1.addComponent(variationsLabels[j]);
				horizontalSub2 = horizontalSub2.addComponent(variationsFields[j]);
			}
			horizontalWeightGroup = horizontalWeightGroup.addGroup(horizontalSub1);
			horizontalWeightGroup = horizontalWeightGroup.addGroup(horizontalSub2);
		}
		horizontalWeightGroup = horizontalWeightGroup.addPreferredGap(ComponentPlacement.UNRELATED);
		flameAffineVariationLayout.setHorizontalGroup(horizontalWeightGroup);
		
		GroupLayout.SequentialGroup verticalWeightGroup = flameAffineVariationLayout.createSequentialGroup();
		for(int i=0; i<2; i++) {
			GroupLayout.ParallelGroup verticalSub = flameAffineVariationLayout.createParallelGroup(GroupLayout.Alignment.BASELINE);
			for(int j=3*i; j<3*i+3; j++) {
				verticalSub = verticalSub.addComponent(variationsLabels[j]);
				verticalSub = verticalSub.addComponent(variationsFields[j]);
			}
			verticalWeightGroup = verticalWeightGroup.addGroup(verticalSub);
		}
		verticalWeightGroup = verticalWeightGroup.addPreferredGap(ComponentPlacement.UNRELATED);
		flameAffineVariationLayout.setVerticalGroup(verticalWeightGroup);
		
		/*
		 * flame affine variation panel
		 */
		
		flameEditPanel.setBorder(flameEditBorder);
		flameEditPanel.add(flameAffineEditPanel);
		flameEditPanel.add(new JSeparator());
		flameEditPanel.add(flameAffineVariationPanel);
		return flameEditPanel;
	}
	
	@SuppressWarnings("serial")
	private class FlameListModel extends AbstractListModel<String> {

		public void addFlame(PresetFlame f) {
			addFlameObservers(f);
			flameList.add(f);
			setCurrentFlame(getSize() - 1);
			fireIntervalAdded(this, getSize(), getSize());
		}

		public void removeFlame(int index) {
			checkIndexFlame(index);
			flameList.remove(index);
			fireIntervalRemoved(this, index, index);
		}

		public void setElement(int index, String newName) {
			checkIndexFlame(index);
			PresetFlame f = flameList.get(index);
			f.setName(newName);
			fireIntervalAdded(this, getSize(), getSize());
		}

		@Override
		public String getElementAt(int index) {
			checkIndexFlame(index);
			return flameList.get(index).getName();
		}

		@Override
		public int getSize() {
			return flameList.size();
		}
	}

	private interface FlameListObserver {
		public void update();
	}
}
