package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

import binaryio.Song;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.Course;
import model.RunPlan;
import model.Module;
import model.StudentProfile;
import view.FinalYearOptionsRootPane;
import view.OverviewSelectionPane;
import view.ReserveModulesPane;
import view.SelectModulesPane;
import view.CreateStudentProfilePane;
import view.FinalYearOptionsMenuBar;

public class FinalYearOptionsController {

	// fields to be used throughout class
	private FinalYearOptionsRootPane view;
	private StudentProfile model;

	private CreateStudentProfilePane cspp;
	private SelectModulesPane smp;
	private ReserveModulesPane rmp;
	private OverviewSelectionPane osp;
	private FinalYearOptionsMenuBar mstmb;
	private String details, reserved, selected;

	public FinalYearOptionsController(StudentProfile model, FinalYearOptionsRootPane view) {
		// initialise view and model fields
		this.view = view;
		this.model = model;

		// initialise view subcontainer fields
		cspp = view.getCreateStudentProfilePane();
		smp = view.getSelectModulesPane();
		rmp = view.getReserveModulesPane();
		osp = view.getOverviewSelectionPane();
		mstmb = view.getModuleSelectionToolMenuBar();

		// add courses to combobox in create student profile pane using the
		// buildModulesAndCourses helper method below
		cspp.addCourseDataToComboBox(buildModulesAndCourses());

		// attach event handlers to view using private helper method
		this.attachEventHandlers();
	}

