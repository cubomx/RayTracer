package edu.up.isgc.raytracer.tools;

import java.awt.*;

public class Material {
    private float shininess;
    private float specular;
    private float ambient;
    private float diffuse;
    private float reflection;
    private float refraction;
    private Color color;

    
   

    public Material(float shininess, float specular, float ambient, float diffuse, float reflection, float refraction,
			Color color) {
		super();
		this.shininess = shininess;
		this.specular = specular;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.reflection = reflection;
		this.refraction = refraction;
		this.color = color;
	}

	public float getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float diffuse) {
        this.diffuse = diffuse;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public float getSpecular() {
        return specular;
    }

    public void setSpecular(float specular) {
        this.specular = specular;
    }

    public float getAmbient() {
        return ambient;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

	public float getReflection() {
		return reflection;
	}

	public void setReflection(float reflection) {
		this.reflection = reflection;
	}

	public float getRefraction() {
		return refraction;
	}

	public void setRefraction(float refraction) {
		this.refraction = refraction;
	}
}
