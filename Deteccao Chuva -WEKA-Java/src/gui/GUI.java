package gui;

import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.border.BevelBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerNumberModel;
import util.PreparadorConjuntosTreinemento;
import util.TipoEntrada;
import classificador.Bayes;
import classificador.MLP;
import classificador.SVM;
import javax.swing.JRadioButton;

public class GUI {

	private JFrame frmDeteccaoChuva;
	private JTextField textFieldArquivoARFFTreino;
	private JTextField textFieldDiretorioSaida;
	private JTextField textFieldArquivoARFFTeste;
	private JTextField textFieldMLPLearnigRate;
	private JTextField textFieldMLPMomentum;
	private JTextField textFieldMLPHiddenLayers;
	private JTextField textFieldMLPValidacao;
	private JTextField textFieldMLPLimiteValidacao;
	private JTextField textFieldMLPMaximoEpocas;
	private JTextField textFieldSVMCost;
	private JTextField textFieldSVMEpsilon;
	private JTextField textFieldSVMGamma;
	private JTextField textFieldSVMDegree;
	private JTextField textFieldSVMCoef0;
	private JCheckBox chckbxUsarApenasConjuntoTreinamento;
	private JSpinner spinnerPercentualTeste;
	private JCheckBox chckbxBayesEstimarDistribuicao;
	private JCheckBox chckbxValidacaoCruzada;
	private JSpinner spinnerRuns;
	private JSpinner spinnerFolds;
	private JComboBox<String> comboBoxSVMKernel;
	private JCheckBox chckbxMLPDecay;
	private JCheckBox checkBoxMLPNormalizar;
	private JCheckBox checkBoxSVMNormalizar;
	private JRadioButton rdbtnApenasNoite;
	private JRadioButton rdbtnApenasDia;
	private JRadioButton rdbtnTudo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmDeteccaoChuva.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void botaoArquivoTreinamentoARFF() {
		final JFileChooser fc = new JFileChooser(
				"E://Dropbox//Mestrado Leandro//Journal IARA 2013//Resultados");

		fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return (f.getName().endsWith(".arff")) || f.isDirectory();
			}