	// helper method - used to attach event handlers
	private void attachEventHandlers() {
		mstmb.addExitHandler(e -> System.exit(0));
		mstmb.addAboutHandler(e -> this.alertDialogBuilder(AlertType.INFORMATION,
				"Final Year Module Selection Tool v1.0", null,
				"This Final Year Module Selection Tool was made as a coursework requirement by Wil K Edwards :)"));
		mstmb.addSaveHandler(new SaveProfileHandler());
		mstmb.addLoadHandler(new LoadProfileHandler());
		cspp.addCreateStudentProfileHandler(new CreateStudentProfileHandler());
		smp.resetHandler(e -> populateModuleViews());
		smp.submitHandler(new SubmitHandler());
		smp.getTerm1Buttons().addHandler(new Term1AddHandler());
		smp.getTerm2Buttons().addHandler(new Term2AddHandler());
		smp.getTerm1Buttons().removeHandler(new Term1RemoveHandler());
		smp.getTerm2Buttons().removeHandler(new Term2RemoveHandler());
		rmp.getRpv1().getButtons().addHandler(new ReserveTerm1AddHandler());
		rmp.getRpv2().getButtons().addHandler(new ReserveTerm2AddHandler());
		rmp.getRpv1().getButtons().removeHandler(new ReserveTerm1RemoveHandler());
		rmp.getRpv2().getButtons().removeHandler(new ReserveTerm2RemoveHandler());
		rmp.getRpv1().getButtons().confirmHandler(e -> rmp.switchPanel());
		rmp.getRpv2().getButtons().confirmHandler(new ReserveConfirmHandler());
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

			cspp.clearAll();
			smp.clearAll();
			rmp.clearAll();
			osp.clearAll();
			cspp.loadProfile(model);
			populateModuleViews();
			populateOverview();

		}
	}

	// event handler (currently empty), which can be used for creating a profile
	private class CreateStudentProfileHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (!cspp.getStudentPnumber().toUpperCase().startsWith("P")) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null, "Please input a valid student number");
			} else if (cspp.getFirstName().isBlank()) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null, "Please input a first name");
			} else if (cspp.getSurname().isBlank()) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null, "Please input a last name");
			} else if (cspp.getStudentEmail().isBlank()) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null, "Please input a valid email");
			} else if (cspp.getStudentDate().isAfter(LocalDate.now())) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null, "Please input a valid date");
			} else {
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

	private class SubmitHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (smp.getTerm1Credits().getCredits() != 60) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null,
						"Selected term 1 credits must be exactly 60 credits");
			} else if (smp.getTerm2Credits().getCredits() != 60) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null,
						"Selected term 2 credits must be exactly 60 credits");
			} else {
				model.getAllSelectedModules().clear();
				smp.getTerm1Selected().getModules().forEach(module -> model.addSelectedModule(module));
				smp.getTerm2Selected().getModules().forEach(module -> model.addSelectedModule(module));
				smp.getYearLong().getModules().forEach(module -> model.addSelectedModule(module));

				rmp.clearAll();
				rmp.getRpv1().getUnselectedModules().setModules(smp.getTerm1Unselected().getModules());
				rmp.getRpv2().getUnselectedModules().setModules(smp.getTerm2Unselected().getModules());

				view.changeTab(2);
			}
		}
	}

	private class Term1AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			smp.getTerm1Credits().incrCredits(smp.getTerm1Unselected().getSelectedModule().getModuleCredits());
			smp.getTerm1Selected().addModule(smp.getTerm1Unselected().getSelectedModule());
			smp.getTerm1Unselected().removeModule(smp.getTerm1Unselected().getSelectedModule());
		}
	}

	private class Term2AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			smp.getTerm2Credits().incrCredits(smp.getTerm2Unselected().getSelectedModule().getModuleCredits());
			smp.getTerm2Selected().addModule(smp.getTerm2Unselected().getSelectedModule());
			smp.getTerm2Unselected().removeModule(smp.getTerm2Unselected().getSelectedModule());
		}
	}

	private class Term1RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			smp.getTerm1Credits().decrCredits(smp.getTerm1Selected().getSelectedModule().getModuleCredits());
			smp.getTerm1Unselected().addModule(smp.getTerm1Selected().getSelectedModule());
			smp.getTerm1Selected().removeModule(smp.getTerm1Selected().getSelectedModule());
		}
	}

	private class Term2RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			smp.getTerm2Credits().decrCredits(smp.getTerm2Selected().getSelectedModule().getModuleCredits());
			smp.getTerm2Unselected().addModule(smp.getTerm2Selected().getSelectedModule());
			smp.getTerm2Selected().removeModule(smp.getTerm2Selected().getSelectedModule());
		}
	}

	private class ReserveTerm1AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			rmp.getRpv1().incrCredits(rmp.getRpv1().getUnselectedModules().getSelectedModule().getModuleCredits());
			rmp.getRpv1().addReservedModule(rmp.getRpv1().getUnselectedModules().getSelectedModule());
			rmp.getRpv1().removeUnselectedModule(rmp.getRpv1().getUnselectedModules().getSelectedModule());
		}
	}

	private class ReserveTerm2AddHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			rmp.getRpv2().incrCredits(rmp.getRpv2().getUnselectedModules().getSelectedModule().getModuleCredits());
			rmp.getRpv2().addReservedModule(rmp.getRpv2().getUnselectedModules().getSelectedModule());
			rmp.getRpv2().removeUnselectedModule(rmp.getRpv2().getUnselectedModules().getSelectedModule());
		}
	}

	private class ReserveTerm1RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			rmp.getRpv1().decrCredits(rmp.getRpv1().getReservedModules().getSelectedModule().getModuleCredits());
			rmp.getRpv1().addUnselectedModule(rmp.getRpv1().getReservedModules().getSelectedModule());
			rmp.getRpv1().removeReservedModule(rmp.getRpv1().getReservedModules().getSelectedModule());
		}
	}

	private class ReserveTerm2RemoveHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			rmp.getRpv2().decrCredits(rmp.getRpv2().getReservedModules().getSelectedModule().getModuleCredits());
			rmp.getRpv2().addUnselectedModule(rmp.getRpv2().getReservedModules().getSelectedModule());
			rmp.getRpv2().removeReservedModule(rmp.getRpv2().getReservedModules().getSelectedModule());
		}
	}

	private class ReserveConfirmHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if (rmp.getRpv1().getCredits() != 30) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null,
						"Reserved term 1 credits must be exactly 30 credits");
			} else if (rmp.getRpv2().getCredits() != 30) {
				alertDialogBuilder(AlertType.ERROR, "Input Error", null,
						"Reserved term 2 credits must be exactly 30 credits");
			} else {
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

	// helper method - builds modules and course data and returns courses within an
	// array
	private Course[] buildModulesAndCourses() {
		Module imat3423 = new Module("IMAT3423", "Systems Building: Methods", 15, true, RunPlan.TERM_1);
		Module ctec3451 = new Module("CTEC3451", "Development Project", 30, true, RunPlan.YEAR_LONG);
		Module ctec3902_SoftEng = new Module("CTEC3902", "Rigorous Systems", 15, true, RunPlan.TERM_2);
		Module ctec3902_CompSci = new Module("CTEC3902", "Rigorous Systems", 15, false, RunPlan.TERM_2);
		Module ctec3110 = new Module("CTEC3110", "Secure Web Application Development", 15, false, RunPlan.TERM_1);
		Module ctec3605 = new Module("CTEC3605", "Multi-service Networks 1", 15, false, RunPlan.TERM_1);
		Module ctec3606 = new Module("CTEC3606", "Multi-service Networks 2", 15, false, RunPlan.TERM_2);
		Module ctec3410 = new Module("CTEC3410", "Web Application Penetration Testing", 15, false, RunPlan.TERM_2);
		Module ctec3904 = new Module("CTEC3904", "Functional Software Development", 15, false, RunPlan.TERM_2);
		Module ctec3905 = new Module("CTEC3905", "Front-End Web Development", 15, false, RunPlan.TERM_2);
		Module ctec3906 = new Module("CTEC3906", "Interaction Design", 15, false, RunPlan.TERM_1);
		Module ctec3911 = new Module("CTEC3911", "Mobile Application Development", 15, false, RunPlan.TERM_1);
		Module imat3410 = new Module("IMAT3104", "Database Management and Programming", 15, false, RunPlan.TERM_2);
		Module imat3406 = new Module("IMAT3406", "Fuzzy Logic and Knowledge Based Systems", 15, false, RunPlan.TERM_1);
		Module imat3611 = new Module("IMAT3611", "Computer Ethics and Privacy", 15, false, RunPlan.TERM_1);
		Module imat3613 = new Module("IMAT3613", "Data Mining", 15, false, RunPlan.TERM_1);
		Module imat3614 = new Module("IMAT3614", "Big Data and Business Models", 15, false, RunPlan.TERM_2);
		Module imat3428_CompSci = new Module("IMAT3428", "Information Technology Services Practice", 15, false,
				RunPlan.TERM_2);

		Course compSci = new Course("Computer Science");
		compSci.addModuleToCourse(imat3423);
		compSci.addModuleToCourse(ctec3451);
		compSci.addModuleToCourse(ctec3902_CompSci);
		compSci.addModuleToCourse(ctec3110);
		compSci.addModuleToCourse(ctec3605);
		compSci.addModuleToCourse(ctec3606);
		compSci.addModuleToCourse(ctec3410);
		compSci.addModuleToCourse(ctec3904);
		compSci.addModuleToCourse(ctec3905);
		compSci.addModuleToCourse(ctec3906);
		compSci.addModuleToCourse(ctec3911);
		compSci.addModuleToCourse(imat3410);
		compSci.addModuleToCourse(imat3406);
		compSci.addModuleToCourse(imat3611);
		compSci.addModuleToCourse(imat3613);
		compSci.addModuleToCourse(imat3614);
		compSci.addModuleToCourse(imat3428_CompSci);

		Course softEng = new Course("Software Engineering");
		softEng.addModuleToCourse(imat3423);
		softEng.addModuleToCourse(ctec3451);
		softEng.addModuleToCourse(ctec3902_SoftEng);
		softEng.addModuleToCourse(ctec3110);
		softEng.addModuleToCourse(ctec3605);
		softEng.addModuleToCourse(ctec3606);
		softEng.addModuleToCourse(ctec3410);
		softEng.addModuleToCourse(ctec3904);
		softEng.addModuleToCourse(ctec3905);
		softEng.addModuleToCourse(ctec3906);
		softEng.addModuleToCourse(ctec3911);
		softEng.addModuleToCourse(imat3410);
		softEng.addModuleToCourse(imat3406);
		softEng.addModuleToCourse(imat3611);
		softEng.addModuleToCourse(imat3613);
		softEng.addModuleToCourse(imat3614);

		Course[] courses = new Course[2];
		courses[0] = compSci;
		courses[1] = softEng;

		return courses;
	}

	private void populateModuleViews() {
		smp.clearAll();

		for (Module module : cspp.getSelectedCourse().getAllModulesOnCourse()) {
			if (module.getDelivery() == RunPlan.TERM_1) {
				if (module.isMandatory()) {
					smp.getTerm1Selected().addModule(module);
					smp.getTerm1Credits().incrCredits(module.getModuleCredits());
				} else {
					smp.getTerm1Unselected().addModule(module);
				}
			} else if (module.getDelivery() == RunPlan.TERM_2) {
				if (module.isMandatory()) {
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

	private void alertDialogBuilder(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
