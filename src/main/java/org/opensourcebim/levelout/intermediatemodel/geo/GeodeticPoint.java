package org.opensourcebim.levelout.intermediatemodel.geo;

import java.io.Serializable;

public class GeodeticPoint implements Serializable {
	private static final long serialVersionUID = -5330066318904627149L;
	public double latitude;
	public double longitude;
	public double height;

	public GeodeticPoint(double latitude, double longitude, double height) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
	}

	int getUtmSegmentNumber() {
		return (int) ((Math.floor((longitude + 180) / 6) % 60) + 1);
	}

	double getUtmGridConvergence() {
		// at origin, this may still be off if the origin is far away from actual coordinates
		double longitudeDiff = longitude - (6 * (double) getUtmSegmentNumber() - 183);
		return Math.atan(Math.tan(Math.toRadians(longitudeDiff)) * Math.sin(Math.toRadians(latitude)));
	}

    public String getUtmEpsg() {
        // TODO : Account for special cases: Norway and Svalbard
        if (latitude <= -80 || latitude > 84) {
            throw new IllegalArgumentException("Latitude outside of valid range -80..84.");
        } else {
            String segment = String.format("%02d", getUtmSegmentNumber());
            return (latitude > 0 ? "326" : "327") + segment;
        }
    }
}
