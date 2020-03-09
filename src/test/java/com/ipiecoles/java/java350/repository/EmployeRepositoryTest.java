package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

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
        emp.setMatricule("M22222");
        employeRepository.save(emp);
        // when
        String result = employeRepository.findLastMatricule();
        // then
        String lastMatricule = emp.getMatricule().substring(1);
        Assertions.assertThat(result).isEqualTo(lastMatricule);
    }

    @Test
    public void testFindLastMatricule2Employes() { //le plus grand nombre des matricules
        // given
        Employe emp1 = employeRepository.save(new Employe("Doe", "John", "M56789",
                LocalDate.now(), 1500d, 1, 1.0));
        Employe emp2 = employeRepository.save(new Employe("Doe", "Toto", "T01234",
                LocalDate.now(), 1500d, 1, 1.0));

        // when
        String lastMatricule = employeRepository.findLastMatricule();

        // then
        Assertions.assertThat(lastMatricule).isEqualTo("56789");
    }
}
