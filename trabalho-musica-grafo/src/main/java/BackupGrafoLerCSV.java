import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class BackupGrafoLerCSV {

	private TinkerGraph grafo;
	private GraphTraversalSource grafoTraversal;

	// Cria a instancia do grafo
	public boolean criarGrafo()
	{
		grafo = TinkerGraph.open() ;
		grafoTraversal = grafo.traversal();

		if (grafo == null || grafoTraversal==null)
		{
			return false;
		}
		return true;
	}

	// Adiciona vertice e aresta. Não adiciona se ja existir
	public boolean adicionarElemento(String primeiroNome, String label, String segundoNome, String terceiroNome)
	{
		if (grafo == null || grafoTraversal==null)
		{
			return false;
		}

		//Cria um nó pro primeiro nome, ao menos que já exista
		Vertex primeiroVertice = 
				grafoTraversal.V().has("name",primeiroNome).fold().
				coalesce(__.unfold(),__.addV().property("name",primeiroNome)).next();

		//Cria um nó pro segundo nome, ao menos que já exista
		Vertex segundoVertice = 
				grafoTraversal.V().has("name",segundoNome).fold().
				coalesce(__.unfold(),__.addV().property("name",segundoNome)).next();
		
		//Cria um nó pro segundo nome, ao menos que já exista
		Vertex terceiroVertice = 
				grafoTraversal.V().has("name",terceiroNome).fold().
				coalesce(__.unfold(),__.addV().property("name",terceiroNome)).next();

		//Cria uma aresta entre o primeiro e o segundo nome, ao menos que essa aresta já exista
		grafoTraversal.V().has("name",primeiroNome).out(label).has("name",segundoNome).out(label).has(terceiroNome).fold().
		coalesce(__.unfold(),
				__.addE(label).from(__.V(primeiroVertice)).to(__.V(segundoVertice)))//.iterate();
		.addE(label).from(__.V(primeiroVertice)).to(__.V(terceiroVertice)).iterate();

		return true;
	}

	public void mostrarGrafo()
	{
		Long numeroVertices, numeroArestas;
		numeroVertices = grafoTraversal.V().count().next();
		numeroArestas = grafoTraversal.E().count().next();
		System.out.println("O grafo tem " + numeroVertices + " vertices");
		System.out.println("O grafo tem " + numeroArestas + " arestas");

		List<Path> caminhos = grafoTraversal.V()/*.has("name", "Miley Cyrus")*/.outE().inV().path().by("name").by().toList();

		for (Path caminho : caminhos)
		{
			System.out.println(caminho);
		}
	}

	public static void main(String[] args) 
	{
		BackupGrafoLerCSV grafoDoCSV = new BackupGrafoLerCSV();

		if (grafoDoCSV.criarGrafo())
		{
			try 
			{
				String linhaDoCSV;
				String [] colunasCSV;

				//FileReader fileReader = new FileReader("edges.csv");
				FileReader arquivoCSV = new FileReader("songs_normalize.csv");

				BufferedReader leitor = new BufferedReader(arquivoCSV);

				while((linhaDoCSV = leitor.readLine()) != null) 
				{
					colunasCSV = linhaDoCSV.split(",");
					grafoDoCSV.adicionarElemento(colunasCSV[0],colunasCSV[1],colunasCSV[2], colunasCSV[3]);
				}

				grafoDoCSV.mostrarGrafo();
				leitor.close();         
			}
			catch( Exception e ) 
			{
				System.out.println("Não deu pra abrir o arquivo, deu o seguinte problema ::: " + e.toString());
			}
		}  
	}      


}
