package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Course;
import model.Module;
import model.RunPlan;
import model.StudentProfile;
import view.CreateStudentProfilePane;
import view.FinalYearOptionsMenuBar;
import view.FinalYearOptionsRootPane;
import view.OverviewSelectionPane;
import view.ReserveModulesPane;
import view.SelectModulesPane;

public class FinalYearOptionsController {

	private FinalYearOptionsRootPane view;
	private StudentProfile model;
	private CreateStudentProfilePane cspp;
	private SelectModulesPane smp;
	private ReserveModulesPane rmp;
	private OverviewSelectionPane osp;
	private FinalYearOptionsMenuBar mstmb;
	private String details, reserved, selected;

	public FinalYearOptionsController(StudentProfile model, FinalYearOptionsRootPane view) {

		this.view = view;
		this.model = model;

		cspp = view.getCreateStudentProfilePane();
		smp = view.getSelectModulesPane();
		rmp = view.getReserveModulesPane();
		osp = view.getOverviewSelectionPane();
		mstmb = view.getModuleSelectionToolMenuBar();

		cspp.addCourseDataToComboBox(buildModulesAndCourses());

		this.attachEventHandlers();
	}

	private void attachEventHandlers() {
		mstmb.addExitHandler(e -> System.exit(0));
		mstmb.addAboutHandler(e -> this.alertDialogBuilder(AlertType.INFORMATION,
				"Final Year Module Selection Tool v1.5", null,
				"This Final Year Module Selection Tool was made as a coursework requirement by Wil K Edwards :)"));
		mstmb.addSaveHandler(new SaveProfileHandler());
		mstmb.addLoadHandler(new LoadProfileHandler());
		cspp.addCreateStudentProfileHandler(new CreateStudentProfileHandler());
		smp.resetHandler(new ResetHandler());
		smp.submitHandler(new SubmitHandler());
		smp.getTerm1Buttons().addHandler(new Term1AddHandler());
		smp.getTerm2Buttons().addHandler(new Term2AddHandler());
		smp.getTerm1Buttons().removeHandler(new Term1RemoveHandler());
		smp.getTerm2Buttons().removeHandler(new Term2RemoveHandler());
		rmp.getRpv1().getButtons().addHandler(new ReserveTerm1AddHandler());
		rmp.getRpv2().getButtons().addHandler(new ReserveTerm2AddHandler());
		rmp.getRpv1().getButtons().removeHandler(new ReserveTerm1RemoveHandler());
		rmp.getRpv2().getButtons().removeHandler(new ReserveTerm2RemoveHandler());
		rmp.getRpv1().getButtons().confirmHandler(new ReserveTerm1ConfirmHandler());
		rmp.getRpv2().getButtons().confirmHandler(new ReserveTerm2ConfirmHandler());
		osp.saveOverviewHandler(new SaveOverviewHandler());
	}

