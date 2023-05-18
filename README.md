# JavaFX Final Year Module Selection Tool
This repository contains the coursework for the JavaFX Final Year Module Selection Tool, which is a project for a university module. This coursework was created solely by myself, [Wil K Edwards](https://github.com/.EdwardsWK). At this time, the coursework is still being edited and enhanced

The objective of this assessment is to design and implement an object-oriented system using Java classes and advanced libraries within the Java SDK. The project involves building a graphical user interface (GUI) that allows second-year undergraduate computing students to select their final year module options based on their chosen course of study.

## Objectives
The objectives of this project are as follows:
1. Design and implement an object-oriented system using Java classes and advanced libraries within the Java SDK.
2. Study and utilize a prebuilt student profile data model effectively.
3. Build a user interface using JavaFX libraries that is suitable for interactive and user friendly interactions.
4. Implement event handling procedures to create an interactive system.
5. Adhere to the standard principles of the Model-View-Controller (MVC) design pattern, ensuring proper abstraction and encapsulation through class decomposition.

## Specifications
The JavaFX Final Year Module Selection Tool allows second-year undergraduate computing students to select their final year module options. The tool includes the following features:

- Students can create a profile and input their details such as P number, name, email, and submission date.
- The GUI provides a combo box with two computing courses (Computer Science and Software Engineering).
- Students can select modules based on their chosen course, including compulsory and optional modules running in term 1, term 2, or throughout the year.
- Validation ensures that only a valid combination of modules can be selected.
- Students must select a total of 120 credits, with a maximum of 60 credits per term.
- The tool allows students to select reserve modules for each term from the remaining unselected modules.
- An overview tab provides a summary of the student's profile, selected modules, and reserved modules.
- The application includes functionality for saving and loading student profiles and module selections.

# GUI Details
- The GUI is divided into four forms displayed on separate tabs:
  - Create Profile: Allows students to enter their personal details and create a profile.
  - Select Modules: Displays unselected and selected modules for term 1 and term 2, including compulsory modules. Provides add and remove buttons for module selection.
  - Reserve Modules: Allows students to choose reserve modules for term 1 and term 2. Provides add and remove buttons for reserve module selection.
  - Overview Selection: Shows an overview of student details, selected modules, and reserved modules based on the submitted profile and module selection.
- The GUI includes a menu bar with options for loading and saving student data, as well as providing help and information about the application.

## License
This project is licensed under the GNU General Public License v3.0 (GPL-3.0). Please see the [LICENSE](LICENCE.md] file for more details.
