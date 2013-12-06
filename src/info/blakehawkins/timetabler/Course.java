package info.blakehawkins.timetabler;

/**
 * Dummy class used to store data derived from courses.xml
 */
public class Course {
	public final String url, name, drps, euclid, acronym, lecturer;
	public final boolean ai, cg, cs, se;
	public final int level, points, year, semester;

	// SELP Coursework description says "It should be possible to browse the
	// timetable information and read more information about lectures and
	// courses." And "It should be possible to select courses and see only
	// the information for the courses which you have selected." Since this says
	// nothing about the level of detail required, I parse everything.
	/**
	 * Constructor used by XMLManager
	 */
	public Course(String url, String name, String drps, String euclid,
			String acronym, String lecturer, boolean ai, boolean cg,
			boolean cs, boolean se, int level, int points, int year,
			int semester) {
		this.url = url;
		this.name = name;
		this.drps = drps;
		this.euclid = euclid;
		this.acronym = acronym;
		this.lecturer = lecturer;
		this.ai = ai;
		this.cg = cg;
		this.cs = cs;
		this.se = se;
		this.level = level;
		this.points = points;
		this.year = year;
		this.semester = semester;
	}
}
