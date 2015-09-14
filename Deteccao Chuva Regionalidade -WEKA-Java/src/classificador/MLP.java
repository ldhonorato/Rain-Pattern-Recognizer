package classificador;

import java.text.DecimalFormat;
import util.OrganizadorResultados;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class MLP {
	private float learningRate;
	private float momentum;
	private String hiddenLayers;
	private int validationSetSize;
	private int validationThreshold;
	private int trainingTime;
	private boolean decay;
	private boolean normalize;
	private Instances conjuntoTreinamento;
	private Instances conjuntoTestes;
	private Evaluation evalTreinamento;
	private Evaluation evalTeste;
	private MultilayerPerceptron classificadorMLP;

	private double[] array_TP_rain;
	private double[] array_TP_NO_rain;
	private final int QUANTIDADE_EXECUCOES = 20;
	private double[] array_TP_rain_Treinamento;
	private double[] array_TP_NO_rain_Treinamento;

	public MLP(float learningRate, float momentum, String hiddenLayers,
			int validationSetSize, int validationThreshold, int trainingTime,
			boolean decay, boolean normalize, Instances conjuntoTreinamento,
			Instances conjuntoTestes) {
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.hiddenLayers = hiddenLayers;
		this.validationSetSize = validationSetSize;
		this.validationThreshold = validationThreshold;
		this.trainingTime = trainingTime;
		this.decay = decay;
		this.normalize = normalize;
		this.conjuntoTreinamento = conjuntoTreinamento;
		this.conjuntoTestes = conjuntoTestes;
		array_TP_rain = new double[QUANTIDADE_EXECUCOES];
		array_TP_NO_rain = new double[QUANTIDADE_EXECUCOES];
		array_TP_rain_Treinamento = new double[QUANTIDADE_EXECUCOES];
		array_TP_NO_rain_Treinamento = new double[QUANTIDADE_EXECUCOES];
	}

	public void Executar(String pathSaida, String prefixoArquivoResultado)
			throws Exception {
		Remove filtroTreino = new Remove();
		filtroTreino.setAttributeIndices("1,2,3,4,5,6,7");
		filtroTreino.setInvertSelection(false);
		filtroTreino.setInputFormat(conjuntoTreinamento);
		Instances novoConjuntoTreinamento = Filter.useFilter(
				conjuntoTreinamento, filtroTreino);

		Remove filtroTeste = new Remove();
		filtroTeste.setAttributeIndices("1,2,3,4,5,6,7");
		filtroTeste.setInvertSelection(false);
		filtroTeste.setInputFormat(conjuntoTestes);
		Instances novoConjuntoTeste = Filter.useFilter(conjuntoTestes,
				filtroTeste);

		classificadorMLP = new MultilayerPerceptron();
		classificadorMLP.setSeed((int) (Math.random() * 100));
		classificadorMLP.setLearningRate(learningRate);
		classificadorMLP.setMomentum(momentum);
		classificadorMLP.setHiddenLayers(hiddenLayers);
		classificadorMLP.setValidationSetSize(validationSetSize);
		classificadorMLP.setValidationThreshold(validationThreshold);
		classificadorMLP.setTrainingTime(trainingTime);
		classificadorMLP.setDecay(decay);
		classificadorMLP.setNormalizeAttributes(normalize);
		// Botar os normalizadores

		for (int i = 0; i < QUANTIDADE_EXECUCOES; i++) {
			classificadorMLP = new MultilayerPerceptron();
			classificadorMLP.setSeed((int) (Math.random() * 100));
			classificadorMLP.setLearningRate(learningRate);
			classificadorMLP.setMomentum(momentum);
			classificadorMLP.setHiddenLayers(hiddenLayers);
			classificadorMLP.setValidationSetSize(validationSetSize);
			classificadorMLP.setValidationThreshold(validationThreshold);
			classificadorMLP.setTrainingTime(trainingTime);
			classificadorMLP.setDecay(decay);
			classificadorMLP.setNormalizeAttributes(normalize);
			classificadorMLP.buildClassifier(novoConjuntoTreinamento);

			evalTreinamento = new Evaluation(novoConjuntoTreinamento);
			evalTreinamento.evaluateModel(classificadorMLP,
					novoConjuntoTreinamento);

			evalTeste = new Evaluation(novoConjuntoTeste);
			evalTeste.evaluateModel(classificadorMLP, novoConjuntoTeste);

//			OrganizadorResultados organizadorResultados = new OrganizadorResultados();
//			organizadorResultados
//					.ImprimirEvaluation(pathSaida, "Avaliacao Treinamento_MLP_"
//							+ i + "--" + prefixoArquivoResultado + ".txt",
//							evalTreinamento);
//			organizadorResultados.ImprimirEvaluation(pathSaida,
//					"Avaliacao Teste_MLP_" + i + "--" + prefixoArquivoResultado
//							+ ".txt", evalTeste);

//			System.out.println("------------->>Treinamento<<----------------");
//			System.out.println(evalTreinamento.truePositiveRate(0) + ";"
//					+ evalTreinamento.truePositiveRate(1) + ";"
//					+ evalTreinamento.fMeasure(0) + ";"
//					+ evalTreinamento.fMeasure(1));
//
//			System.out.println("------------->>Testes<<----------------");
//			System.out.println(evalTeste.truePositiveRate(0) + ";"
//					+ evalTeste.truePositiveRate(1) + ";"
//					+ evalTeste.fMeasure(0) + ";" + evalTeste.fMeasure(1));

			array_TP_rain[i] = evalTeste.truePositiveRate(0);
			array_TP_NO_rain[i] = evalTeste.truePositiveRate(1);

			array_TP_rain_Treinamento[i] = evalTreinamento.truePositiveRate(0);
			array_TP_NO_rain_Treinamento[i] = evalTreinamento
					.truePositiveRate(1);

//			organizadorResultados.GerarArquivoClassificacao(classificadorMLP,
//					conjuntoTestes, novoConjuntoTeste, pathSaida,
//					"resultadoMLP_Testes_" + prefixoArquivoResultado + ".csv",
//					TipoEntrada.CONJUNTO_COMPLETO);
//			organizadorResultados.GerarArquivoClassificacao(classificadorMLP,
//					conjuntoTreinamento, novoConjuntoTreinamento, pathSaida,
//					"resultadoMLP_Treinamento_" + prefixoArquivoResultado
//							+ ".csv", TipoEntrada.CONJUNTO_COMPLETO);
		}
		DecimalFormat df = new DecimalFormat("0.00");
		System.out.println("===Treinamento:===");
		System.out.println("TP RAIN: "
				+ df.format(OrganizadorResultados
						.calcularMedia(array_TP_rain_Treinamento))
				+ " ("
				+ df.format(OrganizadorResultados
						.calularDesvioPadrao(array_TP_rain_Treinamento)) + ")");
		System.out.println("TP NO RAIN: "
				+ df.format(OrganizadorResultados
						.calcularMedia(array_TP_NO_rain_Treinamento))
				+ " ("
				+ df.format(OrganizadorResultados
						.calularDesvioPadrao(array_TP_NO_rain_Treinamento))
				+ ")");
		System.out.println("");
		System.out.println("===Teste:===");
		System.out.println("TP RAIN: "
				+ df.format(OrganizadorResultados.calcularMedia(array_TP_rain))
				+ " ("
				+ df.format(OrganizadorResultados
						.calularDesvioPadrao(array_TP_rain)) + ")");
		System.out.println("TP NO RAIN: "
				+ df.format(OrganizadorResultados
						.calcularMedia(array_TP_NO_rain))
				+ " ("
				+ df.format(OrganizadorResultados
						.calularDesvioPadrao(array_TP_NO_rain)) + ")");

	}

}
