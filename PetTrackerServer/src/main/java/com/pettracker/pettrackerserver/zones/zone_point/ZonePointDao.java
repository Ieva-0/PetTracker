package com.pettracker.pettrackerserver.zones.zone_point;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

@Service
public class ZonePointDao {
	@Autowired
	private ZonePointRepository repository;
	
	public List<ZonePoint> getAllZonePoints(Long zone_id) {
		List<ZonePoint> zonePoints = new ArrayList<>();
		Streamable.of(repository.getAllZonePoints(zone_id)).forEach(zonePoints::add);
		return zonePoints;
	}
	
	public List<ZonePoint> saveAllZonePoints(List<ZonePoint> points) {
		Iterable<ZonePoint> zonePoints = repository.saveAll(points);
		System.out.println(zonePoints);

		List<ZonePoint> resultPoints = new ArrayList<>();
//		resultPoints.add(repository.save(points.get(0)));
		Streamable.of(zonePoints).forEach(resultPoints::add);
		System.out.println(resultPoints.get(resultPoints.size()-1));
		return resultPoints;
	}
	public void deleteZonePoints(Long zone_id) {
		repository.deleteByZone(zone_id);
	}

}
