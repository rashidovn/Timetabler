package info.blakehawkins.timetabler;

public class Lecture {
	public final String day, acronym, room, building, comment, time;
	public final int semester, startTime;
	public final boolean year1, year2, year3, year4, year5;

	public Lecture(String day, String acronym, String room, String building,
			String comment, String time, int semester, int startTime,
			boolean year1, boolean year2, boolean year3, boolean year4,
			boolean year5) {
		this.day = day;
		this.acronym = acronym;
		this.room = room;
		this.building = building;
		this.comment = comment;
		this.time = time;
		this.semester = semester;
		this.startTime = startTime;
		this.year1 = year1;
		this.year2 = year2;
		this.year3 = year3;
		this.year4 = year4;
		this.year5 = year5;
	}
}
