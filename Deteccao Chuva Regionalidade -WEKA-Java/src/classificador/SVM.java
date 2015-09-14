package classificador;

import java.text.DecimalFormat;

import util.OrganizadorResultados;
import util.TipoEntrada;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class SVM {
	private double cost;
	private double epsilon;
	private boolean normalize;
	private int kernelFunction;
	private double gamma;
	private int degree;
	private double coef0;
	private Instances conjuntoTreinamento;
	private Instances conjuntoTestes;
	private Evaluation evalTreinamento;
	private Evaluation evalTeste;
	private LibSVM classificadorSVM;

	public SVM(double cost, double epsilon, boolean normalize,
			int kernelFunction, double gamma, int degree, double coef0,
			Instances conjuntoTreinamento, Instances conjuntoTestes) {
		super();
		this.cost = cost;
		this.epsilon = epsilon;
		this.normalize = normalize;
		this.kernelFunction = kernelFunction;
		this.gamma = gamma;
		this.degree = degree;
		this.coef0 = coef0;
		this.conjuntoTreinamento = conjuntoTreinamento;
		this.conjuntoTestes = conjuntoTestes;
	}

	public void Executar(String pathSaida, String prefixoArquivoResultado) throws Exception {
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

		classificadorSVM = new LibSVM();
		String[] options = new String[2];
		options[0] = "-S 0";
		options[1] = "-K " + kernelFunction;// 0: linear, 1: polinomial, 2:
											// radial, 3: sigmoidal
		classificadorSVM.setOptions(options);
		classificadorSVM.setCoef0(coef0);
		classificadorSVM.setCost(cost);
		classificadorSVM.setEps(epsilon);
		classificadorSVM.setNormalize(normalize);
		classificadorSVM.setGamma(gamma);
		classificadorSVM.setDegree(degree);
		classificadorSVM.setCacheSize(40.0);
		classificadorSVM.setDebug(false);
		classificadorSVM.setDoNotReplaceMissingValues(false);
		classificadorSVM.setLoss(0.1);
		classificadorSVM.setNu(0.5);
		classificadorSVM.setProbabilityEstimates(false);
		classificadorSVM.setShrinking(true);

		classificadorSVM.buildClassifier(novoConjuntoTreinamento);

		evalTreinamento = new Evaluation(novoConjuntoTreinamento);
		evalTreinamento
				.evaluateModel(classificadorSVM, novoConjuntoTreinamento);

		evalTeste = new Evaluation(novoConjuntoTeste);
		evalTeste.evaluateModel(classificadorSVM, novoConjuntoTeste);

		DecimalFormat df = new DecimalFormat("0.00");
		System.out.println("------------->>Testes<<----------------");
		System.out.println(df.format(evalTeste.truePositiveRate(0)) + ";"
				+ df.format(evalTeste.truePositiveRate(1)) + ";" + df.format(evalTeste.fMeasure(0))
				+ ";" + df.format(evalTeste.fMeasure(1)));

		System.out.println("------------->>Treinamento<<----------------");
		System.out.println(df.format(evalTreinamento.truePositiveRate(0)) + ";"
				+ df.format(evalTreinamento.truePositiveRate(1)) + ";"
				+ df.format(evalTreinamento.fMeasure(0)) + ";"
				+ df.format(evalTreinamento.fMeasure(1)));

		OrganizadorResultados organizadorResultados = new OrganizadorResultados();
		organizadorResultados.ImprimirEvaluation(pathSaida,
				"Avaliacao Treinamento_SVM_" + prefixoArquivoResultado + ".txt",
				evalTreinamento);
		organizadorResultados.ImprimirEvaluation(pathSaida,
				"Avaliacao Teste_SVM_" + prefixoArquivoResultado + ".txt", evalTeste);

		organizadorResultados.GerarArquivoClassificacao(classificadorSVM,
				conjuntoTestes, novoConjuntoTeste, pathSaida,
				"resultadoSVM_Testes_" + prefixoArquivoResultado + ".csv", TipoEntrada.CONJUNTO_COMPLETO);
		organizadorResultados.GerarArquivoClassificacao(classificadorSVM,
				conjuntoTreinamento, novoConjuntoTreinamento, pathSaida,
				"resultadoSVM_Treinamento_" + prefixoArquivoResultado + ".csv",
				TipoEntrada.CONJUNTO_COMPLETO);

	}

}
