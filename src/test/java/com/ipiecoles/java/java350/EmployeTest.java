package com.ipiecoles.java.java350;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    // dateEmbauche 2 ans avant ajd
    @Test
    public void testAncienneteDateEmbaucheNMoins2() {
        //given (création d'un employé avec 2 années d'ancienneté
        Employe emp = new Employe();
        emp.setDateEmbauche(LocalDate.now().minusYears(2));

        //when
        Integer emb = emp.getNombreAnneeAnciennete();

        //then
        Assertions.assertThat(emb).isEqualTo(2);
    }

    // dateEmbauche 2 ans dans le futur
    @Test
    public void testAncienneteDateEmbaucheNPlus2() {
        //given (création d'un employé avec 2 années d'ancienneté
        Employe emp = new Employe();
        emp.setDateEmbauche(LocalDate.now().plusWeeks(2));

        //when
        Integer emb = emp.getNombreAnneeAnciennete();

        //then
        Assertions.assertThat(emb).isEqualTo(0);
    }

    //dateEmbauche aujourd'hui
    @Test
    public void testAncienneteDateEmbaucheAujourdhui() {
        //given
        Employe emp = new Employe();
        emp.setDateEmbauche(LocalDate.now());

        //when
        Integer emb = emp.getNombreAnneeAnciennete();

        //then
        Assertions.assertThat(emb).isEqualTo(0);
    }

    //dateEmbauche indéfinie
    @Test
    public void testAncienneteDateEmbaucheIndefinie() {
        //given
        Employe emp = new Employe();
        emp.setDateEmbauche(null);

        //when
        Integer emb = emp.getNombreAnneeAnciennete();

        //then
        Assertions.assertThat(emb).isEqualTo(0);
    }

    //primeAnnuelle, différents scénarios
    // matricule, année/mois/jours d'embauche, temps partiel, performance, prime annuelle attendue
    @ParameterizedTest()
    @CsvSource({
            "'M11109', 0, 1.0, 1, 1700",
            "'M11109', 0, 0.5, 1, 850",
            "'T00000', 10, 1.0, 1, 2000",
            "'T00000', 10, 1.0, 3, 4300",
            "'C00002', 2, 1.0, 1, 1200"
    })
    public void testPrimeAnnuelle(
            String mat,
            Integer anciennete,
            Double tempsPartiel,
            Integer perf,
            Double primeAnnuelle) {
        //Given
        Employe emp = new Employe();
        emp.setMatricule(mat);
        emp.setDateEmbauche(LocalDate.now().minusYears(anciennete));
        emp.setTempsPartiel(tempsPartiel);
        emp.setPerformance(perf);
        //When , Then
        Assertions.assertThat(emp.getPrimeAnnuelle()).isEqualTo(primeAnnuelle);
    }
}
