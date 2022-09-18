package piat.opendatasearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import piat.opendatasearch.XPathProcess.Propiedad;

import javax.xml.xpath.*;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author Adrian Azpilicueta Pomar 02573134D
 * 
 */
public class P5_XPATH {

	/**
	 * Clase principal de la aplicacion de extraccion de informacion del Portal de
	 * Datos Abiertos del Ayuntamiento de Madrid
	 *
	 */

	public static void main(String[] args) {

		List<String> uris = new ArrayList<String>();
		String label = null;
		Map<String, HashMap<String, String>> datasets = new HashMap<>();
		int numConceptos;
		int numDatasets;

		// Verificar nÂº de argumentos correcto
		if (args.length != 4) {
			String mensaje = "ERROR: Argumentos incorrectos.";
			if (args.length > 0)
				mensaje += " He recibido estos argumentos: " + Arrays.asList(args).toString() + "\n";
			mostrarUso(mensaje);
			System.exit(1);
		}

		// TODO
		/*
		 * Validar los argumentos recibidos en main() Instanciar un objeto ManejadorXML
		 * pasando como parÃ¡metro el codigo de la categoria recibido en el segundo
		 * argumento de main() Instanciar un objeto SAXParser e invocar a su mÃ©todo
		 * parse() pasando como parÃ¡metro un descriptor de fichero, cuyo nombre se
		 * recibe en el primer argumento de main(), y la instancia del objeto
		 * ManejadorXML Invocar al mÃ©todo getConcepts() del objeto ManejadorXML para
		 * obtener un List<String> con las uris de los elementos <concept> cuyo elemento
		 * <code> contiene el cÃ³digo de la categorÃ­a buscado Invocar al mÃ©todo
		 * getLabel() del objeto ManejadorXML para obtener el nombre de la categoria
		 * buscada Invocar al metodo getDatasets() del objeto ManejadorXML para obtener
		 * un mapa con los datasets de la categorÃ­a buscada Crear el fichero de salida
		 * con el nombre recibido en el tercer argumento de main() Volcar al fichero de
		 * salida los datos en el formato XML especificado por ResultadosBusquedaP3.xsd
		 */

		ManejadorXML handler;
		SAXParser sax;
		try {
			handler = new ManejadorXML(args[1]); // Instanciar un objeto ManejadorXML pasando como parametro el codigo
													// de la categoria recibido en el segundo argumento de main()
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			sax = factory.newSAXParser(); // Instanciar un objeto SAXParser
			File archivo = new File(args[0]);
			if (archivo.exists() && archivo.canRead()) {
				mostrarUso("Existe");
			} else {
				mostrarUso("ERROR.");
			}
			sax.parse(archivo, handler); // e invocar a su metodo parse() pasando como parametro un descriptor de
											// fichero, cuyo nombre se recibiÃ³ en el primer argumento de main(), y la
											// instancia del objeto ManejadorXML
			uris = handler.getConcepts();
			label = handler.getLabel();
			datasets = handler.getDatasets();
			numConceptos = uris.size();
			numDatasets = datasets.size();

			/* CREACIÓN DE LAS NUEVAS VARIABLES */
			JSONDatasetParser json = null;
			List<Graph> listaTotal = new ArrayList<Graph>();

//			System.out.println("datasets.size = "+ numDatasets);
//			System.out.println("Soy el print de datasets keyset "+datasets.keySet());

			for (String IDDataset : datasets.keySet()) {
				// System.out.println("Entro en IDDAtaset fe");

				int processors = Runtime.getRuntime().availableProcessors();
				json = new JSONDatasetParser(IDDataset, uris, datasets);
				ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(processors);
				scheduledExecutorService.execute(json);

				for (Graph aux : json.get_lista_graphs()) {
					listaTotal.add(aux);
				}
			}

			FileWriter fichero = new FileWriter(args[2]);
			try (PrintWriter escritor = new PrintWriter(fichero, true)) {
				escritor.println(GenerarXML.XMLPattern());
				escritor.println(GenerarXML.summaryToXML(handler.getCode(), numConceptos, numDatasets));
				escritor.println(GenerarXML.conceptsToXML(uris));
				escritor.println(GenerarXML.datasetsToXML(datasets));
				escritor.println(GenerarXML.resourcesToXML(listaTotal));
				escritor.println(GenerarXML.finalXML());
				escritor.close();
			}

			// FileWriter ficheroJSON = new FileWriter(args[2]);
			/*
			 * try { File dirActual = new File(".");
			 * System.out.println("Archivo a procesar: " + dirActual.getCanonicalPath() +
			 * "/" + args[2] ); File fichSource = new File(dirActual.getCanonicalPath() +
			 * "/" + args[2] ); if ( fichSource.exists() )
			 * System.out.println("Se ha abierto el archivo. Inicio del analisis" ); else {
			 * System.out.println("No se ha abierto el archivo " +
			 * fichSource.getAbsolutePath()); return; } InputSource inputSource = new
			 * InputSource(args[2]); DocumentBuilderFactory domFactory =
			 * DocumentBuilderFactory .newInstance(); // Creación de la factoria
			 * domFactory.setNamespaceAware(true);
			 * domFactory.setIgnoringElementContentWhitespace(true);// Configuración de la
			 * factoria para que trabaje con espacios de nombres DocumentBuilder builder =
			 * domFactory.newDocumentBuilder(); Document inputDoc = builder.parse
			 * (inputSource); XPath xpath = XPathFactory.newInstance().newXPath();
			 * 
			 * //xpath.setNamespaceContext(new NamespaceResolver(inputDoc));
			 * 
			 * //xpath.evaluate(label, inputSource);
			 * //XPathProcess.evaluar("C:/Users/azpi3/Desktop/piat/P5v2/salida.xml"); }catch
			 * (Throwable t) { t.printStackTrace(); }
			 */

			List<Propiedad> lista = null;

			try {
				lista = XPathProcess.evaluar(args[2]);
			} catch (XPathExpressionException e) {
//                 TODO Auto-generated catch block
				e.printStackTrace();
			}

			FileWriter fSJSON = new FileWriter(args[3]);

			XPathProcess.GenerarJSON oJSON = new XPathProcess.GenerarJSON();

			try (PrintWriter escritorJSON = new PrintWriter(fSJSON, true)) {

				escritorJSON.println(oJSON.JSONBuilder(lista));

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SAXException | ParserConfigurationException |

				IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(0);

	}

	/**
	 * Muestra mensaje de los argumentos esperados por la aplicaciÃ³n. DeberÃ¡
	 * invocase en la fase de validaciÃ³n ante la detecciÃ³n de algÃºn fallo
	 *
	 * @param mensaje Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(String mensaje) {
		Class<? extends Object> thisClass = new Object() {
		}.getClass();

		if (mensaje != null)
			System.err.println(mensaje + "\n");
		System.err.println("Uso: " + thisClass.getEnclosingClass().getCanonicalName()
				+ " <ficheroCatalogo> <codigoCategoria> <ficheroSalida>\n" + "donde:\n"
				+ "\t ficheroCatalogo:\t path al fichero XML con el catalogo de datos\n"
				+ "\t codigoCategoria:\t codigo de la categoria de la que se desea obtener datos\n"
				+ "\t ficheroSalida:\t\t nombre del fichero XML de salida\n"
				+ "\t ficheroSalida:\t\t nombre del fichero JSON de salida\n");
	}

}
