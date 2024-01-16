package sen.dev.sponsorshipapp1.ripositories.utilisateur;


import sen.dev.sponsorshipapp1.DBConnection;
import sen.dev.sponsorshipapp1.entities.Role;
import sen.dev.sponsorshipapp1.entities.Utilisateur;
import sen.dev.sponsorshipapp1.ripositories.role.IRole;
import sen.dev.sponsorshipapp1.ripositories.role.RoleImpl;

import java.sql.ResultSet;

public class UtilisateurImpl implements IUtilisateur{
    private DBConnection db=new DBConnection();
    private ResultSet rs;
    @Override
    public Utilisateur seConnecter(String login, String password) {
        String sql="SELECT * FROM user WHERE login = ? AND password = ?";
        Utilisateur user=null;
        try {
            //Préparation ou initialisation de la requete
            db.initPrepar(sql);
            //Passage de valeurs
            db.getPstm().setString(1, login);
            db.getPstm().setString(2, password);
            //Exécution de la requete
            rs = db.executeSelect();
            if(rs.next()){
                user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString(5));
                user.setActived(rs.getInt("actived"));
                IRole iRole = new RoleImpl();
                int idRole = rs.getInt("profil");
                Role role = iRole.getRoleById(idRole);
                user.setProfil(role);
            }
            db.closeConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }
}
