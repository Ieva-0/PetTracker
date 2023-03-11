package com.pettracker.pettrackerserver.model.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pettracker.pettrackerserver.model.zone.Zone;
import com.pettracker.pettrackerserver.model.zone.ZoneDao;
import com.pettracker.pettrackerserver.model.zone_point.ZonePoint;
import com.pettracker.pettrackerserver.model.zone_point.ZonePointDao;
@RestController
@RequestMapping("zone_points")
public class ZonePointController {
	
	@Autowired
	private ZonePointDao zonepointdao;
	
	@GetMapping("zone")
	public List<ZonePoint> getAllZonePoints(@RequestParam Long zone_id) {
		return zonepointdao.getAllZonePoints(zone_id);
	}
	
	@PostMapping("create")
	public List<ZonePoint> addZonePoints(@RequestBody List<ZonePoint> points) {
		System.out.println(points.get(points.size()-1));

		return zonepointdao.saveAllZonePoints(points);
	}

} 
