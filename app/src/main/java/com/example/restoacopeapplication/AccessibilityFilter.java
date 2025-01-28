package com.example.restoacopeapplication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import android.util.Log;
public class AccessibilityFilter {
    // Mobilité Physique
    public static class MobilitePhysique {
        public static final String RAMPE = "rampeAcces";
        public static final String PARKING = "parkingPMR";
        public static final String PORTES = "porteAutomatique";
        public static final String CHEMIN = "chemainDaccesStable";
        public static final String TOILETTES = "toilettesAdaptes";
        public static final String ASCENSEUR = "ascenseur";
        public static final String TABLES = "tablesHauteurAdaptes";
        public static final String CIRCULATION = "circulationsAise";
    }

    // Déficience Visuelle
    public static class DeficienceVisuelle {
        public static final String MENU_BRAILLE = "menuBraille";
        public static final String GROS_CARACTERES = "menusGrosCaracteres";
        public static final String DESCRIPTION = "descriptionAudio";
        public static final String ECLAIRAGE = "eclairageAdapte";
        public static final String CHIENS = "chiensGuideAcceptes";
        public static final String FORMATION = "formationDuPersonnel";
    }

    // Déficience Auditive
    public static class DeficienceAuditive {
        public static final String LSF = "PersonnelFormeLSF";
        public static final String SUPPORT_ECRIT = "SupportEcrit";
        public static final String SUPPORT_VISUEL = "SupportVisuel";
        public static final String BOUCLE = "BoucleMagnetique";
        public static final String ALERTES = "AlertesVisuelles";
        public static final String ALARMES = "AlarmesLuminueuses";
    }

    private Map<String, Boolean> filters = new HashMap<>();
    public AccessibilityFilter() {
        filters = new HashMap<>();
        initializeFilters(); // Initialise tous les filtres à false
    }

    // Méthode pour initialiser tous les filtres possibles
    private void initializeFilters() {
        // Mobilité Physique
        filters.put(MobilitePhysique.RAMPE, false);
        filters.put(MobilitePhysique.PARKING, false);
        filters.put(MobilitePhysique.PORTES, false);
        filters.put(MobilitePhysique.CHEMIN, false);
        filters.put(MobilitePhysique.TOILETTES, false);
        filters.put(MobilitePhysique.ASCENSEUR, false);
        filters.put(MobilitePhysique.TABLES, false);
        filters.put(MobilitePhysique.CIRCULATION, false);

        // Déficience Visuelle
        filters.put(DeficienceVisuelle.MENU_BRAILLE, false);
        filters.put(DeficienceVisuelle.GROS_CARACTERES, false);
        filters.put(DeficienceVisuelle.DESCRIPTION, false);
        filters.put(DeficienceVisuelle.ECLAIRAGE, false);
        filters.put(DeficienceVisuelle.CHIENS, false);
        filters.put(DeficienceVisuelle.FORMATION, false);

        // Déficience Auditive
        filters.put(DeficienceAuditive.LSF, false);
        filters.put(DeficienceAuditive.SUPPORT_ECRIT, false);
        filters.put(DeficienceAuditive.SUPPORT_VISUEL, false);
        filters.put(DeficienceAuditive.BOUCLE, false);
        filters.put(DeficienceAuditive.ALERTES, false);
        filters.put(DeficienceAuditive.ALARMES, false);
    }


    public void toggleFilter(String key, boolean value) {
        filters.put(key, value);
        Log.d("Filter", "Toggle filter - Key: " + key + ", New Value: " + value);
        Log.d("Filter", "Current filters state: " + filters.toString());

        // Vérifie si le filtre a bien été enregistré
        boolean currentValue = filters.get(key);
        if (currentValue != value) {
            Log.e("Filter", "Error: Filter value not properly saved!");
        }
    }
    public boolean isFilterActive(String key) {
        Boolean value = filters.get(key);
        Log.d("Filter", "Checking filter: " + key + " = " + value);
        return value != null && value;
    }
    public void logCurrentState() {
        Log.d("Filter", "=== Current Filter State ===");
        for (Map.Entry<String, Boolean> entry : filters.entrySet()) {
            Log.d("Filter", entry.getKey() + ": " + entry.getValue());
        }
        Log.d("Filter", "=========================");
    }

    // Ajout de la méthode getActiveFilters
    public Set<String> getActiveFilters() {
        Set<String> activeFilters = new HashSet<>();
        for (Map.Entry<String, Boolean> entry : filters.entrySet()) {
            if (entry.getValue()) {
                activeFilters.add(entry.getKey());
            }
        }
        return activeFilters;
    }
}