package com.ipiecoles.java.java350.model;

import com.thoughtworks.gauge.Step;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


import java.time.LocalDate;

public class EmployeTest {


    @ParameterizedTest
    @CsvSource({
            ",10,6,2.5,100",
            "'M12345',10,,1.0,81",
            "'M67891',20,1,1.0,50",
            "'M23456',30,0,1.0,45",
            "'M78912',05,1,0.5,62",
            "'C12345',7,1,1.0,0",
            "'C67891',5,1,1.0,2",
            "'C23456',2,1,1.0,65",
            "'C78912',0,2,1.0,45",
            "'C34567',3,2,1.0,0",
            "'C89123',0,1,0.5,60",
            "'C45678',0,1,1.0,0"

    })
    public void testGetNbRtt(
            String matricule,
            Integer nbAnneesAnciennete,
            Integer performance,
            Double tauxActivite,
            Double prime
    ){
        //Given
        Employe employe = new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(nbAnneesAnciennete), 2500d, performance, tauxActivite);

        //When
        Double primeObtenue = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeObtenue).isEqualTo(prime);
    }


    @Test

    public void testAugmenterSalaireNan(){
        //Given
        Employe employe = new Employe();
        employe.setSalaire(2000d);
        //When
        Double augmenterSalaire = employe.augmenterSalaire(Double.NaN);
        System.out.println(augmenterSalaire.toString());
        //Then

        Assertions.assertThatIllegalArgumentException();
    }

    @Test
    public void testAugmenterSalaireNegatif(){
        //Given
        Employe employe = new Employe();
        employe.setSalaire(2000d);
        //When

        //Then
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> employe.augmenterSalaire(-10))
                .withMessage("La valeur entrée est négative");
    }


    @Test
    public void testGetNbAnneesAncienneteDateEmbaucheNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();

        //Then nbAnneesAnciennete = 0
        Assertions.assertThat(nbAnneesAnciennete).isZero();
    }

    @Test
    public void testGetNbAnneesAncienneteDateEmbauchePassee(){
        //Given
        //Date d'embauche 10 ans dans le passé
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(10));
        //employe.setDateEmbauche(LocalDate.of(2012, 4, 26)); //Pas bon...

        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        // => 10
        Assertions.assertThat(nbAnneesAnciennete).isEqualTo(10);
    }

    @Test
    public void testGetNbAnneesAncienneteDateEmbaucheFuture(){
        //Given
        //Date d'embauche 2 ans dans le futur
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));

        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        // => 0
        Assertions.assertThat(nbAnneesAnciennete).isZero();
    }

    @Test
    public void testGetNbAnneesAncienneteDateEmbaucheNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        // => 0
        Assertions.assertThat(nbAnneesAnciennete).isZero();
    }


    @ParameterizedTest
    @CsvSource({
            ",10,6,2.5,100",
            "'M12345',10,,1.0,81",
            "'M67891',20,1,1.0,50",
            "'M23456',30,0,1.0,45",
            "'M78912',05,1,0.5,62",
            "'C12345',7,1,1.0,0",
            "'C67891',5,1,1.0,2",
            "'C23456',2,1,1.0,65",
            "'C78912',0,2,1.0,45",
            "'C34567',3,2,1.0,0",
            "'C89123',0,1,0.5,60",
            "'C45678',0,1,1.0,0"

    })
    public void testGetPrimeAnnuelleManagerPerformanceBasePleinTemps(
            String matricule,
            Integer nbAnneesAnciennete,
            Integer performance,
            Double tauxActivite,
            Double prime
    ){
        //Given
        Employe employe = new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(nbAnneesAnciennete), 2500d, performance, tauxActivite);

        //When
        Double primeObtenue = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeObtenue).isEqualTo(prime);
    }
}
