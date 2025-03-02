package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;

@Service
public class EmployeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //private static final Logger LOGGER = LoggerFactory.getLogger(EmployeService.class);

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private DummyService dummyService;

    public Boolean executeDummy(){
        Integer myInt = dummyService.doSomething();
        if(myInt > 0){
            return true;
        }
        return false;
    }

    /**
     * Méthode enregistrant un nouvel employé dans l'entreprise
     *
     * @param nom Le nom de l'employé
     * @param prenom Le prénom de l'employé
     * @param poste Le poste de l'employé
     * @param niveauEtude Le niveau d'étude de l'employé
     * @param tempsPartiel Le pourcentage d'activité en cas de temps partiel
     *
     * @throws EmployeException Si on arrive au bout des matricules possibles
     * @throws EntityExistsException Si le matricule correspond à un employé existant
     */
    public void embaucheEmploye(String nom, String prenom, Poste poste, NiveauEtude niveauEtude, Double tempsPartiel) throws EmployeException, EntityExistsException {
        logger.info("Embauche de l'employé : {} {} {} {} {}", nom, prenom, poste, niveauEtude, tempsPartiel);

        //Récupération du type d'employé à partir du poste
        logger.trace("Récupération du type d'employé à partir du poste {}", poste);
        String typeEmploye = poste.name().substring(0,1);

        //Récupération du dernier matricule...
        String lastMatricule = employeRepository.findLastMatricule();
        if(lastMatricule == null){
            logger.warn("Aucun employé trouvé, initialisation du matricule initial {}", Entreprise.MATRICULE_INITIAL);
            lastMatricule = Entreprise.MATRICULE_INITIAL;
        }
        //... et incrémentation
        Integer numeroMatricule = Integer.parseInt(lastMatricule) + 1;
        if(numeroMatricule >= 100000){
            logger.error("Limite des 100000 matricules atteinte !");
            throw new EmployeException("Limite des 100000 matricules atteinte !");
        } else if(numeroMatricule > 90000){
            logger.warn("Matricule 90000 dépassé atteint sur une limite de 100000");
        }
        //On complète le numéro avec des 0 à gauche
        String matricule = "00000" + numeroMatricule;
        matricule = typeEmploye + matricule.substring(matricule.length() - 5);
        logger.debug("Matricule calculé : {}", matricule);

        //On vérifie l'existence d'un employé avec ce matricule
        if(employeRepository.findByMatricule(matricule) != null){
            logger.error("L'employé de matricule {} existe déjà en BDD", matricule);
            throw new EntityExistsException("L'employé de matricule " + matricule + " existe déjà en BDD");
        }

        //Calcul du salaire
        Double salaire = Entreprise.COEFF_SALAIRE_ETUDES.get(niveauEtude) * Entreprise.SALAIRE_BASE;
        if(tempsPartiel != null){
            salaire = salaire * tempsPartiel;
        }
        salaire = Math.round(salaire * 100) / 100d;
        logger.debug("Salaire calculé : {}", salaire);

        //Création et sauvegarde en BDD de l'employé.
        Employe employe = new Employe(nom, prenom, matricule, LocalDate.now(), salaire, Entreprise.PERFORMANCE_BASE, tempsPartiel);

        logger.info("Employé avant la sauvegarde : {}", employe.toString());
        employe = employeRepository.save(employe);

        logger.info("Employé après la sauvegarde : {}", employe.toString());
        logger.trace("Fin de la méthode embaucheEmploye");
    }


    /**
     *
     * @param matricule
     * @param caTraite
     * @param objectifCa
     * @throws EmployeException
     */
    public void calculPerformanceCommercial(String matricule, Long caTraite, Long objectifCa) throws EmployeException {
        //Vérification des paramètres d'entrée
        if(caTraite == null || caTraite < 0){
            throw new EmployeException("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
        if(objectifCa == null || objectifCa < 0){
            throw new EmployeException("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
        if(matricule == null || !matricule.startsWith("C")){
            throw new EmployeException("Le matricule ne peut être null et doit commencer par un C !");
        }
        //Recherche de l'employé dans la base
        Employe employe = employeRepository.findByMatricule(matricule);
        if(employe == null){
            throw new EmployeException("Le matricule " + matricule + " n'existe pas !");
        }

        Integer performance = Entreprise.PERFORMANCE_BASE;
        //Cas 2
        if(caTraite >= objectifCa*0.8 && caTraite < objectifCa*0.95){
            performance = Math.max(Entreprise.PERFORMANCE_BASE, employe.getPerformance() - 2);
        }
        //Cas 3
        else if(caTraite >= objectifCa*0.95 && caTraite <= objectifCa*1.05){
            performance = Math.max(Entreprise.PERFORMANCE_BASE, employe.getPerformance());
        }
        //Cas 4
        else if(caTraite <= objectifCa*1.2 && caTraite > objectifCa*1.05){
            performance = employe.getPerformance() + 1;
        }
        //Cas 5
        else if(caTraite > objectifCa*1.2){
            performance = employe.getPerformance() + 4;
        }
        //Si autre cas, on reste à la performance de base.

        //Calcul de la performance moyenne
        Double performanceMoyenne = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");
        if(performanceMoyenne != null && performance > performanceMoyenne){
            performance++;
        }

        //Affectation et sauvegarde
        employe.setPerformance(performance);
        employeRepository.save(employe);
    }
}
