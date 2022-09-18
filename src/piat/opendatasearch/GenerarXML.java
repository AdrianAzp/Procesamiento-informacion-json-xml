package piat.opendatasearch;

/**
 * @author Adrian Azpilicueta Pomar 02573134D
 * 
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerarXML {
	private static final String sXMLPattern = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"
			+ "<searchResults xmlns=\"http://www.piat.dte.upm.es/practica4\"" + "\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "\n"
			+ "xsi:schemaLocation=\"http://www.piat.dte.upm.es/practica4 ResultadosBusquedaP4.xsd\">";

	private static final String summaryPatternInicio = "\t<summary>";
	private static final String sQuery = "\n\t\t<query>#ID#</query>";
	private static final String sNumConcepts = "\n\t\t<numConcepts>#ID#</numConcepts>";
	private static final String sNumDatasets = "\n\t\t<numDatasets>#ID#</numDatasets>";
	private static final String summaryPatternFinal = "\n\t</summary>";

	private static final String resultsPatternInicio = "\t<results>";

	private static final String conceptsPatternInicio = "\n\t\t<concepts>";
	private static final String conceptPattern = "\n\t\t\t<concept id=\"#ID#\"/>";
	private static final String conceptsPatternFinal = "\n\t\t</concepts>";

	private static final String datasetsPatternInicio = "\t\t<datasets>";
	private static final String datasetPattern = "\n\t\t\t<dataset id=\"#ID#\">";
	private static final String datasetPatternTitle = "\n\t\t\t\t<title>#ID#</title>";
	private static final String datasetPatternDescription = "\n\t\t\t\t<description>#ID#</description>";
	private static final String datasetPatternTheme = "\n\t\t\t\t<theme>#ID#</theme>";
	private static final String datasetPatternFinal = "\n\t\t\t</dataset>";
	private static final String datasetsPatternFinal = "\n\t\t</datasets>";

	private static final String resourcesPatternInicio = "\n\t\t<resources>";
	private static final String resourcePattern = "\n\t\t\t<resource id=\"#ID#\">";
	private static final String resourcePatternConcept = "\n\t\t\t\t<concept id=\"#ID#\"></concept>";
	private static final String resourcePatternLink = "\n\t\t\t\t<link><![CDATA[ #ID# ]]></link>";
	private static final String resourcePatternTitle = "\n\t\t\t\t<title>#ID#</title>";

	private static final String resourcePatternLocationInicio = "\n\t\t\t\t<location>";
	private static final String resourcePatternEventLocation = "\n\t\t\t\t\t<eventLocation>#ID#</eventLocation>";
	private static final String resourcePatternArea = "\n\t\t\t\t\t<area>#ID#</area>";
	
	private static final String resourcePatternTimetableInicio = "\n\t\t\t\t\t<timetable>";
	private static final String resourcePatternTimetableStart = "\n\t\t\t\t\t\t<start>#ID#</start>";
	private static final String resourcePatternTimetableEnd = "\n\t\t\t\t\t\t<end>#ID#</end>";
	private static final String resourcePatternTimetableFinal = "\n\t\t\t\t\t</timetable>";

	//private static final String resourcePatternAccesibility = "\n\t\t\t\t\t<accesibility>\"#ID#\"</accesibility>";

	private static final String resourcePatternGeoreferenceInicio = "\n\t\t\t\t\t<georeference>";
	private static final String resourcePatternLatitude = "#ID#";
	private static final String resourcePatternLongitude = " #ID#";
	private static final String resourcePatternGeoreferenceFinal = "</georeference>";

	private static final String resourcePatternLocationFinal = "\n\t\t\t\t</location>";

	private static final String resourceOrganizationPatternInicio = "\n\t\t\t\t<organization>";
	private static final String resourcePatternAccesibility = "\n\t\t\t\t\t<accesibility>#ID#</accesibility>";
	private static final String resourcePatternOrganizationName = "\n\t\t\t\t\t<organizationName>#ID#</organizationName>";
	private static final String resourceOrganizationPatternFinal = "\n\t\t\t\t</organization>";

	private static final String resourcePatternDescription = "\n\t\t\t\t<description>#ID#</description>";
	
	private static final String resourcePatternFinal = "\n\t\t\t</resource>";
	private static final String resourcesPatternFinal = "\t\t</resources>";

	private static final String resultsPatternFinal = "\n\t</results>";

	private static final String sXMLPatternFinal = "\n</searchResults>";

	public static String XMLPattern() {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(sXMLPattern);
		String sSalida = sbSalida.toString();
		return sSalida;
	}

	public static String summaryToXML(String query, int numConcepts, int numDatasets) {
		StringBuilder sbSalida = new StringBuilder();
		String numC = String.valueOf(numConcepts);
		String numD = String.valueOf(numDatasets);

		sbSalida.append(summaryPatternInicio);
		sbSalida.append(sQuery.replace("#ID#", query));
		sbSalida.append(sNumConcepts.replace("#ID#", numC));
		sbSalida.append(sNumDatasets.replace("#ID#", numD));
		sbSalida.append(summaryPatternFinal);

		String sSalida = sbSalida.toString();
		return sSalida;
	}

	public static String conceptsToXML(List<String> lConcepts) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(resultsPatternInicio);
		sbSalida.append(conceptsPatternInicio);
		for (String unConcepto : lConcepts) {
			sbSalida.append(conceptPattern.replace("#ID#", unConcepto));
		}
		sbSalida.append(conceptsPatternFinal);
		String sSalida = sbSalida.toString();
		return sSalida;
	}

	public static String datasetsToXML(Map<String, HashMap<String, String>> mapDatasets) {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(datasetsPatternInicio);

		for (String IDDataset : mapDatasets.keySet()) {
			sbSalida.append(datasetPattern.replace("#ID#", IDDataset));
			HashMap<String, String> aux = mapDatasets.get(IDDataset);
			String title = null, description = null, theme = null;
			for (String key : aux.keySet()) {
				
				if (key.equals("title"))
					title = aux.get(key);
					//sbSalida.append(datasetPatternTitle.replace("#ID#", aux.get(key)));
				if (key.equals("description"))
					description= aux.get(key);
					//sbSalida.append(datasetPatternDescription.replace("#ID#", aux.get(key)));
				if (key.equals("theme"))
					theme = aux.get(key);
					//sbSalida.append(datasetPatternTheme.replace("#ID#", aux.get(key)));
				
			}
			if(title != null && description != null && theme != null) {
				sbSalida.append(datasetPatternTitle.replace("#ID#", title));
				sbSalida.append(datasetPatternDescription.replace("#ID#", description));
				sbSalida.append(datasetPatternTheme.replace("#ID#", theme));
			}
			sbSalida.append(datasetPatternFinal);
		}

		sbSalida.append(datasetsPatternFinal);
		sbSalida.append(resourcesPatternInicio);
		String sSalida = sbSalida.toString();
		return sSalida;
	}

	public static String resourcesToXML(List<Graph> listGraph) {
		StringBuilder sbSalida = new StringBuilder();


		for (Graph aux : listGraph) {
			sbSalida.append(resourcePattern.replace("#ID#", aux.getResourceID()));

			sbSalida.append(resourcePatternConcept.replace("#ID#", aux.getType()));

			if (aux.getLink().equals("null"))
			sbSalida.append(resourcePatternLink.replace("#ID#", aux.getRelation()));
			
			else sbSalida.append(resourcePatternLink.replace("#ID#", aux.getLink()));

			sbSalida.append(resourcePatternTitle.replace("#ID#", aux.getTitle()));

			sbSalida.append(resourcePatternLocationInicio);

			if (aux.getEventLocation() != null)
				sbSalida.append(resourcePatternEventLocation.replace("#ID#", aux.getEventLocation()));
			if (aux.getIdArea() != null)
				sbSalida.append(resourcePatternArea.replace("#ID#", aux.getIdArea()));
			//if (aux.getAccesibility() != null)
			//	sbSalida.append(resourcePatternAccesibility.replace("#ID#", aux.getAccesibility()));

			sbSalida.append(resourcePatternTimetableInicio);
			sbSalida.append(resourcePatternTimetableStart.replace("#ID#", String.valueOf(aux.getTimetableStart())));
			sbSalida.append(resourcePatternTimetableEnd.replace("#ID#", String.valueOf(aux.getTimetableEnd())));
			sbSalida.append(resourcePatternTimetableFinal);
			
			sbSalida.append(resourcePatternGeoreferenceInicio);
			sbSalida.append(resourcePatternLatitude.replace("#ID#", String.valueOf(aux.getLatitude())));
			sbSalida.append(resourcePatternLongitude.replace("#ID#", String.valueOf(aux.getLongitude())));
			sbSalida.append(resourcePatternGeoreferenceFinal);
			
			sbSalida.append(resourcePatternLocationFinal);
			
			sbSalida.append(resourceOrganizationPatternInicio);
			sbSalida.append(resourcePatternAccesibility.replace("#ID#", String.valueOf(aux.getAccesibility())));
			sbSalida.append(resourcePatternOrganizationName.replace("#ID#", String.valueOf(aux.getOrganizationName())));
			sbSalida.append(resourceOrganizationPatternFinal);
			
			sbSalida.append(resourcePatternDescription.replace("#ID#", String.valueOf(aux.getDescription())));

			sbSalida.append(resourcePatternFinal);
		}

		String sSalida = sbSalida.toString();
		return sSalida;
	}

	public static String finalXML() {
		StringBuilder sbSalida = new StringBuilder();
		sbSalida.append(resourcesPatternFinal);
		sbSalida.append(resultsPatternFinal);
		sbSalida.append(sXMLPatternFinal);
		String sSalida = sbSalida.toString();
		return sSalida;
	}
}
