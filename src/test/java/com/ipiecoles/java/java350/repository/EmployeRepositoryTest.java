package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class EmployeRepositoryTest {

    @Autowired
    EmployeRepository employeRepository;

    @BeforeEach
    public void setUpDB() {
        employeRepository.deleteAll();
    }

    @Test
    public void testFindNoMatricule() {
        String lastMatricule = employeRepository.findLastMatricule();
        Assertions.assertThat(lastMatricule).isNull();
    }

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
    public void testFindLast1Matricule() {
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

    /**
     * Test d'intégration de la moyenne de 0 commercial
     */
    @Test
    public void testAvgPerformanceWhereMatriculeStartsWithNoCommercial() {
        // given when then
        Assertions.assertThat(
                employeRepository.avgPerformanceWhereMatriculeStartsWith("C")
        ).isNull();
    }

    /**
     * Test d'intégration de la moyenne de 3 commerciaux
     */
    @Test
    public void testAvgPerformanceWhereMatriculeStartsWith3Commerciaux() {
        // given
        Employe[] comTab = new Employe[3];
        for (int i = 0; i < 3; i++) {
            comTab[i] = new Employe();
            comTab[i].setMatricule("C0000" + i);
            comTab[i].setPerformance(2 + 4*i); // perf variable
            employeRepository.save(comTab[i]);
        }

        // when
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        // then
        Assertions.assertThat(moyenne).isEqualTo(6);
    }

    /**
     * Test d'intégration de la moyenne de 2 techniciens
     */
    @Test
    public void testAvgPerformanceWhereMatriculeStartsWith2Techniciens() {
        // given
        Employe tech1 = new Employe();
        Employe tech2 = new Employe();
        tech1.setMatricule("T00001");
        tech2.setMatricule("T00002");
        tech1.setPerformance(10);
        tech2.setPerformance(20);
        employeRepository.save(tech1);
        employeRepository.save(tech2);

        // when
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("T");

        // then
        Assertions.assertThat(moyenne).isEqualTo(15);
    }

    /**
     * Test d'intégration de la moyenne de 1 manager
     */
    @Test
    public void testAvgPerformanceWhereMatriculeStartsWith1Manager() {
        // given
        Employe man = new Employe();
        man.setMatricule("M00001");
        man.setPerformance(10);
        employeRepository.save(man);

        // when
        Double moyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");

        // then
        Assertions.assertThat(moyenne).isEqualTo(Double.valueOf(man.getPerformance()));
    }


}
