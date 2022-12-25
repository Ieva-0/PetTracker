package com.pettracker.PetTrackerServer;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pettracker.pettrackerserver.model.employee.Employee;
import com.pettracker.pettrackerserver.model.employee.EmployeeDao;

@SpringBootTest
class PetTrackerServerApplicationTests {
	
	@Autowired
	private EmployeeDao employeedao;
	
//	@Test
	void addEmployeeTest() {
		Employee employee = new Employee();
		employee.setName("newname");
		employee.setBranch("uwu");
		employee.setLocation("uwu2electricboogaloo");
		employeedao.save(employee);
	}
	
//	@Test
	void getAllEmployees() {
		List<Employee> employees = employeedao.getAllEmployees();
		System.out.println(employees);
	}

}
