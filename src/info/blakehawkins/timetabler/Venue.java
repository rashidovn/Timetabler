package info.blakehawkins.timetabler;

/**
 * Dummy class used to store data from venues.xml
 */
public class Venue {
	public final String name, description, mapUri;
	
	/**
	 * Constructor used by VenuxXMLParser
	 */
	public Venue(String name, String desc, String mapUri) {
		this.name=name;
		this.description=desc;
		this.mapUri=mapUri;
	}
}
