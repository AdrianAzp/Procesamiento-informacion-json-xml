package piat.opendatasearch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.*;

/**
 * @author Adrian Azpilicueta Pomar 02573134D
 * 
 */
public class XPathProcess {

	static List<Propiedad> listaTitulo = new ArrayList<Propiedad>();
	static ArrayList<Propiedad> listaID = new ArrayList<Propiedad>();
	static ArrayList<Propiedad> numID = new ArrayList<Propiedad>();
	static List<Propiedad> listaDS = new ArrayList<Propiedad>();

	public static final String ANSI_BLUE = "\u001B[34m";

	private static boolean debug = false;
	private static boolean debuglvl2 = true;

	// private static XPath xPath;

	/**
	 * Método que se encarga de evaluar las expresiones xpath sobre el fichero XML
	 * generado en la práctica 4
	 * 
	 * @return - Una lista con la propiedad resultante de evaluar cada expresion
	 *         xpath
	 * @throws IOException
	 * 
	 * @throws ParserConfigurationException
	 */

	public static List<Propiedad> evaluar(String ficheroXML) throws IOException, XPathExpressionException {

		List<Propiedad> lista = new ArrayList<Propiedad>();
		ArrayList<String> IDAMirar = new ArrayList<String>();

		// TODO: Realizar las 4 consultas xpath al documento XML de entrada qeu se
		// indican en el enunciado en el apartado "3.2 Búsqueda de información y
		// generación del documento de resultados."
		// Cada consulta devolverá una información que se añadirá a la una
		// colección
		// List <Propiedad>
		// Una consulta puede devolver una propiedad o varias

		String nomFichero = ficheroXML;
		try {
			File dirActual = new File(".");
			System.out.println("Archivo a procesar: " + dirActual.getCanonicalPath() + "/" + nomFichero);
			File fichSource = new File(dirActual.getCanonicalPath() + "/" + nomFichero);

			if (fichSource.exists() && fichSource.canRead())
				System.out.println("Se ha abierto el archivo. Inicio del analisis");
			else {
				System.out.println("No se ha abierto el archivo " + fichSource.getAbsolutePath());
			}
			if (debug) {
				System.out.println(fichSource.toString());
				System.out.println("longitud fichSource: " + fichSource.length()); // Lo coge
			}
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance(); // Creación de la factoria
//			domFactory.setNamespaceAware(true);
			domFactory.setIgnoringElementContentWhitespace(true);// Configuración de la factoria para que trabaje con

			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document inputDoc = builder.parse(fichSource.getCanonicalPath());
			if (debug) {
				System.out.println(inputDoc.getDocumentURI());
			}

			// Mucho mejor decir que no tiene dataset, setNamespaceAware está en false de
			// primeras
			// Simplemente no decimos que tenemos namespace, porque se da la casualidad de
			// que no tenemos namespace

//			NamespaceContext ctx = new NamespaceContext() {
//				public String getNamespaceURI(String prefix) {
//					String uri;
//					switch (prefix) {
//					case "s":
//						uri = "http://www.piat.dte.upm.es/practica4";
//						break;
//					default:
//						uri = "";
//					}
//					return uri;
//				}
//
//				@Override
//				public Iterator getPrefixes(String val) {
//					throw new IllegalAccessError("Not implemented!");
//				}
//
//				@Override
//				public String getPrefix(String uri) {
//					throw new IllegalAccessError("Not implemented!");
//				}
//			};

			XPath xpath = XPathFactory.newInstance().newXPath();
			// xpath.setNamespaceContext(new NamespaceResolver(inputDoc)); Si tiene
			// namespace se descomenta
//			xpath.setNamespaceContext(ctx);

			// String datasets ="//s:datasets[count(descendant::s:datasets)]";
			// NodeList lnode = (NodeList) xpath.evaluate("//*[name()='datasets']",
			// inputDoc, XPathConstants.NODESET);
			NodeList lnode = (NodeList) xpath.evaluate("/searchResults/results/datasets/dataset", inputDoc,
					XPathConstants.NODESET);
			// System.out.println("Cuantos datasets hay: " + lnode.getLength());

			NodeList nodosResource = (NodeList) xpath.evaluate("/searchResults/results/resources/resource", inputDoc,
					XPathConstants.NODESET);
			if (debug) {
				for (int i = 0; i < nodosResource.getLength(); i++) {
					Element resource = (Element) nodosResource.item(i);
					System.out.println("Resource: " + (String) xpath.evaluate("@id", resource, XPathConstants.STRING)); // Sí
																														// que
																														// lo
																														// pilla
				}

			}

			// NodeList datasetsC = (NodeList) xpath.evaluate(datasets, inputDoc,
			// XPathConstants.NUMBER);
			// System.out.println("query"
			// +xpath.evaluate("/s:searchResults/s:summary/s:query/text()", inputDoc));
			Propiedad query = new Propiedad("query", xpath.evaluate("/searchResults/summary/query/text()", inputDoc));
			lista.add(query); // lista(0)
			System.out.println(query.toString());
			String numDatasets = xpath.evaluate("count(/searchResults/results/datasets/dataset)", inputDoc);
			System.out.println("numDatasets " + numDatasets);
			Propiedad numDatasetsProperty = new Propiedad("numDatasets", numDatasets);
			lista.add(numDatasetsProperty); // lista(1)
			// String numResources =
			// xpath.evaluate("count(/s:searchResults/s:results/s:resources/s:resource)",
			// inputDoc);

			// ##########################################
//			String idDataset = xpath.evaluate("/s:searchResults/s:results/s:datasets/s:dataset[@id/text()]", inputDoc);
//			System.out.println("ID DEL DATASET: "+idDataset);

			// POR CADA DATASET HIJO DE DATASETS TENGO QUE MIRAR SU ID Y GUARDARLO EN UN
			// ARRAY DE STRINGS QUE GUARDEN LOS IDS
			// SI ESE ID ES IGUAL QUE EL DE UN RESOURCE LO COJO Y LO CUENTO

			if (debug) {
				System.out.println("Cuantos resource hay: " + lnode.getLength());
			}

			for (int i = 0; i < lnode.getLength(); i++) { // Este lnode est� bien???? Nos est� dando resource o eso
															// parece //resource tambien tiene title pinta de que esta
															// mal, preguntar
				Element dataset = (Element) lnode.item(i);
				String idDelDatasetActual = (String) xpath.evaluate("@id", dataset, XPathConstants.STRING);
				Propiedad idDatasetProperty = new Propiedad("id", idDelDatasetActual);
				lista.add(idDatasetProperty); // lista(2,4)
				if (debug) {
					System.out.println("Supuesto ID del dataset: " + idDelDatasetActual);
				}

//				Propiedad ID = new Propiedad("id",
//						xpath.evaluate("@id", dataset, XPathConstants.STRING).toString());

				listaID.add(idDatasetProperty);
				IDAMirar.add((String) xpath.evaluate("@id", dataset, XPathConstants.STRING)); // Cojo el ID del dataset
																								// hijo de datasets

//				System.out.println("\n- " + xpath.evaluate("./s:title/text()", dataset, XPathConstants.STRING));
//				Propiedad title = new Propiedad("title",
//						xpath.evaluate("./s:title/text()", dataset, XPathConstants.STRING).toString());
//				listaTitulo.add(title); //lista(3,5)
			}

			List<Integer> ArraynumResourceConIgualID = new ArrayList<>();
			int numResourceConIgualID = 0;
			int k = 0;
			String apoyo2 = null;

			for (int i = 0; i < nodosResource.getLength(); i++) {

				String idDelResourceActual;
				Element resource = (Element) nodosResource.item(i);
				idDelResourceActual = (String) xpath.evaluate("@id", resource, XPathConstants.STRING);
				// System.out.println("Resource: " + (String) xpath.evaluate("@id", resource,
				// XPathConstants.STRING)); // Sí
				// que
				// lo
				// pilla

				if (debug) {
					System.out.println("Lista de IDS a buscar: " + IDAMirar.toString());

				}

				System.out.println("\n- " + xpath.evaluate("./title/text()", resource, XPathConstants.STRING));
				Propiedad title = new Propiedad("title",
						xpath.evaluate("./title/text()", resource, XPathConstants.STRING).toString());
				listaTitulo.add(title);

				for (String apoyo : IDAMirar) {

					if (debug) {
						System.err.println("Estoy comparando " + idDelResourceActual + " con: " + apoyo);
					}
					if (apoyo.equals(idDelResourceActual)) {
						if (!apoyo.equals(apoyo2)) {
							ArraynumResourceConIgualID.add(numResourceConIgualID);
							numResourceConIgualID = 0;
						}
						numResourceConIgualID++;
						Propiedad idResource = new Propiedad("id", idDelResourceActual);
						lista.add(idResource); // lista(6)
						if (debuglvl2) {
							System.err.println("El " + i + " numRes = " + numResourceConIgualID);
						}
						apoyo2 = apoyo;

					}

				}

				if (debug) {
					System.out.println(idDelResourceActual + "\n" + idDelResourceActual);
				}

			}

			ArraynumResourceConIgualID.add(numResourceConIgualID);
			ArraynumResourceConIgualID.remove(0);
			for (int a : ArraynumResourceConIgualID) {
				Propiedad numResource = new Propiedad("num", String.valueOf(a));
				numID.add(numResource);
				lista.add(numResource);// lista(6)
			}

//			if (debuglvl2) {
//				System.out.println(numID.get(5));
//			}

//			for (int a : ArraynumResourceConIgualID) {
//				Propiedad numResource = new Propiedad("num", String.valueOf(a));
//
//				numID.add(numResource);
//				lista.add(numResource);// lista(6)
//			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

		/*
		 * // FOREACH DE CONTROL for (Propiedad p : lista) {
		 * System.out.println("ESTE ES EL VALOR DE LA LISTA" + p.toString()); }
		 */
		return lista;
	}

	/**
	 * Esta clase interna define una propiedad equivalente a "nombre":"valor" en
	 * JSON
	 */
	public static class Propiedad {
		public final String nombre;
		public final String valor;

		public Propiedad(String nombre, String valor) {
			this.nombre = nombre;
			this.valor = valor;
		}

		@Override
		public String toString() {
			return "\"" + this.nombre + "\"" + " : " + "\"" + this.valor + "\"";

		}

	} // Fin de la clase interna Propiedad

	public static class GenerarJSON {

		private static final String beginObject = "{";
		private static final String endObject = "}";

		private static final String beginArray = "[";
		private static final String endArray = "]";

		private static final String query = "\n\t#ID#,";
		private static final String numDataset = "\n\t#ID#,";

		// private static final String infDatasets = "\n\t\t"infDatasets" :";
		private static final String infDatasets = "\n\t#ID#";
		private static final String id = "\n\t\t\t#ID#,";
		private static final String num = "\n\t\t\t#ID#,";

		// private static final String titles = "\n\t\t"titles":";
		private static final String titles = "\n\t#ID#";

		private static final String title = "\n\t\t\t{#ID#},";

		public String JSONBuilder(List<Propiedad> lista) {
			StringBuilder sbSalida = new StringBuilder();
			sbSalida.append(beginObject);
			sbSalida.append(query.replace("#ID#", lista.get(0).toString()));
			sbSalida.append(numDataset.replace("#ID#", lista.get(1).toString()));
			sbSalida.append(infDatasets.replace("#ID#", "\"infDatasets\" :"));
			sbSalida.append(" " + beginArray);
			// sbSalida.append(id.replace("#ID#", lista.get(2).toString()));
			sbSalida.append(datasetsToJSON());

//          sbSalida.append(id.replace("#ID#", lista.get(3).toString()));
//          sbSalida.append(num.replace("#ID#", lista.get(4).toString()));
			sbSalida.append("\n\t" + endArray + ",");

			sbSalida.append(titles.replace("#ID#", "\"titles\" : "));
			sbSalida.append(beginArray);

			sbSalida.append(titleToJSON(listaTitulo));
			sbSalida.append("\n\t" + endArray);

			sbSalida.append("\n" + endObject);

			String sSalida = sbSalida.toString();
			return sSalida;
		}

		public String titleToJSON(List<Propiedad> listaTitulo) {
			StringBuilder salida = new StringBuilder();
			String titulo;
			Propiedad p;

			for (int i = 0; i < listaTitulo.size(); i++) {
				p = listaTitulo.get(i);
				titulo = p.toString();
				salida.append("\n\t\t" + beginObject);
				salida.append(titulo);
				salida.append(endObject);

				if (i < listaTitulo.size() - 1) {
					salida.append(",");
				}

			}

			return salida.toString();

		}

		public String datasetsToJSON() {
			StringBuilder salida = new StringBuilder();
			String id;
			String num;
			Propiedad Pid, Pnum;

//        	for (String apoyo : IDAMirar) {
//        		p = new Propiedad ("id", apoyo);
//        		listaDS.add(p);
//        	}
//        	
			for (int i = 0; i < listaID.size(); i++) {
				if (debuglvl2) {
					System.out.println("listaId.size" + listaID.size());
				}
				Pid = listaID.get(i);
				Pnum = numID.get(i);
				if (debuglvl2) {
					System.out.println(numID.size());
					System.out.println(numID.toString());
					System.out.println(Pnum);
				}

				num = Pnum.toString();
				id = Pid.toString();

				salida.append("\n\t\t" + beginObject);
				salida.append("\n\t\t\t" + id + ",");
				salida.append("\n\t\t\t" + num);
				salida.append("\n\t\t" + endObject);
				if (i < listaID.size() - 1) {
					salida.append(",");
				}

			}

			return salida.toString();
		}

	}

} // Fin de la clase XPathProcess