			public String getDescription() {
				return "*.arff";
			}
		});
		int result = fc.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			textFieldArquivoARFFTreino.setText(fc.getSelectedFile()
					.getAbsolutePath());
		}

	}

	private void botaoDiretorioSaida() {
		final JFileChooser fc = new JFileChooser(
				"E://Dropbox//Mestrado Leandro//Journal IARA 2013//Resultados");

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			textFieldDiretorioSaida.setText(fc.getSelectedFile()
					.getAbsolutePath());
		}

	}

	private void botaoArquivoTesteARFF() {
		final JFileChooser fc = new JFileChooser(
				"E://Dropbox//Mestrado Leandro//Journal IARA 2013//Resultados");

		fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return (f.getName().endsWith(".arff")) || f.isDirectory();
			}

			public String getDescription() {
				return "*.arff";
			}
		});
		int result = fc.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			textFieldArquivoARFFTeste.setText(fc.getSelectedFile()
					.getAbsolutePath());
		}
	}

	private TipoEntrada verificarTipoEntrada() {
		if(this.rdbtnTudo.isSelected() == true)
		{
			return TipoEntrada.CONJUNTO_COMPLETO;
		}
		
		if(this.rdbtnApenasDia.isSelected() == true)
		{
			return TipoEntrada.APENAS_DIA;
		}
		
		if(this.rdbtnApenasNoite.isSelected() == true)
		{
			return TipoEntrada.APENAS_NOITE;
		}
		
		return null;
	}
	
	void executarClassificadorBayes() {
		PreparadorConjuntosTreinemento preparador;

		preparador = lerArquivosARFF();
		TipoEntrada tipoEntrada = verificarTipoEntrada();
		if (preparador != null) {
			Bayes classificador = new Bayes(
					this.chckbxBayesEstimarDistribuicao.isSelected(),
					preparador.getConjuntoTreinamento(),
					preparador.getConjuntoTeste(), tipoEntrada);
			try {
				classificador
						.Executar(this.chckbxValidacaoCruzada.isSelected(),
								Integer.parseInt(this.spinnerFolds.getValue()
										.toString()), Integer
										.parseInt(this.spinnerRuns.getValue()
												.toString()), preparador
										.getPathSaida());
				JOptionPane.showMessageDialog(null, "OK");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Erro na execução do classificador");
				e.printStackTrace();
			}
		}
	}

	void executarClassificadorMultilayerPerceptron() {
		PreparadorConjuntosTreinemento preparador;
		float learningRate;
		float momentum;
		String hiddenLayers;
		int validationSetSize;
		int validationThreshold;
		int trainingTime;
		boolean decay;
		boolean normalize;

		preparador = lerArquivosARFF();
		TipoEntrada tipoEntrada = verificarTipoEntrada();
		if (preparador != null) {
			learningRate = (float) Double
					.parseDouble(this.textFieldMLPLearnigRate.getText());
			momentum = (float) Double.parseDouble(this.textFieldMLPMomentum
					.getText());
			hiddenLayers = this.textFieldMLPHiddenLayers.getText();
			validationSetSize = Integer.parseInt(this.textFieldMLPValidacao
					.getText());
			validationThreshold = Integer
					.parseInt(this.textFieldMLPLimiteValidacao.getText());
			trainingTime = Integer.parseInt(this.textFieldMLPMaximoEpocas
					.getText());
			decay = this.chckbxMLPDecay.isSelected();
			normalize = this.checkBoxMLPNormalizar.isSelected();
			
			MLP classificador = new MLP(learningRate, momentum, hiddenLayers,
					validationSetSize, validationThreshold, trainingTime,
					decay, normalize, preparador.getConjuntoTreinamento(),
					preparador.getConjuntoTeste(), tipoEntrada);
			try {
				classificador
						.Executar(this.chckbxValidacaoCruzada.isSelected(),
								Integer.parseInt(this.spinnerFolds.getValue()
										.toString()), Integer
										.parseInt(this.spinnerRuns.getValue()
												.toString()), preparador
										.getPathSaida());
				JOptionPane.showMessageDialog(null, "OK");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Erro na execução do classificador");
				e.printStackTrace();
			}
		}

	}

	void executarClassificadorSVM() {
		PreparadorConjuntosTreinemento preparador;
		double cost;
		double epsilon;
		boolean normalize;
		int kernelFunction;
		double gamma;
		int degree;
		double coef0;

		preparador = lerArquivosARFF();
		TipoEntrada tipoEntrada = verificarTipoEntrada();
		if (preparador != null) {
			cost = Double.parseDouble(this.textFieldSVMCost.getText());
			epsilon = Double.parseDouble(this.textFieldSVMEpsilon.getText());
			normalize = this.checkBoxSVMNormalizar.isSelected();
			kernelFunction = this.comboBoxSVMKernel.getSelectedIndex();
			gamma = Double.parseDouble(this.textFieldSVMGamma.getText());
			degree = Integer.parseInt(this.textFieldSVMDegree.getText());
			coef0 = Double.parseDouble(this.textFieldSVMCoef0.getText());

			SVM classificador = new SVM(cost, epsilon, normalize,
					kernelFunction, gamma, degree, coef0,
					preparador.getConjuntoTreinamento(),
					preparador.getConjuntoTeste(), tipoEntrada);
			try {
				classificador
						.Executar(this.chckbxValidacaoCruzada.isSelected(),
								Integer.parseInt(this.spinnerFolds.getValue()
										.toString()), Integer
										.parseInt(this.spinnerRuns.getValue()
												.toString()), preparador
										.getPathSaida());
				JOptionPane.showMessageDialog(null, "OK");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Erro na execução do classificador");
				e.printStackTrace();
			}
		}
	}

	PreparadorConjuntosTreinemento lerArquivosARFF() {
		PreparadorConjuntosTreinemento preparador = null;
		String pathARFFTreino, pathARFFTeste, pathDiretorioSaida;
		
		pathARFFTreino = this.textFieldArquivoARFFTreino.getText();
		if (pathARFFTreino.length() == 0) {
			JOptionPane.showMessageDialog(null,
					"Selecione o arquivo para treinar o modelo");
			return null;
		}
		
		pathDiretorioSaida = this.textFieldDiretorioSaida.getText();
		if (pathDiretorioSaida.length() == 0) {
			JOptionPane.showMessageDialog(null,
					"Selecione o diretorio de saida");
			return null;
		}
		
		if (this.chckbxUsarApenasConjuntoTreinamento.isSelected() == true) {
			try {
				preparador = new PreparadorConjuntosTreinemento(pathARFFTreino,
						Integer.parseInt(this.spinnerPercentualTeste.getValue()
								.toString()), 0);
				preparador.setPathSaida(pathDiretorioSaida);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Problema na abertura do arquivo!");
				e.printStackTrace();
			}
		} else {
			pathARFFTeste = this.textFieldArquivoARFFTeste.getText();
			if (pathARFFTeste.length() == 0) {
				JOptionPane.showMessageDialog(null,
						"Selecione o arquivo para testar o modelo");
				return null;
			}

			try {
				preparador = new PreparadorConjuntosTreinemento(pathARFFTreino,
						pathARFFTeste);
				preparador.setPathSaida(pathDiretorioSaida);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"Problema na abertura do arquivo!");
				e.printStackTrace();
				return null;
			}

		}

		return preparador;
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDeteccaoChuva = new JFrame();
		frmDeteccaoChuva.setTitle("Detec\u00E7\u00E3o Chuva");
		frmDeteccaoChuva.setBounds(100, 100, 708, 718);
		frmDeteccaoChuva.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDeteccaoChuva.getContentPane().setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		panel_1.setBounds(0, 0, 685, 673);
		frmDeteccaoChuva.getContentPane().add(panel_1);

		JPanel panel = new JPanel();
		panel.setBounds(12, 269, 322, 123);
		panel_1.add(panel);
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Classificador Baysiano");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setBounds(56, 13, 209, 25);
		panel.add(lblNewLabel);

		JButton btnExecutarBayes = new JButton("Executar");
		btnExecutarBayes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executarClassificadorBayes();
			}
		});
		btnExecutarBayes.setBounds(112, 81, 97, 25);
		panel.add(btnExecutarBayes);

		this.chckbxBayesEstimarDistribuicao = new JCheckBox(
				"Use Kernel Estimator");
		chckbxBayesEstimarDistribuicao.setBounds(87, 47, 147, 25);
		panel.add(chckbxBayesEstimarDistribuicao);

		textFieldArquivoARFFTreino = new JTextField();
		textFieldArquivoARFFTreino.setBounds(206, 53, 367, 22);
		panel_1.add(textFieldArquivoARFFTreino);
		textFieldArquivoARFFTreino.setColumns(10);

		JLabel lblArquivoCsv = new JLabel("Arquivo ARFF Treinamento:");
		lblArquivoCsv.setBounds(12, 54, 180, 21);
		panel_1.add(lblArquivoCsv);

		JLabel lblDiretrioDeSada = new JLabel("Diret\u00F3rio de Sa\u00EDda:");
		lblDiretrioDeSada.setBounds(12, 110, 125, 21);
		panel_1.add(lblDiretrioDeSada);

		textFieldDiretorioSaida = new JTextField();
		textFieldDiretorioSaida.setColumns(10);
		textFieldDiretorioSaida.setBounds(206, 109, 367, 22);
		panel_1.add(textFieldDiretorioSaida);

		JButton btnBrowseArquivoCSV = new JButton("Browse");
		btnBrowseArquivoCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				botaoArquivoTreinamentoARFF();
			}
		});
		btnBrowseArquivoCSV.setBounds(579, 52, 97, 25);
		panel_1.add(btnBrowseArquivoCSV);

		JButton btnBrowseDiretorioSaida = new JButton("Browse");
		btnBrowseDiretorioSaida.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				botaoDiretorioSaida();
			}
		});
		btnBrowseDiretorioSaida.setBounds(579, 108, 97, 25);
		panel_1.add(btnBrowseDiretorioSaida);

		JLabel lblDetecoDeChuva = new JLabel("Detec\u00E7\u00E3o de Chuva");
		lblDetecoDeChuva.setBounds(255, 13, 174, 22);
		panel_1.add(lblDetecoDeChuva);
		lblDetecoDeChuva.setFont(new Font("Tahoma", Font.BOLD, 18));

		JLabel lblArquivoArffTeste = new JLabel("Arquivo ARFF Teste:");
		lblArquivoArffTeste.setBounds(12, 84, 159, 16);
		panel_1.add(lblArquivoArffTeste);

		textFieldArquivoARFFTeste = new JTextField();
		textFieldArquivoARFFTeste.setColumns(10);
		textFieldArquivoARFFTeste.setBounds(206, 81, 367, 22);
		panel_1.add(textFieldArquivoARFFTeste);

		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				botaoArquivoTesteARFF();
			}
		});
		button.setBounds(579, 80, 97, 25);
		panel_1.add(button);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.GRAY,
				null, null, null));
		panel_2.setBounds(12, 187, 322, 69);
		panel_1.add(panel_2);
		panel_2.setLayout(null);

		this.chckbxUsarApenasConjuntoTreinamento = new JCheckBox(
				"Usar Apenas Conjunto de Treinamento");
		chckbxUsarApenasConjuntoTreinamento.setBounds(35, 7, 251, 25);
		panel_2.add(chckbxUsarApenasConjuntoTreinamento);

		JLabel lblTeste = new JLabel("% Teste:");
		lblTeste.setBounds(81, 41, 53, 16);
		panel_2.add(lblTeste);

		this.spinnerPercentualTeste = new JSpinner();
		spinnerPercentualTeste.setBounds(137, 38, 56, 22);
		spinnerPercentualTeste.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		panel_2.add(spinnerPercentualTeste);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.GRAY,
				null, null, null));
		panel_3.setBounds(339, 186, 337, 69);
		panel_1.add(panel_3);
		panel_3.setLayout(null);

		this.chckbxValidacaoCruzada = new JCheckBox(
				"Usar Valida\u00E7\u00E3o Cruzada");
		chckbxValidacaoCruzada.setBounds(86, 7, 165, 25);
		panel_3.add(chckbxValidacaoCruzada);

		JLabel lblRuns = new JLabel("Runs:");
		lblRuns.setBounds(184, 38, 33, 16);
		panel_3.add(lblRuns);

		this.spinnerRuns = new JSpinner();
		spinnerRuns.setBounds(248, 35, 56, 22);
		spinnerRuns.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		panel_3.add(spinnerRuns);

		JLabel lblNewLabel_1 = new JLabel("Folds:");
		lblNewLabel_1.setBounds(31, 38, 35, 16);
		panel_3.add(lblNewLabel_1);

		this.spinnerFolds = new JSpinner();
		spinnerFolds.setBounds(97, 34, 56, 22);
		spinnerFolds.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		panel_3.add(spinnerFolds);

		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		panel_4.setBounds(12, 397, 322, 263);
		panel_1.add(panel_4);

		JLabel lblRedeNeuralMlp = new JLabel("Rede Neural MLP");
		lblRedeNeuralMlp.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblRedeNeuralMlp.setBounds(83, 13, 155, 22);
		panel_4.add(lblRedeNeuralMlp);

		JButton btnExecutarMLP = new JButton("Executar");
		btnExecutarMLP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				executarClassificadorMultilayerPerceptron();
			}
		});
		btnExecutarMLP.setBounds(112, 229, 97, 25);
		panel_4.add(btnExecutarMLP);

		this.chckbxMLPDecay = new JCheckBox("Decay");
		chckbxMLPDecay.setBounds(223, 39, 69, 29);
		panel_4.add(chckbxMLPDecay);

		JLabel lblLearnigRate = new JLabel("Learnig Rate:");
		lblLearnigRate.setBounds(12, 43, 86, 21);
		panel_4.add(lblLearnigRate);

		textFieldMLPLearnigRate = new JTextField();
		textFieldMLPLearnigRate.setText("0.3");
		textFieldMLPLearnigRate.setBounds(146, 39, 59, 27);
		panel_4.add(textFieldMLPLearnigRate);
		textFieldMLPLearnigRate.setColumns(10);

		JLabel lblMomentum = new JLabel("Momentum:");
		lblMomentum.setBounds(12, 72, 80, 21);
		panel_4.add(lblMomentum);

		textFieldMLPMomentum = new JTextField();
		textFieldMLPMomentum.setText("0.2");
		textFieldMLPMomentum.setColumns(10);
		textFieldMLPMomentum.setBounds(146, 68, 59, 27);
		panel_4.add(textFieldMLPMomentum);

		JLabel lblHiddenLayer = new JLabel("Hidden Layers:");
		lblHiddenLayer.setBounds(12, 101, 91, 21);
		panel_4.add(lblHiddenLayer);

		textFieldMLPHiddenLayers = new JTextField();
		textFieldMLPHiddenLayers.setText("a");
		textFieldMLPHiddenLayers.setColumns(10);
		textFieldMLPHiddenLayers.setBounds(146, 97, 59, 27);
		panel_4.add(textFieldMLPHiddenLayers);

		JLabel lblValidao = new JLabel("Validation set size (%):");
		lblValidao.setBounds(12, 130, 134, 16);
		panel_4.add(lblValidao);

		textFieldMLPValidacao = new JTextField();
		textFieldMLPValidacao.setText("25");
		textFieldMLPValidacao.setColumns(10);
		textFieldMLPValidacao.setBounds(146, 126, 59, 27);
		panel_4.add(textFieldMLPValidacao);

		this.checkBoxMLPNormalizar = new JCheckBox("Normalize");
		checkBoxMLPNormalizar.setBounds(223, 70, 91, 25);
		panel_4.add(checkBoxMLPNormalizar);

		JLabel lblLimiteValidao = new JLabel("Validation Threshold:");
		lblLimiteValidao.setBounds(12, 159, 122, 16);
		panel_4.add(lblLimiteValidao);

		textFieldMLPLimiteValidacao = new JTextField();
		textFieldMLPLimiteValidacao.setText("20");
		textFieldMLPLimiteValidacao.setColumns(10);
		textFieldMLPLimiteValidacao.setBounds(146, 155, 59, 27);
		panel_4.add(textFieldMLPLimiteValidacao);

		JLabel lblMximopocas = new JLabel("Training time:");
		lblMximopocas.setBounds(12, 188, 108, 21);
		panel_4.add(lblMximopocas);

		textFieldMLPMaximoEpocas = new JTextField();
		textFieldMLPMaximoEpocas.setText("500");
		textFieldMLPMaximoEpocas.setColumns(10);
		textFieldMLPMaximoEpocas.setBounds(146, 184, 59, 27);
		panel_4.add(textFieldMLPMaximoEpocas);

		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		panel_5.setBounds(339, 269, 337, 391);
		panel_1.add(panel_5);

		JLabel lblSvm = new JLabel("SVM");
		lblSvm.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblSvm.setBounds(149, 13, 39, 22);
		panel_5.add(lblSvm);

		JButton btnExecutarSVM = new JButton("Executar");
		btnExecutarSVM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				executarClassificadorSVM();
			}
		});
		btnExecutarSVM.setBounds(120, 353, 97, 25);
		panel_5.add(btnExecutarSVM);

		JLabel lblCost = new JLabel("Cost:");
		lblCost.setBounds(27, 44, 32, 21);
		panel_5.add(lblCost);

		textFieldSVMCost = new JTextField();
		textFieldSVMCost.setText("1.0");
		textFieldSVMCost.setColumns(10);
		textFieldSVMCost.setBounds(84, 44, 59, 27);
		panel_5.add(textFieldSVMCost);

		this.checkBoxSVMNormalizar = new JCheckBox("Normalize");
		checkBoxSVMNormalizar.setBounds(117, 78, 103, 29);
		panel_5.add(checkBoxSVMNormalizar);

		JLabel lblEpsilon = new JLabel("Epsilon:");
		lblEpsilon.setBounds(157, 44, 51, 21);
		panel_5.add(lblEpsilon);

		textFieldSVMEpsilon = new JTextField();
		textFieldSVMEpsilon.setText("0.001");
		textFieldSVMEpsilon.setColumns(10);
		textFieldSVMEpsilon.setBounds(229, 44, 59, 27);
		panel_5.add(textFieldSVMEpsilon);

		JLabel lblFunoKernel = new JLabel("Kernel:");
		lblFunoKernel.setBounds(9, 103, 96, 21);
		panel_5.add(lblFunoKernel);

		this.comboBoxSVMKernel = new JComboBox<String>();
		comboBoxSVMKernel.setModel(new DefaultComboBoxModel<String>(
				new String[] { "polynomial: (gamma*u'*v + coef0)^degree",
						"linear: u'*v",
						"radial basis function: exp(-gamma*|u-v|^2)",
						"sigmoid: tanh(gamma*u'*v + coef0)" }));
		comboBoxSVMKernel.setBounds(9, 132, 317, 27);
		panel_5.add(comboBoxSVMKernel);

		JLabel lblGamma = new JLabel("Gamma:");
		lblGamma.setBounds(9, 167, 55, 21);
		panel_5.add(lblGamma);

		textFieldSVMGamma = new JTextField();
		textFieldSVMGamma.setText("0");
		textFieldSVMGamma.setColumns(10);
		textFieldSVMGamma.setBounds(80, 167, 59, 27);
		panel_5.add(textFieldSVMGamma);

		JLabel lblDegree = new JLabel("Degree:");
		lblDegree.setBounds(9, 196, 52, 21);
		panel_5.add(lblDegree);

		textFieldSVMDegree = new JTextField();
		textFieldSVMDegree.setText("3");
		textFieldSVMDegree.setColumns(10);
		textFieldSVMDegree.setBounds(80, 196, 59, 27);
		panel_5.add(textFieldSVMDegree);

		JLabel lblCoef = new JLabel("coef0:");
		lblCoef.setBounds(9, 225, 40, 21);
		panel_5.add(lblCoef);

		textFieldSVMCoef0 = new JTextField();
		textFieldSVMCoef0.setText("0");
		textFieldSVMCoef0.setColumns(10);
		textFieldSVMCoef0.setBounds(80, 225, 59, 27);
		panel_5.add(textFieldSVMCoef0);
		
		JPanel panel_6 = new JPanel();
		panel_6.setLayout(null);
		panel_6.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.GRAY,
						null, null, null));
		panel_6.setBounds(12, 140, 664, 42);
		panel_1.add(panel_6);
		
		rdbtnTudo = new JRadioButton("Tudo");
		rdbtnTudo.setSelected(true);
		rdbtnTudo.setBounds(8, 9, 62, 25);
		panel_6.add(rdbtnTudo);
		
		rdbtnApenasDia = new JRadioButton("Apenas Dia");
		rdbtnApenasDia.setBounds(90, 9, 93, 25);
		panel_6.add(rdbtnApenasDia);
		
		rdbtnApenasNoite = new JRadioButton("Apenas Noite");
		rdbtnApenasNoite.setBounds(204, 9, 113, 25);
		panel_6.add(rdbtnApenasNoite);
		
		ButtonGroup bg= new ButtonGroup(); 
		bg.add(rdbtnApenasNoite);
		bg.add(rdbtnApenasDia);
		bg.add(rdbtnTudo);
	}
}
