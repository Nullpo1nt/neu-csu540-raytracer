CS U540 Raytracer
	Author:  Bryant Martin
	Date:    04/12/2007

	* Requires Java 5 or later. (Tested with 1.5.0_10-b03)
	* Requires Java 3D API (vecmath.jar)
	* Developed/Tested with Eclipse 3.2.1 on a Gentoo Linux system

A simple raytracer application.

How to compile:
	Load source code into an Eclipse project.

	In Eclipse:
		1) Create a new Java project and name it "martinBryant.sampler"
		2) Eclipse should automatically create the project after this
		3) Ensure vecmath.jar is in the class path.
		4) Simply run the application (the main method is located in 
		   Main.java)

Running the application:
	The application is GUI based.  Once running you should be presented with a
	couple menus and a large red X.
	
	Load a scene using the File menu and then select render.  Only press render 
	once as it starts another thread to handle rendering and clicking it two or
	more times will start multiple rendering jobs with an unknown outcome.  Once
	the rendering is complete the results will be displayed imediately.
	
	Scenes for all of the examples provided at the webpage "Building a Ray 
	Tracer - Page One" (http://www.ccs.neu.edu/home/fell/COM3370/RayTrace1.html)
	are provided as "scene-test*.txt".
	
	* All provided scenes are in the "Scenes" folder.
	
	The console window will provide status of a rendering and scene.
	
	Menus:
		File:
			Load scene, Reload scene (usefull if you are editing a scene), exit.
		Settings:
			Subsampling and recursion settings.
			
		Render - Push to render the currently loaded scene.

Some Useful starting points:
	Main.java: Contains the main method and windowing functions
	Raytracer.java: Main raytracing functionality.
	surface/Sphere.java: A Sphere object.

Possible Problems:
	-The parser is not very robust and very little testing on it was done.  As
	 long as the format used in any of the provided scene isn't deviated from
	 there should be no problems.
