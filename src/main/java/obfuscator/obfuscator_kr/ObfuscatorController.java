package obfuscator.obfuscator_kr;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ObfuscatorController{
    @FXML
    private Button searchFiles;
    @FXML
    private TextArea convertedCode;
    @FXML
    private TextArea sourceCode;
    @FXML
    private Button buttonObf;
    @FXML
    private Button buttonDeobf;
    @FXML
    private TextField codeWord;
    @FXML
    private CheckBox replaceVar;
    @FXML
    private CheckBox deleteComments;
    @FXML
    private CheckBox oneString;
    @FXML
    private CheckBox tabsAndSpace;
    public static File selectedFile;
    public static String userStr;
    @FXML
    protected void onButtonSearchFilesClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select ...", ".java");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Select a .java", "*.java"));
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile == null) return;
        sourceCode.setText(Main.getText());
    }
    @FXML
    protected void onButtonObfClick() throws IOException {
        Main.userStr = codeWord.getText();
        convertedCode.setText(Main.obfuscation(replaceVar.isSelected(), deleteComments.isSelected(), oneString.isSelected(), tabsAndSpace.isSelected()));
        //textArea1.setText(Main.getText());
        StringBuilder pathSb = new StringBuilder();
        pathSb.append(selectedFile.getPath());
        int countPath = pathSb.length()-1;
        while (pathSb.charAt(countPath) != '\\') {
            pathSb.deleteCharAt(countPath);
            countPath--;
        }
        String nameFile;
        StringBuilder nameSelectedFile = new StringBuilder();
        for (int i = 0; i < selectedFile.getName().toString().length(); i++) {
            if (selectedFile.getName().toString().charAt(i)!='.') {
                nameSelectedFile.append(selectedFile.getName().toString().charAt(i));
            }
            else {
                break;
            }
        }
        if (Main.nameMainClass.equals(nameSelectedFile.toString())) {
            nameFile = Main.nameMainClass + "(1)";
            File file = new File(pathSb.toString() + nameFile + ".java");
            if (file.createNewFile()){
                System.out.println("File is created!");
            }
            else{
                System.out.println("File already exists.");
            }
            FileWriter writer = new FileWriter (file);
            writer.write(Main.obfCode);
            writer.close();
        }
        else {
            File file = new File(pathSb.toString() + Main.nameMainClass + ".java");
            if (file.createNewFile()) {
                System.out.println("File is created!");
            }
            else {
                System.out.println("File already exists.");
            }
            FileWriter writer = new FileWriter (file);
            writer.write(Main.obfCode);
            writer.close();
        }
    }
    @FXML
    protected void onButtonDeobfuscationClick() throws IOException {
        Main.userStr = codeWord.getText();
        convertedCode.setText(Deobfuscation.deobfuscation());
        StringBuilder pathSb = new StringBuilder();
        pathSb.append(selectedFile.getPath());
        int countPath = pathSb.length()-1;
        while (pathSb.charAt(countPath) != '\\') {
            pathSb.deleteCharAt(countPath);
            countPath--;
        }
        System.out.println();
        File file = new File(pathSb.toString() + Main.nameMainClass + ".java");
        if (file.createNewFile()){
            System.out.println("File is created!");
        }
        else{
            System.out.println("File already exists.");
        }
        FileWriter writer = new FileWriter (file);
        writer.write(Deobfuscation.deobfCode);
        writer.close();
    }
}