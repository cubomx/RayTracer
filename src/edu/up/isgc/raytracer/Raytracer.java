package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.objects.Sphere;
import edu.up.isgc.raytracer.tools.Material;
import edu.up.isgc.raytracer.tools.OBJReader;
import edu.up.isgc.raytracer.lights.DirectionalLight;
import edu.up.isgc.raytracer.lights.Light;
import edu.up.isgc.raytracer.lights.PointLight;
import edu.up.isgc.raytracer.objects.Camera;
import edu.up.isgc.raytracer.objects.Object3D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class Raytracer {

	/** Template of the scene
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.out.println(new Date());
		Scene scene01 = new Scene();
		Material teapotMat =  new Material(32f, 2.1f, 0.2f, 1f, 0f, 0, Color.blue);
		Material column =  new Material(32f, 2.1f, 0.2f, 1f, 0f, 0, Color.cyan);
		Material groundMat = new Material(32f, 3.1f, 0.2f, 1f, .0f, 0, Color.GRAY);
		Material reflectMat = new Material(32f, 2.1f, 0.35f, 1f, 1f, 0,  new Color(5, 5, 5));
		Material lightMat = new Material(32f, 3.1f, 0.2f, 1, 0f, 0, Color.WHITE);
		Material dirLightMat = new Material(4f, 1, 0.2f, 1f, 0, 0, Color.RED);
		Material refractMat = new Material(4f, 1f, 0.2f, 1, 0f, 1.33f, new Color(50, 50, 50));
		scene01.setCamera(new Camera(new Vector3D(0, 0, -8), 160, 160, 1200, 1200, 8.2f, 50f));
		//scene01.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(0.0, 0.0, 1.0), lightMat, 1.1));
		//scene01.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(0.0, -1.0, 0.0), lightMat, 1.1));
		scene01.addLight(new PointLight(new Vector3D(0f, 2.2f, 1.2f), lightMat, 50));
		scene01.addObject(OBJReader.GetPolygon("ground.obj", new Vector3D(0f, -3.0f, 0f), groundMat));
		//scene01.addObject(new Sphere(new Vector3D(-.3f, -1.0f, 3.3f), 0.9f, reflectMat));
		//scene01.addObject(new Sphere(new Vector3D(1.1, 1.0, 3.5), 1.3f, reflectMat));
		//scene01.addObject(new Sphere(new Vector3D(-2.5, -1.0, 1.5), 0.9f, reflectMat));
		scene01.addObject(new Sphere(new Vector3D(-1.5, 0.0, 4.5), 0.25f, teapotMat));
		scene01.addObject(new Sphere(new Vector3D(2.35, 0.0, 3.5), 0.3f, teapotMat));
		scene01.addObject(new Sphere(new Vector3D(4.85, 1.0, 4.5), 0.3f, teapotMat));
		scene01.addObject(OBJReader.GetPolygon("smallTeapot.obj", new Vector3D(2f, 0.3f, 5.3f), teapotMat));
		scene01.addObject(OBJReader.GetPolygon("Cube.obj", new Vector3D(0f, -1.5f, 2.5f), refractMat));
		scene01.addObject(new Sphere(new Vector3D(2.85, 1.0, 304.5f), 0.5f, teapotMat));
		scene01.addObject(OBJReader.GetPolygon("smallTeapot.obj", new Vector3D(-1f, -2.3f, 4.9f), teapotMat));
		//scene01.addObject(new Sphere(new Vector3D(0f, -1.0f, 4.5f), 1f, refractMat));
		
		BufferedImage image = raytrace(scene01);
		File outputImage = new File("refract2.png");
		try {
			ImageIO.write(image, "png", outputImage);
		} catch (IOException ex) {
			System.out.println("Something failed");
		}

		System.out.println(new Date());
	}
	
	/**
	 * Sending rays to each of the object and see if is the way of the direction.
	 * @param ray
	 * @param objects
	 * @param caster
	 * @param clippingPlanes
	 * @return {@link Intersection}
	 */

	public static Intersection raycast(Ray ray, ArrayList<Object3D> objects, Object3D caster, float[] clippingPlanes) {
		Intersection closestIntersection = null;
		for (int k = 0; k < objects.size(); k++) {
			Object3D currentObj = objects.get(k);
			if (caster == null || !currentObj.equals(caster)) {
				Intersection intersection = currentObj.getIntersection(ray);
				if (intersection != null) {
					double distance = intersection.getDistance();
					if (distance >= 0 && (closestIntersection == null || distance < closestIntersection.getDistance()) && (clippingPlanes == null
							|| (intersection.getPosition().getZ() >= clippingPlanes[0] && intersection.getPosition().getZ() <= clippingPlanes[1]))) {
						closestIntersection = intersection;
					}
				}
			}
		}
		return closestIntersection;
	}
	
	/**
	 * Getting the frame of the scene established in the main function
	 * @param scene
	 * @return {@link BufferedImage}
	 */

	public static BufferedImage raytrace(Scene scene) {
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		Camera mainCamera = scene.getCamera();
		ArrayList<Light> lights = scene.getLights();
		float[] nearFarPlanes = mainCamera.getNearFarPlanes();
		BufferedImage image = new BufferedImage(mainCamera.getResolution()[0], mainCamera.getResolution()[1], BufferedImage.TYPE_INT_RGB);
		ArrayList<Object3D> objects = scene.getObjects();

		Vector3D[][] positionsToRaytrace = mainCamera.calculatePositionsToRay();
		for (int i = 0; i < positionsToRaytrace.length; i++) {
			for (int j = 0; j < positionsToRaytrace[i].length; j++) {
				double x = positionsToRaytrace[i][j].getX() + mainCamera.getPosition().getX();
				double y = positionsToRaytrace[i][j].getY() + mainCamera.getPosition().getY();
				double z = positionsToRaytrace[i][j].getZ() + mainCamera.getPosition().getZ();
				Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));
				Runnable runnable = getPixelColor(ray, i, j, mainCamera, objects, nearFarPlanes, lights, image);
				executorService.execute(runnable);
			}
		}
		executorService.shutdown();
		try {
			if(!executorService.awaitTermination(10, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
			}
		} catch(InterruptedException e) {
			executorService.shutdownNow();
		} finally {
			if(!executorService.isTerminated()) {
				System.err.println("cancel non-finished");
			}
		}
		executorService.shutdownNow();
		return image;
	}
	
	/**
	 * Making a ray from one point to another and then see if there's a object in between.
	 * @param mainCamera
	 * @param nearFarPlanes
	 * @param objects
	 * @param direction
	 * @param start
	 * @return {@link Intersection}
	 */

    private static Intersection getHit(Camera mainCamera, float[] nearFarPlanes, ArrayList<Object3D> objects, Vector3D direction, Vector3D start) {
        Ray ray = new Ray(start, direction);
        Intersection hit = raycast(ray, objects, null, new float[] {(float) mainCamera.getPosition().getZ() + nearFarPlanes[0], (float) mainCamera.getPosition().getZ() + nearFarPlanes[1]} );
        return hit;
	}
    
    /**
     * Get the sum of the specular, diffuse and ambient colors of a specific place in the scene
     * @param intersection
     * @param light
     * @param pixelColor
     * @param camera
     * @param objects
     * @param nearFarPlanes
     * @return {@link Color}
     */

	private static Color getObjectColor (Intersection intersection, Light light, Color pixelColor, Camera camera, ArrayList<Object3D> objects, float [] nearFarPlanes){
        double distance =  Vector3D.magnitude(Vector3D.substract(intersection.getPosition(), light.getPosition()));
        Object3D obj = intersection.getObject();
        float intensity = getIntensity(intersection, light);
        float [] colors = getAmbient(obj);
        Color ambient = new Color(clamp(colors[0], 0, 1), clamp(colors[1], 0, 1), clamp(colors[2], 0, 1));
        pixelColor = addColor(pixelColor, ambient);

        if (light instanceof DirectionalLight) distance = 1;
        colors = getDiffuse(colors, intensity, distance, light);
        distance =  Vector3D.magnitude(Vector3D.substract(intersection.getPosition(), light.getPosition()));
        Color diffuse = new Color(clamp(colors[0], 0, 1), clamp(colors[1], 0, 1), clamp(colors[2], 0, 1));
    	Color specular = specular(light, intersection, colors, obj);
        Intersection lightInt = getHit(camera, nearFarPlanes, objects, intersection.getPosition(), light.getPosition());
        
        if (lightInt != null){
            double distObj = Vector3D.magnitude(Vector3D.substract(lightInt.getPosition(), light.getPosition()));
            if (lightInt.getObject() == intersection.getObject()) {
            	pixelColor = addColor(pixelColor, diffuse);
            	pixelColor = addColor(pixelColor, specular );
            }
            else if (distance < distObj) {
            	pixelColor = addColor(pixelColor, diffuse);
            	pixelColor = addColor(pixelColor, specular );
            }
        }
        else {
        	pixelColor = addColor(pixelColor, diffuse);
        	pixelColor = addColor(pixelColor, specular );
        }
        return pixelColor;
    }

	/**
	 * Get the amount of intensity depending of the intersection and the light
	 * @param inter
	 * @param light
	 * @return {@link Float}
	 */
	
	private static float getIntensity(Intersection inter, Light light){
		return (float) light.getIntensity() * light.getNDotL(inter);
	}

	/**
	 * Get the specular color and give it brigthness to the object
	 * @param light
	 * @param intersection
	 * @param colors
	 * @param obj
	 * @return {@link Color}
	 */
	
	private static Color specular (Light light, Intersection intersection, float [] colors, Object3D obj){
		Vector3D h = Vector3D.add(light.getPosition(), intersection.getPosition());
		h = Vector3D.normalize(h);
		//Vector3D h = Vector3D.scalarMultiplication(l_v, 1.0f / Vector3D.magnitude(l_v));
		float specular = (float) Math.pow(Vector3D.dotProduct(intersection.getNormal(), h), obj.getMat().getShininess());
		for (int color = 0; color < 3; color++) colors[color] *= specular * obj.getMat().getSpecular();
		Color specular_ = new Color(clamp(colors[0], 0, 1), clamp(colors[1], 0, 1), clamp(colors[2], 0, 1));
		return specular_;
	}

	/**
	 * Get the minimum color to show on the scene
	 * @param obj
	 * @return {@link Float}
	 */
	
	private static float [] getAmbient(Object3D obj){
		return new float[] { (obj.getMat().getColor().getRed()/ 255.0f* obj.getMat().getAmbient()),
				(obj.getMat().getColor().getGreen()/ 255.0f * obj.getMat().getAmbient()),
				(obj.getMat().getColor().getBlue()/ 255.0f* obj.getMat().getAmbient())};
	}
	
	/**
	 * Making the operations to get the color of the objects over the ambient color
	 * @param colors
	 * @param intensity
	 * @param distance
	 * @param light
	 * @return {@link Float}
	 */

	private static float [] getDiffuse(float [] colors, float intensity, double distance, Light light){
		colors[0] *= (intensity/Math.pow(distance, 2)) * (light.getMat().getColor().getRed() / 255.0f) ;
		colors[1] *= (intensity/Math.pow(distance, 2)) * (light.getMat().getColor().getGreen() / 255.0f);
		colors[2] *= (intensity/Math.pow(distance, 2)) * (light.getMat().getColor().getBlue() / 255.0f);
		return colors;
	}
	
	/**
	 * Get the reflection vector and tracing a new ray in search of something to reflect and get its color
	 * @param intersection
	 * @param light
	 * @param objects
	 * @param camera
	 * @param nearFarPlanes
	 * @return {@link Color}
	 */

	private static Color calculateReflection (Intersection intersection, Light light, ArrayList<Object3D>  objects, Camera camera, float [] nearFarPlanes){
		Vector3D incidenRay = Vector3D.substract(intersection.getPosition(), camera.getPosition());
		double normalRay = -2.0 * Vector3D.dotProduct(intersection.getNormal(), incidenRay);
		Vector3D reflection = Vector3D.scalarMultiplication(intersection.getNormal(), normalRay);
		Color reflectColor = Color.BLACK;
		reflection = Vector3D.add(incidenRay, reflection);

		Ray reflectHit = new Ray(intersection.getPosition(), reflection);
		Intersection reflectIntersect = raycast(reflectHit, objects, null, new float[] {(float) camera.getPosition().getZ() + nearFarPlanes[0]-8, (float) camera.getPosition().getZ() + nearFarPlanes[1]-8});
		if (reflectIntersect != null && intersection.getObject() != reflectIntersect.getObject()){
			reflectColor = getObjectColor(reflectIntersect, light, reflectColor, camera, objects, new float []{nearFarPlanes[0] - 8, nearFarPlanes[1] -8});
			if (reflectIntersect.getObject().getMat().getRefraction() != 0) {
				reflectColor = addColor(reflectColor, calculateRefraction(reflectIntersect, light, objects, camera, nearFarPlanes));
			}
			else if (reflectIntersect.getObject().getMat().getReflection() != 0) {
				reflectColor = addColor(reflectColor, calculateReflection(reflectIntersect, light, objects, camera, nearFarPlanes));
			}
		}
		return reflectColor;
	}
	
	/**
	 * Get the refraction vector and tracing a new ray in search of something to refract
	 * @param intersection
	 * @param light
	 * @param objects
	 * @param camera
	 * @param nearFarPlanes
	 * @return {@link Color}
	 */

	private static Color calculateRefraction (Intersection intersection, Light light, ArrayList<Object3D> objects, Camera camera, float [] nearFarPlanes){
		Object3D obj = intersection.getObject();
		Vector3D position = intersection.getPosition();
		
		Vector3D normal = intersection.getNormal();
		Vector3D newPosition = Vector3D.scalarMultiplication(normal, 1.5);
		float nr = obj.getMat().getRefraction();
		float reflectance = (float) (Math.pow(nr-1, 2) / Math.pow(nr+1, 2));
		
		if (reflectance > 0 ) {
			position = Vector3D.add(position, newPosition);
		}
		else {
			position = Vector3D.substract(position, newPosition);
		}
		
		Color refractColor = Color.BLACK;
		
		Vector3D incidenRay = Vector3D.substract(position, camera.getPosition()); //view vector
		double cos_theta = clamp(-1.0f, 1.0f,(float) Vector3D.dotProduct(incidenRay, normal));
		
		Vector3D t = Vector3D.ZERO();

		double c2 = Math.sqrt(1 - Math.pow(nr, 2)* (1 - Math.pow(cos_theta, 2)));
		double c1 = (nr)*cos_theta;
		if (c2 > 0) {
			t = Vector3D.add(Vector3D.scalarMultiplication(incidenRay, nr), Vector3D.scalarMultiplication(normal, c1-c2 ));
		}
		
		Intersection refractIntersect = null;
		Ray reflectHit = new Ray(position, t);
		if (intersection.getGroup() != null) {
			refractIntersect = raycast(reflectHit, objects, intersection.getGroup(), new float[] {(float) camera.getPosition().getZ() + nearFarPlanes[0]-8, (float) camera.getPosition().getZ() + nearFarPlanes[1]-8});
		}
		else {
			refractIntersect = raycast(reflectHit, objects, intersection.getObject(), new float[] {(float) camera.getPosition().getZ() + nearFarPlanes[0]-8, (float) camera.getPosition().getZ() + nearFarPlanes[1]-8});
		}

		if (refractIntersect != null && refractIntersect.getObject().getMat().getRefraction() == 0f){
			refractColor = getObjectColor(refractIntersect, light, refractColor, camera, objects, new float []{nearFarPlanes[0]-3, nearFarPlanes[1] -3});
			if (refractIntersect.getObject().getMat().getReflection() > 0) {
				refractColor = addColor(refractColor, calculateReflection(refractIntersect, light, objects, camera, nearFarPlanes));
			}
		}
		
		return refractColor;
	}
	
	/**
	 * Keep a value between two extreme points
	 * @param value
	 * @param min
	 * @param max
	 * @return {@link Float}
	 */

	public static float clamp(float value, float min, float max) {
		if (value < min) {
			return min;
		}

		if (value > max) {
			return max;
		}

		return value;
	}
	
	/**
	 * Adding to the original color a other color and preventing to getting out of range
	 * @param original
	 * @param otherColor
	 * @return {@link Color}
	 */

	public static Color addColor(Color original, Color otherColor) {
		float red = clamp((original.getRed() / 255.0f) + (otherColor.getRed() / 255.0f), 0, 1);
		float green = clamp((original.getGreen() / 255.0f) + (otherColor.getGreen() / 255.0f), 0, 1);
		float blue = clamp((original.getBlue() / 255.0f) + (otherColor.getBlue() / 255.0f), 0, 1);
		return new Color(red, green, blue);
	}
	
	/**
	 * Tracing rays to get the color of every pixel of the scene
	 * @param ray
	 * @param i
	 * @param j
	 * @param mainCamera
	 * @param objects
	 * @param nearFarPlanes
	 * @param lights
	 * @param image
	 * @return {@link Runnable}
	 */
	
	public static Runnable getPixelColor(Ray ray, int i, int j, Camera mainCamera, ArrayList<Object3D> objects, float [] nearFarPlanes, ArrayList<Light> lights, BufferedImage image) {
		Runnable aRunnable = new Runnable() {
			public void run () {
				Intersection closestIntersection = raycast(ray, objects, null, new float[] {
						(float) mainCamera.getPosition().getZ() + nearFarPlanes[0], (float) mainCamera.getPosition().getZ() + nearFarPlanes[1] });
				Color pixelColor = Color.BLACK;
				if (closestIntersection != null) {
					pixelColor = Color.BLACK;
					for (Light light : lights) {
						pixelColor = addColor(pixelColor, getObjectColor(closestIntersection, light, pixelColor, mainCamera, objects, nearFarPlanes));
						if (closestIntersection.getObject().getMat().getReflection() > 0)
							pixelColor = addColor(pixelColor, calculateReflection(closestIntersection, light, objects, mainCamera, nearFarPlanes));
						if (closestIntersection.getObject().getMat().getRefraction() > 0){
							pixelColor = addColor(pixelColor, calculateRefraction(closestIntersection, light, objects, mainCamera, nearFarPlanes));
						}
					}
				}
				setRGB(image, i, j, pixelColor);
			}
		};
		return aRunnable;
	}
	
	/**
	 * Performing a lock to the access of the image to prevent any error
	 * @param image
	 * @param x
	 * @param y
	 * @param pixelColor
	 */
	
	public static synchronized void setRGB(BufferedImage image, int x, int y, Color pixelColor) {
		image.setRGB(x, y, pixelColor.getRGB());
	}
}