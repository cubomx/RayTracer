/**
 *  2019 - Universidad Panamericana 
 *  All Rights Reserved
 */
package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.objects.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 *
 * @author Jafet
 */
public class Raytracer {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.out.println(new Date());
		ArrayList <String> data = new ArrayList<>();
		try {
			data = getData("smallTeapot.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene scene01 = new Scene();
		scene01.setCamera(new Camera(new Vector3D(0, 0, -8), 160, 160, 800, 800, 0f, 70));
		scene01.addObject(new Triangle(new Vector3D(20, 20, 50), new Vector3D(12, 15, 50), new Vector3D(28, 15, 50), Color.BLUE));
		scene01.addObject(new Sphere(new Vector3D(0.0f, 0f, 4.5f), 0.25f, Color.GREEN));
		scene01.addObject(new Sphere(new Vector3D(.5f, 0f, 4.5f), 0.3f, Color.ORANGE));
		scene01.addObject(OBJReader.createShape(data));

		BufferedImage image = raytrace(scene01);
		File outputImage = new File("image2.png");
		try {
			ImageIO.write(image, "png", outputImage);
		} catch (IOException ex) {
			System.out.println("Something failed");
		}

		System.out.println(new Date());
	}

	public static ArrayList<String> getData(String filename)throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(filename).getAbsolutePath()));
		String line = br.toString();
		ArrayList <String> file = new ArrayList<>();
		while ( (line = br.readLine()) != null){
			file.add(line);
		}
		return file;
	}

	public static Intersection raycast(Ray ray, ArrayList<Object3D> objects, Object3D caster, Camera cam) {
		Intersection closestIntersection = null;

		for (int k = 0; k < objects.size(); k++) {
			Object3D currentObj = objects.get(k);

			if (caster == null || !currentObj.equals(caster)) {
				Intersection intersection = currentObj.getIntersection(ray, cam);

				if (intersection != null) {
					double distance = intersection.getDistance();

					if (distance >= 0 && (closestIntersection == null || distance < closestIntersection.getDistance()) && intersection.getPosition().getZ() >= cam.getNear() && intersection.getPosition().getZ() <= cam.getFar()	) {

						closestIntersection = intersection;
					}
				}
			}
		}

		return closestIntersection;
	}

	public static BufferedImage raytrace(Scene scene) {
		Camera mainCamera = scene.getCamera();
		BufferedImage image = new BufferedImage(mainCamera.getResolution()[0], mainCamera.getResolution()[1], BufferedImage.TYPE_INT_RGB);
		ArrayList<Object3D> objects = scene.getObjects();

		Vector3D[][] positionsToRaytrace = mainCamera.calculatePositionsToRay();
		for (int i = 0; i < positionsToRaytrace.length; i++) {
			for (int j = 0; j < positionsToRaytrace[i].length; j++) {
				double x = positionsToRaytrace[i][j].getX() + mainCamera.getPosition().getX();
				double y = positionsToRaytrace[i][j].getY() + mainCamera.getPosition().getY();
				double z = positionsToRaytrace[i][j].getZ() + mainCamera.getPosition().getZ();
				Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));

				Intersection closestIntersection = raycast(ray, objects, null, scene.getCamera());

				// Background color
				Color pixelColor = Color.WHITE;
				if (closestIntersection != null) {
					pixelColor = closestIntersection.getObject().getColor();
				}
				image.setRGB(i, j, pixelColor.getRGB());
			}
		}

		return image;
	}

}
