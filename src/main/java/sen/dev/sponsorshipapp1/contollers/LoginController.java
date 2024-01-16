package sen.dev.sponsorshipapp1.contollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sen.dev.sponsorshipapp1.entities.Utilisateur;
import sen.dev.sponsorshipapp1.ripositories.utilisateur.IUtilisateur;
import sen.dev.sponsorshipapp1.ripositories.utilisateur.UtilisateurImpl;
import sen.dev.sponsorshipapp1.tools.Notification;
import sen.dev.sponsorshipapp1.tools.Outils;

public class LoginController {

    @FXML
    private TextField loginTfd;

    @FXML
    private PasswordField passwordTfd;
    private IUtilisateur userDao=new UtilisateurImpl();
    @FXML
    void login(ActionEvent event) {
        String login = loginTfd.getText();
        String password = passwordTfd.getText();
        if (login.trim().equals("") || password.trim().equals("")){
            System.out.println("Tous les champs sont obligatoires ");
            Notification.NotifError("Erreur !", "Tous les champs sont obligatoires !");
        }else{
            Utilisateur user = userDao.seConnecter(login, password);
            if(user != null){
                try {
                    Notification.NotifSuccess("Succés !", "Connexion réussie !");
                    if (user.getProfil().getName().equalsIgnoreCase("ROLE_ADMIN"))
                        Outils.load(event, "Bienvenue", "/pages/admin.fxml");
                    else if (user.getProfil().getName().equalsIgnoreCase("ROLE_CANDIDAT"))
                        Outils.load(event, "Bienvenue", "/pages/candidat.fxml");
                    else
                        Outils.load(event, "Bienvenue", "/pages/electeur.fxml");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Notification.NotifError("Erreur !", "Login et/ou password incorrects !");
            }
        }

    }

}