	private class SaveProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream("savedProfile.dat"));
				oos.writeObject(model);
				oos.flush();
				alertDialogBuilder(AlertType.INFORMATION, "Save successful", null,
						"Your details and module choices were saved successfully");
			} catch (IOException ex) {
				alertDialogBuilder(AlertType.ERROR, "Save Failed", "File failed to save", ex.toString());
			} finally {
				try {
					oos.close();
				} catch (IOException ex) {
					alertDialogBuilder(AlertType.ERROR, "Save Failed", "File failed to save", ex.toString());
				}
			}
		}
	}

	private class LoadProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savedProfile.dat"))) {
				model = (StudentProfile) ois.readObject();
				ois.close();
				alertDialogBuilder(AlertType.INFORMATION, "Load Successful", null,
						"The student profile loaded successfully");
			} catch (IOException io) {
				alertDialogBuilder(AlertType.ERROR, "Loading failed", "File failed to load", io.toString());
			} catch (ClassNotFoundException c) {
				alertDialogBuilder(AlertType.ERROR, "Loading Failed", "File failed to load", c.toString());
			}

			clearPanes();
			populateProfile();
			populateModuleViews();
			populateReserveModules();
			populateOverview();

		}
	}

	// event handler (currently empty), which can be used for creating a profile
	private class CreateStudentProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			
			if (valid()) {
				model.setStudentCourse(cspp.getSelectedCourse());
				model.setStudentPnumber(cspp.getStudentPnumber());
				model.setStudentName(cspp.getStudentName());
				model.setStudentEmail(cspp.getStudentEmail());
				model.setSubmissionDate(cspp.getStudentDate());

				populateModuleViews();

				view.changeTab(1);
			}
		}
	}

	private class ResetHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			model.getAllSelectedModules().clear();
			model.getAllReservedModules().clear();
			populateModuleViews();
		}
	}

	private class SubmitHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (smp.getTerm1Credits().getCredits() < 60) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Chosen modules total less than 60 credits for term 1");
			} else if (smp.getTerm1Credits().getCredits() > 60) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Selected modules total more than 60 credits for term 1");
			} else if (smp.getTerm2Credits().getCredits() < 60) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Chosen modules total less than 60 credits for term 2");
			} else if (smp.getTerm2Credits().getCredits() > 60) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Selected modules total more than 60 credits for term 2");
			} else {
				model.getAllSelectedModules().clear();
				smp.getTerm1Selected().getModules().forEach(module -> model.addSelectedModule(module));
				smp.getTerm2Selected().getModules().forEach(module -> model.addSelectedModule(module));
				smp.getYearLong().getModules().forEach(module -> model.addSelectedModule(module));

				populateReserveModules();

				view.changeTab(2);
			}
		}
	}

	private class Term1AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (smp.getTerm1Unselected().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking add");
			} else {
				smp.getTerm1Credits().incrCredits(smp.getTerm1Unselected().getSelectedModule().getModuleCredits());
				smp.getTerm1Selected().addModule(smp.getTerm1Unselected().getSelectedModule());
				smp.getTerm1Unselected().removeModule(smp.getTerm1Unselected().getSelectedModule());
			}
		}
	}

	private class Term2AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (smp.getTerm2Unselected().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking add");
			} else {
				smp.getTerm2Credits().incrCredits(smp.getTerm2Unselected().getSelectedModule().getModuleCredits());
				smp.getTerm2Selected().addModule(smp.getTerm2Unselected().getSelectedModule());
				smp.getTerm2Unselected().removeModule(smp.getTerm2Unselected().getSelectedModule());
			}
		}
	}

	private class Term1RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (smp.getTerm1Selected().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking remove");
			} else if (smp.getTerm1Selected().getSelectedModule().isMandatory()) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Mandatory modules cannot be removed");
			} else {
				smp.getTerm1Credits().decrCredits(smp.getTerm1Selected().getSelectedModule().getModuleCredits());
				smp.getTerm1Unselected().addModule(smp.getTerm1Selected().getSelectedModule());
				smp.getTerm1Selected().removeModule(smp.getTerm1Selected().getSelectedModule());
			}
		}
	}

	private class Term2RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (smp.getTerm2Selected().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking remove");
			} else if (smp.getTerm2Selected().getSelectedModule().isMandatory()) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Mandatory modules cannot be removed");
			} else {
				smp.getTerm2Credits().decrCredits(smp.getTerm2Selected().getSelectedModule().getModuleCredits());
				smp.getTerm2Unselected().addModule(smp.getTerm2Selected().getSelectedModule());
				smp.getTerm2Selected().removeModule(smp.getTerm2Selected().getSelectedModule());
			}
		}
	}

	private class ReserveTerm1AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getRpv1().getUnselectedModules().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking add");
			} else {
				rmp.getRpv1().incrCredits(rmp.getRpv1().getUnselectedModules().getSelectedModule().getModuleCredits());
				rmp.getRpv1().addReservedModule(rmp.getRpv1().getUnselectedModules().getSelectedModule());
				rmp.getRpv1().removeUnselectedModule(rmp.getRpv1().getUnselectedModules().getSelectedModule());
			}
		}

	}

	private class ReserveTerm2AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getRpv2().getUnselectedModules().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking add");
			} else {
				rmp.getRpv2().incrCredits(rmp.getRpv2().getUnselectedModules().getSelectedModule().getModuleCredits());
				rmp.getRpv2().addReservedModule(rmp.getRpv2().getUnselectedModules().getSelectedModule());
				rmp.getRpv2().removeUnselectedModule(rmp.getRpv2().getUnselectedModules().getSelectedModule());
			}
		}
	}

	private class ReserveTerm1RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getRpv1().getReservedModules().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking remove");
			} else {
				rmp.getRpv1().decrCredits(rmp.getRpv1().getReservedModules().getSelectedModule().getModuleCredits());
				rmp.getRpv1().addUnselectedModule(rmp.getRpv1().getReservedModules().getSelectedModule());
				rmp.getRpv1().removeReservedModule(rmp.getRpv1().getReservedModules().getSelectedModule());
			}
		}
	}

	private class ReserveTerm2RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getRpv2().getReservedModules().getSelectedModule() == null) {
				alertDialogBuilder(AlertType.ERROR, "Error", null, "Please chose a module before clicking remove");
			} else {
				rmp.getRpv2().decrCredits(rmp.getRpv2().getReservedModules().getSelectedModule().getModuleCredits());
				rmp.getRpv2().addUnselectedModule(rmp.getRpv2().getReservedModules().getSelectedModule());
				rmp.getRpv2().removeReservedModule(rmp.getRpv2().getReservedModules().getSelectedModule());
			}
		}
	}

	private class ReserveTerm1ConfirmHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getRpv1().getCredits() < 30) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Reserved modules total less than 30 credits for term 1. Please add the remaining number of credits");
			} else if (rmp.getRpv1().getCredits() > 30) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Reserved modules total more than 30 credits for term 1. Please remove the excess number of credits");
			} else {
				rmp.switchPanel();
			}
		}
	}

	private class ReserveTerm2ConfirmHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getRpv2().getCredits() < 30) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Reserved modules total less than 30 credits for term 2. Please add the remaining number of credits");
			} else if (rmp.getRpv2().getCredits() > 30) {
				alertDialogBuilder(AlertType.ERROR, "Error", null,
						"Reserved modules total more than 30 credits for term 2. Please remove the excess number of credits");
			} else {
				model.getAllReservedModules().clear();
				rmp.getRpv1().getReservedModules().getModules().forEach(module -> model.addReservedModule(module));
				rmp.getRpv2().getReservedModules().getModules().forEach(module -> model.addReservedModule(module));
				populateOverview();
				view.changeTab(3);
			}
		}
	}

	private class SaveOverviewHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			try (PrintWriter writer = new PrintWriter(new File("StudentProfile.txt"))) {
				writer.println(details);
				writer.println(selected);
				writer.println(reserved);
				alertDialogBuilder(AlertType.INFORMATION, "Information", "Save successful",
						"Your details and selected modules have been successfully saved");
			} catch (IOException ex) {
				alertDialogBuilder(AlertType.ERROR, "About", null, ex.getMessage());
			}
		}

	}

	private Course[] buildModulesAndCourses() {
	    List<Course> courses = new ArrayList<>();

	    try {
	        File file = new File("modules.csv");
	        Scanner sc = new Scanner(file);

	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            String[] data = line.split(",");

	            // Create the module
	            Module module = new Module(data[0], data[1], Integer.parseInt(data[2]), Boolean.parseBoolean(data[3]), RunPlan.valueOf(data[4]));

	            // Check if the course exists in the list of courses
	            Course course = findCourse(courses, data[5]);

	            // If the course doesn't exist, create a new course and add it to the list
	            if (course == null) {
	                course = new Course(data[5]);
	                courses.add(course);
	            }

	            // Add the module to the course
	            course.addModuleToCourse(module);
	        }

	        sc.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

	    return courses.toArray(new Course[0]);
	}

	private Course findCourse(List<Course> courses, String courseName) {
	    for (Course course : courses) {
	        if (course.getCourseName().equals(courseName)) {
	            return course;
	        }
	    }
	    return null;
	}
	
	private Boolean valid() {
		String error = "";
		Boolean isValid = true;
		
		if (!cspp.getStudentPnumber().matches("[p P][0-9]+")) {
			error = "Please input a valid student number\n";
		}
		if (!cspp.getFirstName().matches("^[a-zA-Z\\s]+") || (cspp.getFirstName().length() < 2) || (cspp.getFirstName().length() > 25)) {
			error += "Please input a valid first name\n";
		}
		if (!cspp.getSurname().matches("^[a-zA-Z\\s]+") || (cspp.getSurname().length() < 2) || (cspp.getSurname().length() > 25)) {
			error += "Please input a valid surname\n";
		}
		if (!(cspp.getStudentEmail().contains("@")) || !(cspp.getStudentEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))) {
			error += "Please input a valid email\n";
		}
		if (cspp.getStudentDate() == null || cspp.getStudentDate().isAfter(LocalDate.now())) {
			error += "Please input a valid date\n";
		}
		if (error != "") {
			alertDialogBuilder(AlertType.ERROR, "Error", null, error);
			isValid = false;
		}
		return isValid;
	}
	
	private void populateProfile() {
		cspp.setSelectedCourse(model.getStudentCourse().getCourseName());
		cspp.setStudentPnumber(model.getStudentPnumber());
		cspp.setFirstName(model.getStudentName().getFirstName());
		cspp.setSurname(model.getStudentName().getFamilyName());
		cspp.setStudentEmail(model.getStudentEmail());
		cspp.setStudentDate(model.getSubmissionDate());
	}

	private void populateModuleViews() {
		smp.clearAll();

		for (Module module : cspp.getSelectedCourse().getAllModulesOnCourse()) {
			if (module.getDelivery() == RunPlan.TERM_1) {
				if (module.isMandatory() || model.getAllSelectedModules().contains(module)) {
					smp.getTerm1Selected().addModule(module);
					smp.getTerm1Credits().incrCredits(module.getModuleCredits());
				} else {
					smp.getTerm1Unselected().addModule(module);
				}
			} else if (module.getDelivery() == RunPlan.TERM_2) {
				if (module.isMandatory() || model.getAllSelectedModules().contains(module)) {
					smp.getTerm2Selected().addModule(module);
					smp.getTerm2Credits().incrCredits(module.getModuleCredits());
				} else {
					smp.getTerm2Unselected().addModule(module);
				}
			} else {
				smp.getYearLong().addModule(module);
				smp.getTerm1Credits().incrCredits(module.getModuleCredits() / 2);
				smp.getTerm2Credits().incrCredits(module.getModuleCredits() / 2);
			}
		}
	}

	private void populateReserveModules() {
		rmp.clearAll();

		for (Module module : smp.getTerm1Unselected().getModules()) {
			if (model.getAllReservedModules().contains(module)) {
				rmp.getRpv1().addReservedModule(module);
			} else {
				rmp.getRpv1().addUnselectedModule(module);
			}
		}

		for (Module module : smp.getTerm2Unselected().getModules()) {
			if (model.getAllReservedModules().contains(module)) {
				rmp.getRpv2().addReservedModule(module);
			} else {
				rmp.getRpv2().addUnselectedModule(module);
			}
		}
	}

	private void populateOverview() {
		osp.clearAll();
		details = "STUDENT DETAILS";
		details += "\n===============";
		details += "\nStudent Number: " + model.getStudentPnumber();
		details += "\nName: " + model.getStudentName().getFullName();
		details += "\nEmail: " + model.getStudentEmail();
		details += "\nDate: " + model.getSubmissionDate();
		details += "\nCourse: " + model.getStudentCourse();
		osp.setStudentDetails(details);

		selected = "\nSELECTED MODULES";
		selected += "\n===============";
		for (Module module : model.getAllSelectedModules()) {
			selected += "\nModule code: " + module.getModuleCode() + ", Module name: " + module.getModuleName()
					+ "\nModule credits: " + module.getModuleCredits() + ", Mandatory: " + module.isMandatory()
					+ ", Delivery: " + module.getDelivery() + "\n";
		}
		;
		osp.setSelectedModules(selected);

		reserved = "RESERVED MODULES";
		reserved += "\n===============";
		for (Module module : model.getAllReservedModules()) {
			reserved += "\nModule code: " + module.getModuleCode() + ", Module name: " + module.getModuleName()
					+ "\nModule credits: " + module.getModuleCredits() + ", Mandatory: " + module.isMandatory()
					+ ", Delivery: " + module.getDelivery() + "\n";
		}
		;
		osp.setReservedModules(reserved);
	}

	private void clearPanes() {
		cspp.clearAll();
		smp.clearAll();
		rmp.clearAll();
		osp.clearAll();
	}

	private void alertDialogBuilder(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
