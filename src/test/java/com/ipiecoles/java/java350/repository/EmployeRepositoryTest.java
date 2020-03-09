package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmployeRepositoryTest {

    @Autowired
    EmployeRepository employeRepository;

    @Test
    public void testFindByMatricule() {
        // given
        Employe emp = new Employe();
        emp.setMatricule("M12345");
        employeRepository.save(emp);

        // when
        Employe result = employeRepository.findByMatricule("M12345");
        // then
        Assertions.assertThat(result).isEqualTo(emp);
    }

    @Test
    public void testFindLastMatricule() {
        // given
        Employe emp = new Employe();
        emp.setMatricule("M99999");
        employeRepository.save(emp);
        // when
        String result = employeRepository.findLastMatricule();
        // then
        String empMatricule = emp.getMatricule().substring(1);
        Assertions.assertThat(result).isEqualTo(empMatricule);
    }
}
