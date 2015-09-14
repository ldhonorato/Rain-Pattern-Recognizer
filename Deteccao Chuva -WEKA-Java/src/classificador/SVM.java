package classificador;

import util.OrganizadorResultados;
import util.TipoEntrada;
import util.ValidacaoCruzada;
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
	private TipoEntrada tipoConjunto;
	
	public SVM(double cost, double epsilon, boolean normalize,
			int kernelFunction, double gamma, int degree, double coef0,
			Instances conjuntoTreinamento, Instances conjuntoTestes, TipoEntrada tipoConjunto) {
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
		this.tipoConjunto = tipoConjunto;
	}


	public void Executar(boolean validacaoCruzada, int folds, int runs,
			String pathSaida) throws Exception {
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
		String []options = new String[2];
		options[0] = "-S 0";
		options[1] = "-K " + kernelFunction;//0: linear, 1: polinomial, 2: radial, 3: sigmoidal
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

		if (validacaoCruzada == false) {
			classificadorSVM.buildClassifier(novoConjuntoTreinamento);

			evalTreinamento = new Evaluation(novoConjuntoTreinamento);
			evalTreinamento.evaluateModel(classificadorSVM,
					novoConjuntoTreinamento);

			evalTeste = new Evaluation(novoConjuntoTeste);
			evalTeste.evaluateModel(classificadorSVM, novoConjuntoTeste);

			System.out.println("------------->>Testes<<----------------");
			System.out.println(evalTeste.truePositiveRate(0) + ";"
					+ evalTeste.truePositiveRate(1) + ";"
					+ evalTeste.fMeasure(0) + ";"
					+ evalTeste.fMeasure(1));
			
			System.out.println("------------->>Treinamento<<----------------");
			System.out.println(evalTeste.truePositiveRate(0) + ";"
					+ evalTreinamento.truePositiveRate(1) + ";"
					+ evalTreinamento.fMeasure(0) + ";"
					+ evalTreinamento.fMeasure(1));
			
			OrganizadorResultados organizadorResultados = new OrganizadorResultados();
			organizadorResultados.ImprimirEvaluation(pathSaida,
					"Avaliacao Treinamento_SVM_" + tipoConjunto +".txt", evalTreinamento);
			organizadorResultados.ImprimirEvaluation(pathSaida,
					"Avaliacao Teste_SVM_" + tipoConjunto +".txt", evalTeste);
			
			organizadorResultados.GerarArquivoClassificacao(classificadorSVM, conjuntoTestes, 
					novoConjuntoTeste, pathSaida, "resultadoSVM_Testes_" + tipoConjunto +".csv", tipoConjunto);
			organizadorResultados.GerarArquivoClassificacao(classificadorSVM, conjuntoTreinamento, 
					novoConjuntoTreinamento, pathSaida, "resultadoSVM_Treinamento_" + tipoConjunto +".csv", tipoConjunto);
			
		} else {
			// VALIDACAO CRUZADA COM i RUNS e n FOLDS
			ValidacaoCruzada validacao = new ValidacaoCruzada();
			validacao.ExecutarValidacaoCruzada(classificadorSVM, novoConjuntoTreinamento, pathSaida,"validacaoCruzada_SVM_" + tipoConjunto +".txt", folds, runs);
		}
	}
	
	
}
