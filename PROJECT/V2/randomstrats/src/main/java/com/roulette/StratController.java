package com.roulette;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;

public class StratController implements Initializable {

    @FXML
    private RadioButton attackBox;

    @FXML
    private RadioButton defendBox;
    
    @FXML
    private Button deleteStratButton;
    
    @FXML 
    private ChoiceBox<String> gameType;
    private String[] gameModes = {"Bomb", "Hostage", "Secure Area"};

    @FXML
    private Button insertStratButton;

    @FXML
    private TextArea resultTextArea;
    final private String path = "./files/strats.txt";

    @FXML
    private Button stratButton;

    @FXML
    private Group mode;

    @FXML
    private TextArea newStratTextArea;

    @FXML
    void addStrat(ActionEvent event) {
        String stratToAdd = gameType.getValue();
        if(newStratTextArea.getText() == "") {
            resultTextArea.setText("Could not add strat.");
            return;
        }
        stratToAdd += attackBox.isSelected() ? ":Attacking:" : ":Defending:";
        stratToAdd += newStratTextArea.getText();
        stratToAdd = stratToAdd.replace("\n","") + "\n";
        byte[] stratToBytes = stratToAdd.getBytes();
        try {
            Files.write(Paths.get(path), stratToBytes, StandardOpenOption.APPEND);
            updateTextArea(newStratTextArea.getText());
        } catch (IOException e) {
            resultTextArea.setText("Error: Could not fine file in: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void deleteStrat(ActionEvent event) {
        String stratToDelete = resultTextArea.getText().replace("\n", "");
        stratToDelete = stratToDelete.replaceFirst("Mode: ", "")
            .replaceFirst("Side", "")
            .replaceFirst(" ", "")
            .replaceFirst("Strat", "")
            .replaceFirst(" ", "");
        if(stratToDelete.contains("Secure")) {
            stratToDelete = stratToDelete.replaceFirst(" ", "");
            stratToDelete = stratToDelete.replaceFirst("Secure", "Secure ");

        }
        try {
            List<String> strats = Files.readAllLines(Paths.get(path));
            if(strats.remove(stratToDelete)) {
                resultTextArea.setText("Successfully Removed: \n" + stratToDelete);
                Files.write(Paths.get(path), strats);
            }
            else {
                resultTextArea.setText("Could not remove strat, or there was no strat selected."); 
            }
        } catch (IOException e) {
            resultTextArea.setText("Error: Could not find file in: " + e.getMessage());
            e.printStackTrace();
        }
    }    

        @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        gameType.getItems().addAll(gameModes);
        gameType.setValue("Bomb");
        Path stratFile = Paths.get(path);
        if (!Files.exists(stratFile)) {
            try {
                Files.createDirectories(stratFile.getParent()); // create all directories in the path
                Files.createFile(stratFile); // create the file itself
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void getStrat(ActionEvent event) {
        String selectedMode = gameType.getValue();
        boolean isAttacking = attackBox.isSelected();
        String side = isAttacking ? "Attacking" : "Defending";

        try {
            List<String> strats = Files.readAllLines(Paths.get(path));
            String foundStrat = findRandomStrat(selectedMode, side, strats);
            updateTextArea(foundStrat);
        } catch (IOException e) {
            resultTextArea.setText("Error: Could not fine file in: " + e.getMessage());
            e.printStackTrace();
        }
    }

    String findRandomStrat(String mode, String side, List<String> strats) {
        List<String> matchingStrats = new ArrayList<>();

        for (String strat : strats) {
            String[] stratInfo = strat.split(":");
            String stratMode = stratInfo[0];
            String stratSide = stratInfo[1];
            String stratText = stratInfo[2];

            if (stratMode.equals(mode) && stratSide.equals(side)) {
                matchingStrats.add(stratText);
            }
        }

        if (matchingStrats.isEmpty()) {
            return "Could not find strats that match given criteria";
        } else {
            Collections.shuffle(matchingStrats);
            return matchingStrats.get(0);
            }
        }

        void updateTextArea(String text) {
            boolean isAttacking = attackBox.isSelected();
            String side = isAttacking ? "Attacking" : "Defending";
            String selectedMode = gameType.getValue();
            resultTextArea.setText("Mode: " + selectedMode + "\nSide: " + side + "\n\nStrat: " + text);
        }

}
