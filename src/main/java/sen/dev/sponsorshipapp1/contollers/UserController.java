package sen.dev.sponsorshipapp1.contollers;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
//import org.w3c.dom.events.MouseEvent;
import javafx.stage.Stage;
import sen.dev.sponsorshipapp1.DBConnection;
import sen.dev.sponsorshipapp1.entities.Role;
import sen.dev.sponsorshipapp1.entities.Utilisateur;
import sen.dev.sponsorshipapp1.ripositories.role.IRole;
import sen.dev.sponsorshipapp1.ripositories.role.RoleImpl;
import sen.dev.sponsorshipapp1.tools.Notification;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class UserController  implements Initializable {
    private DBConnection db=new DBConnection();


    private int id;
    @FXML
    private TextField nomTfd;
    @FXML
    private TextField loginTfd;
    @FXML
    private TextField prenomTfd;

    @FXML
    private TextField emailTfd;

    @FXML
    private PasswordField passwordTfd;

    @FXML
    private TableColumn<Utilisateur, Integer> activedCol;

    @FXML

    private ComboBox<String> profilTfd;

    @FXML
    private TableView<Utilisateur> utilisateursTbl;

    @FXML
    private TableColumn<Utilisateur, Integer> idCol;

    @FXML
    private TableColumn<Utilisateur, String> nomCol;

    @FXML
    private TableColumn<Utilisateur, String> prenomCol;

    @FXML
    private TableColumn<Utilisateur, String> profilCol;
    @FXML
    private TableColumn<Utilisateur, String> loginCol;

    @FXML
    private Button enregistrerBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @FXML
    private Button deconnectionBtn;
    @FXML
    void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) deconnectionBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Notification.NotifError("Error","Deconnection Echouer!");
        }
    }

    @FXML
    void vider(ActionEvent event) {
        viderChamp();
    }

    @FXML
    void update(ActionEvent event) {
        String sql = "UPDATE user SET nom = ?, prenom = ?, login = ?, profil = ? WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setString(1, nomTfd.getText());
            db.getPstm().setString(2, prenomTfd.getText());
            db.getPstm().setString(3, loginTfd.getText());
            // db.getPstm().setString(4, passwordTfd.getText());
            String profil = profilTfd.getSelectionModel().getSelectedItem().toString();
            db.getPstm().setInt(4, profil.equals("ROLE_CANDIDAT") ? 2 : 3);
            db.getPstm().setInt(5, id);
            db.executeMaj();
            db.closeConnection();
            loadTable();
            viderChamp();
            Notification.NotifSuccess("Succes", "Modification Avec success!");
            enregistrerBtn.setDisable(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void getData(MouseEvent event) {
        Utilisateur utilisateur=utilisateursTbl.getSelectionModel().getSelectedItem();
        if (utilisateur != null) {
            id = utilisateur.getId();
            prenomTfd.setText(utilisateur.getPrenom());
            nomTfd.setText(utilisateur.getNom());
            loginTfd.setText(utilisateur.getLogin());
            //passwordTfd.setText(utilisateur.getPassword());
            profilTfd.getSelectionModel().select(utilisateur.getProfil().getName());
            enregistrerBtn.setDisable(true);
        }
    }
    private String generateUsername(String nom, String prenom) {
        return (nom.substring(0, 2) + prenom).toUpperCase();
    }
    @FXML
    void save(ActionEvent event) {

        String sql="INSERT INTO  user(id,nom,prenom,login,password,profil) VALUES(NULL,?,?,?,?,?)";
        try{
            db.initPrepar(sql);
            db.getPstm().setString(1,nomTfd.getText());
            db.getPstm().setString(2,prenomTfd.getText());
            //generate login
            String generatedUsername = generateUsername(nomTfd.getText(), prenomTfd.getText());
            db.getPstm().setString(3, generatedUsername);
            loginTfd.setText(generatedUsername);
            //db.getPstm().setString(3,loginTfd.getText());
            db.getPstm().setString(4,passwordTfd.getText());
            String profil= profilTfd.getSelectionModel().getSelectedItem().toString();
            db.getPstm().setInt(5,profil.equals("ROLE_CANDIDAT") ? 2:3);
            db.executeMaj();
            db.closeConnection();
            loadTable();
            viderChamp();
            Notification.NotifSuccess("Succes","Utilisateur enregistrer Avec success!");
        }catch (SQLException e){
            throw  new RuntimeException();
        }
    }

    @FXML
    void delete(ActionEvent event) {
        String sql = "DELETE FROM user WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, id);
            db.executeMaj();
            db.closeConnection();
            loadTable();
            viderChamp();
            Notification.NotifSuccess("Succes", "Utilisateur Supprimer Avec success!");
            enregistrerBtn.setDisable(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
        setListeProfil();

        ///////////////////////Activer et desactiver
        TableColumn<Utilisateur, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button activerBtn = new Button("Activer");
            private final Button desactiverBtn = new Button("Désactiver");

            //La syntaxe utilisée ici, avec les {} après la déclaration des boutons
            // crée un bloc d'initialisation d'instance. Ce bloc d'initialisation est exécuté
            // lorsque chaque instance de la classe est créée. Dans ce cas, il est utilisé pour définir les actions
            // associées aux boutons activerBtn et desactiverBtn
            {
                activerBtn.setOnAction(event -> {
                    Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                    activerUtilisateur(utilisateur);
                });

                desactiverBtn.setOnAction(event -> {
                    Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                    desactiverUtilisateur(utilisateur);
                });
            }
            ///Cette méthode est appelée chaque fois qu'une cellule dans le TableView doit être mise à jour
            //item représente la valeur actuelle de la cellule //empty est un indicateur qui indique si la cellule est vide ou non.
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                ///setGraphic(null) est utilisé pour s'assurer que la cellule est vide graphiquement.
                if (empty) {
                    setGraphic(null);
                } else {
                    Utilisateur utilisateur = getTableView().getItems().get(getIndex());
                    if (utilisateur.getActived() == 1) {
                        setGraphic(desactiverBtn);
                    } else {
                        setGraphic(activerBtn);
                    }
                }
            }
        });

        utilisateursTbl.getColumns().add(actionCol);
    }

    ///La fonction qui permet d'activer un utilisateur
    private void activerUtilisateur(Utilisateur utilisateur) {
        String sql = "UPDATE user SET actived = 1 WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, utilisateur.getId());
            db.executeMaj();
            db.closeConnection();
            Notification.NotifSuccess("Succes", "Utilisateur activé avec succès !");
            loadTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    ///La fonction qui permet desactiver un utilisateur
    private void desactiverUtilisateur(Utilisateur utilisateur) {
        String sql = "UPDATE user SET actived = 0 WHERE id = ?";
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, utilisateur.getId());
            db.executeMaj();
            db.closeConnection();
            Notification.NotifSuccess("Succes", "Utilisateur désactivé avec succès !");
            loadTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setListeProfil(){
        List<String> profilList=new ArrayList<>();
        String sql = "SELECT name FROM role WHERE id <> 1";
        try {
            db.initPrepar(sql);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                profilList.add(rs.getString(1));
            }
            db.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        profilTfd.getItems().addAll(profilList);
    }
    public ObservableList<Utilisateur> getUtilisateurs() {
        ObservableList<Utilisateur> utilisateurs = FXCollections.observableArrayList();
        String sql = "SELECT * FROM user ";
        try {
            db.initPrepar(sql);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt(1));
                user.setNom(rs.getString(2));
                user.setPrenom(rs.getString(3));
                user.setLogin(rs.getString(4));
                user.setActived(rs.getInt(6));
                IRole iRole = new RoleImpl();
                Role profil = iRole.getRoleById(rs.getInt(7));
                user.setProfil(profil);
                utilisateurs.add(user);
            }
            db.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return utilisateurs;
    }

    public void loadTable(){
        ObservableList<Utilisateur> liste=getUtilisateurs();
        utilisateursTbl.setItems(liste);
        idCol.setCellValueFactory(new PropertyValueFactory<Utilisateur,Integer>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("prenom"));
        profilCol.setCellValueFactory(cellDate->{
            Utilisateur utilisateur=cellDate.getValue();
            Role role=utilisateur.getProfil();
            String roleName=(role!=null) ? role.getName() : "";
            return new SimpleStringProperty(roleName);
        });
        loginCol.setCellValueFactory(new PropertyValueFactory<Utilisateur,String>("login"));
        activedCol.setCellValueFactory(new PropertyValueFactory<Utilisateur,Integer>("actived"));
    }
    public void viderChamp(){
        nomTfd.setText("");
        prenomTfd.setText("");
        loginTfd.setText("");
        passwordTfd.setText("");
        profilTfd.getSelectionModel().clearSelection();
    }
}


