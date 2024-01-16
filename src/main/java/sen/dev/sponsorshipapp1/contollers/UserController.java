package sen.dev.sponsorshipapp1.contollers;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sen.dev.sponsorshipapp1.DBConnection;
import sen.dev.sponsorshipapp1.entities.Role;
import sen.dev.sponsorshipapp1.entities.Utilisateur;
import sen.dev.sponsorshipapp1.ripositories.role.IRole;
import sen.dev.sponsorshipapp1.ripositories.role.RoleImpl;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class UserController  implements Initializable {
    private DBConnection db=new DBConnection();
    @FXML
    private TextField nomTfd;

    @FXML
    private TextField prenomTfd;

    @FXML
    private TextField emailTfd;

    @FXML
    private PasswordField passwordTfd;

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
    private Button enregistrerBtn;

    @FXML
    private Button modifierBtn;

    @FXML
    private Button supprimerBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
        setListeProfil();
    }

    public void setListeProfil(){
        List<String> profilList=new ArrayList<>();
        String sql = "SELECT name FROM role";
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

    }
}


