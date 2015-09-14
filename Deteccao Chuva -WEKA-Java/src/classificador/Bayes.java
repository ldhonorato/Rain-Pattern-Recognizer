package classificador;

import util.OrganizadorResultados;
import util.TipoEntrada;
import util.ValidacaoCruzada;
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
	private TipoEntrada tipoConjunto;

	public Bayes(boolean useKernelEstimation, Instances conjuntoTreinamento,
			Instances conjuntoTeste, TipoEntrada tipoConjunto) {
		this.useKernelEstimation = useKernelEstimation;
		this.conjuntoTestes = conjuntoTeste;
		this.conjuntoTreinamento = conjuntoTreinamento;
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
		
		classificadorBayesiano = new NaiveBayes();

		classificadorBayesiano.setUseKernelEstimator(useKernelEstimation);
		classificadorBayesiano.setUseSupervisedDiscretization(false);

		if (validacaoCruzada == false) {
			classificadorBayesiano.buildClassifier(novoConjuntoTreinamento);

			evalTreinamento = new Evaluation(novoConjuntoTreinamento);
			evalTreinamento.evaluateModel(classificadorBayesiano,
					novoConjuntoTreinamento);

			evalTeste = new Evaluation(novoConjuntoTeste);
			evalTeste.evaluateModel(classificadorBayesiano, novoConjuntoTeste);
			
			
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
					"Avaliacao Treinamento_Bayes_" + tipoConjunto +".txt", evalTreinamento);
			organizadorResultados.ImprimirEvaluation(pathSaida,
					"Avaliacao Teste_Bayes_" + tipoConjunto + ".txt", evalTeste);

			organizadorResultados.GerarArquivoClassificacao(
					classificadorBayesiano, conjuntoTestes, novoConjuntoTeste,
					pathSaida, "resultadoBayes_Testes_" + tipoConjunto +".csv", this.tipoConjunto);
			organizadorResultados.GerarArquivoClassificacao(
					classificadorBayesiano, conjuntoTreinamento,
					novoConjuntoTreinamento, pathSaida,
					"resultadoBayes_Treinamento_" + tipoConjunto + ".csv", this.tipoConjunto);

		} else {
			// VALIDACAO CRUZADA COM i RUNS e n FOLDS
			ValidacaoCruzada validacao = new ValidacaoCruzada();
			validacao.ExecutarValidacaoCruzada(classificadorBayesiano,
					novoConjuntoTreinamento, pathSaida, "validacaoCruzada_Bayes_" + tipoConjunto +".txt",
					folds, runs);
		}
	}
}
