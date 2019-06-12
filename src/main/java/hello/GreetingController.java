package hello;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
    
    
    @RequestMapping("/getEmp")
    public EmployeeDTO getEmploye(@RequestParam(value="empId", defaultValue="World") String empId) {
    	
    	EmployeeDTO employeeDTO = new EmployeeDTO();
    	employeeDTO.setEmpId(empId);
    	employeeDTO.setEmpName("Mahesh");
    	employeeDTO.setLocation("GA");
    	employeeDTO.setDob("10/10/1010");
    	
        return employeeDTO;
    }
}
