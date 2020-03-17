package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @Mock
    EmployeRepository employeRepository;

    @InjectMocks
    EmployeService employeService;

    @Test
    public void testEmbaucheEmployePleinTempsBTS() throws EmployeException {
        // given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("00345");
        Mockito.when(employeRepository.findByMatricule("C00346")).thenReturn(null);

        // when
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        // then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C00346");
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(
                employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        ).isEqualTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(tempsPartiel);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);

    }

    @Test
    public void testEmbaucheEmployeLimiteMatricule() {
        // given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");

        // when
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("Le test doit échouer");
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
        }
    }

    /**
     * Les cas de tests sont dans l'ordre du csv ( cas 1, cas 2 ... cas 5)
     * Cas 1 : le commercial retombe à la performance de base ( +1 pour performance moyenne )
     * Cas 2 : CA inférieur entre 20% et 5% par rapport à l'ojectif, il perd 2 de performance
     * Cas 3 : Si le chiffre d'affaire est entre -5% et +5% de l'objectif fixé, la performance reste la même.
     * Cas 4 : Si le chiffre d'affaire est supérieur entre 5 et 20%, il gagne 1 de performance
     * Cas 5 : Si le chiffre d'affaire est supérieur de plus de 20%, il gagne 4 de performance
     * Cas 6 : Perf supèrieure à la moyenne : perf + 1
     */
    @ParameterizedTest
    @CsvSource({
            "100000, 200000, 20, 1",
            "90000, 100000, 10, 8",
            "100000 , 100000, 10, 10",
            "100000, 90000, 8, 9",
            "200000, 100000, 5, 9",
            "100000, 100000, 60, 61"

    })
    public void testCalculPerformanceCommercialParametre(
            Long caTraite, Long objectifCa, Integer intialPerf, Integer finalPerf
    ) throws EmployeException {
        // given
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(50.0);
        Employe emp = new Employe();
        String matricule = "C00009";
        emp.setMatricule(matricule);
        emp.setPerformance(intialPerf);
        Mockito.when(employeRepository.findByMatricule("C00009")).thenReturn(emp);

        // when
        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        // then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getPerformance()).isEqualTo(finalPerf);
    }

    /**
     * Cas 7 : caTraite négatif
     * Cas 8 : objectifCa négatif
     * Cas 9 : matricule ne commence pas la lettre "C"
     */
    @ParameterizedTest
    @CsvSource({
            "-100000, 100000, 'C00001'",
            "'100000', -100000, 'C00001'",
            "100000, 100000, 'T00001'"
    })
    public void testCalculPerformanceCommercialBadMatricule(
            Long caTraite, Long objectifCa, String matricule
    ) {
        // given - when
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
        }
    }

    /**
     * Cas 10 : Recherche de l'employé dans la base quand Emp == null
     */
    @Test
    public void testCalculPerformanceCommercialEmpIsNull() {
        // given - when
        try {
            employeService.calculPerformanceCommercial("C99999", 0L, 0L);
        } catch (Exception e) {
            // then
            Assertions.assertThat(e).isInstanceOf(EmployeException.class);
        }
    }
}
