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
    public void testEmbaucheEmployeLimiteMatricule() throws EmployeException {
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
     * Cas 1 : le commercial retombe à la performance de base ( +1 pour performance moyenne )
     */
    @Test
    public void testCalculPerformanceCommercialCas1() {
        // given
        Employe emp = new Employe();
        emp.setMatricule("C00001");
        emp.setPerformance(10);
        Mockito.when(employeRepository.findByMatricule("C00001")).thenReturn(emp);
        String matricule = "C00001";
        Long caTraite = 100000L;
        Long objectifCa = 200000L;

        // when
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
        } catch (EmployeException e) {
            System.out.println("########### ERREUR ###########");
        }

        // then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE + 1);
    }

    /**
     * Cas 2 : CA inférieur entre 20% et 5% par rapport à l'ojectif, il perd 2 de performance
     */
    @Test
    public void testCalculPerformanceCommercialCas2() {
        // given
        /*Employe[] comTabBestPerf = new Employe[3];
        for (int i = 0; i < 3; i++) {
            comTabBestPerf[i] = new Employe();
            comTabBestPerf[i].setMatricule("C0000" + i);
            comTabBestPerf[i].setPerformance(99);
            Mockito.when(employeRepository.findByMatricule("C0000" + i)).thenReturn(comTabBestPerf[i]);
        }*/
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(50.0);
        Employe emp = new Employe();
        emp.setMatricule("C00009");
        emp.setPerformance(10); // doit être égale à 8
        Mockito.when(employeRepository.findByMatricule("C00009")).thenReturn(emp);
        String matricule = "C00009";
        Long caTraite = 90000L;
        Long objectifCa = 100000L;

        // when
        try {
            employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);
        } catch (EmployeException e) {
            System.out.println("########### ERREUR ###########");
        }

        // then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, Mockito.times(1)).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getPerformance()).isEqualTo(8);
    }
}
