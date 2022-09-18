package piat.opendatasearch;

public class Graph {
	private String idGeneral;
	private String type;
	private String link;
	private String relation;
	private String title;
	private String eventLocation;
	private String idArea;
	private String accesibility;
	private String latitude;
	private String longitude;
	private String description;
	private String timetableStart;
	private String timetableEnd;
	private String organizationName;
	private String resourceID;

	public Graph() {

	}

	public Graph(String idGeneral, String type, String link, String relation, String title, String eventLocation,
			String idArea, String accesibility, String latitude, String longitude, String description,
			String timetableStart, String timetableEnd, String organizationName, String resourceID) {
		this.idGeneral = idGeneral;
		this.type = type;
		this.link = link;
		this.relation = relation;
		this.title = title;
		this.eventLocation = eventLocation;
		this.idArea = idArea;
		this.accesibility = accesibility;
		this.latitude = latitude;
		this.longitude = longitude;
		this.description = description;
		this.setTimetableStart(timetableStart);
		this.setTimetableEnd(timetableEnd);
		this.setOrganizationName(organizationName);
		this.setResourceID(resourceID);
	}

	public String getIdGeneral() {
		return idGeneral;
	}

	public void setIdGeneral(String idGeneral) {
		this.idGeneral = idGeneral;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public String getIdArea() {
		return idArea;
	}

	public void setIdArea(String idArea) {
		this.idArea = idArea;
	}

	public String getAccesibility() {
		return accesibility;
	}

	public void setAccesibility(String accesibility) {
		this.accesibility = accesibility;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTimetableStart() {
		return timetableStart;
	}

	public void setTimetableStart(String timetableStart) {
		this.timetableStart = timetableStart;
	}

	public String getTimetableEnd() {
		return timetableEnd;
	}

	public void setTimetableEnd(String timetableEnd) {
		this.timetableEnd = timetableEnd;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getResourceID() {
		return resourceID;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

}
