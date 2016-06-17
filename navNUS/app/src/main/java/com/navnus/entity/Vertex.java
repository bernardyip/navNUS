package com.navnus.entity;

import java.io.Serializable;

public class Vertex implements Serializable {
	public int id;
	public String name;
	public GeoCoordinate coordinate;
	
	public Vertex(int id, String name, GeoCoordinate coordinate) {
		super();
		this.id = id;
		this.name = name;
		this.coordinate = coordinate;
	}
	
	public Vertex(String dataString) {
		String[] data = dataString.split(";");
		id = Integer.parseInt(data[0]);
		name = data[1];
		coordinate = new GeoCoordinate(Double.parseDouble(data[2]), Double.parseDouble(data[3]));
	}
	
	public String toString() {
		//return id + " : " + name + " (" + coordinate.latitude + ", " + coordinate.longitude + ")";
		return id + ";" + name + ";" + coordinate.latitude + ";" + coordinate.longitude;
	}
}