package classificador;

import java.text.DecimalFormat;

import util.OrganizadorResultados;
import util.TipoEntrada;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class Bayes {

	private boolean useKernelEstimation;
	private Instances conjuntoTreinamento;
	private Instances conjuntoTestes;
	private Evaluation evalTreinamento;
	private Evaluation evalTeste;
	private NaiveBayes classificadorBayesiano;

	public Bayes(boolean useKernelEstimation, Instances conjuntoTreinamento,
			Instances conjuntoTeste) {
		this.useKernelEstimation = useKernelEstimation;
		this.conjuntoTestes = conjuntoTeste;
		this.conjuntoTreinamento = conjuntoTreinamento;
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

		classificadorBayesiano = new NaiveBayes();

		classificadorBayesiano.setUseKernelEstimator(useKernelEstimation);
		classificadorBayesiano.setUseSupervisedDiscretization(false);

		classificadorBayesiano.buildClassifier(novoConjuntoTreinamento);

		evalTreinamento = new Evaluation(novoConjuntoTreinamento);
		evalTreinamento.evaluateModel(classificadorBayesiano,
				novoConjuntoTreinamento);

		evalTeste = new Evaluation(novoConjuntoTeste);
		evalTeste.evaluateModel(classificadorBayesiano, novoConjuntoTeste);
		
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
				"Avaliacao Treinamento_Bayes_" + prefixoArquivoResultado
						+ ".txt", evalTreinamento);
		organizadorResultados.ImprimirEvaluation(pathSaida,
				"Avaliacao Teste_Bayes_" + prefixoArquivoResultado + ".txt",
				evalTeste);

		organizadorResultados
				.GerarArquivoClassificacao(classificadorBayesiano,
						conjuntoTestes, novoConjuntoTeste, pathSaida,
						"resultadoBayes_Testes_" + prefixoArquivoResultado + ".csv",
						TipoEntrada.CONJUNTO_COMPLETO);

		organizadorResultados.GerarArquivoClassificacao(classificadorBayesiano,
				conjuntoTreinamento, novoConjuntoTreinamento, pathSaida,
				"resultadoBayes_Treinamento_" + prefixoArquivoResultado + ".csv",
				TipoEntrada.CONJUNTO_COMPLETO);

	}
}
