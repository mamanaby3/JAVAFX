package sen.dev.sponsorshipapp1.contollers;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
//import org.w3c.dom.events.MouseEvent;
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
        id=utilisateur.getId();
        prenomTfd.setText(utilisateur.getPrenom());
        nomTfd.setText(utilisateur.getNom());
        loginTfd.setText(utilisateur.getLogin());
        //passwordTfd.setText(utilisateur.getPassword());
        profilTfd.getSelectionModel().select(utilisateur.getProfil().getName());
        enregistrerBtn.setDisable(true);
    }

    @FXML
    void save(ActionEvent event) {

        String sql="INSERT INTO  user(id,nom,prenom,login,password,profil) VALUES(NULL,?,?,?,?,?)";
        try{
            db.initPrepar(sql);
            db.getPstm().setString(1,nomTfd.getText());
            db.getPstm().setString(2,prenomTfd.getText());
            db.getPstm().setString(3,loginTfd.getText());
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
    }
}


