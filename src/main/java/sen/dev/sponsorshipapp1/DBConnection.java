package sen.dev.sponsorshipapp1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConnection {
        //Pour la connexion
        private Connection cnx;
        //Pour les requetes préparées
        private PreparedStatement pstm;
        //Pour les résultats des requetes de type SELECT
        private ResultSet rs;
        //Pour les résultats des requetes de type mis á jour(INSERT,UPDATE,DELETE)
        private int ok;

        //Méthode de connexion á la base de données
        private void getConnection(){
            //Paramétres de connexion
            String host = "localhost";
            String database = "sponsorship_db";
            int port = 3306;
            String url = "jdbc:mysql://"+host+":"+port+"/"+database;
            String user = "root";
            String password = "";
            try {
                //Charger le pilote (ou driver) JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");
                //Ouvrir la base de données
                cnx = DriverManager.getConnection(url, user, password);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void initPrepar(String sql){
            try {
                getConnection();
                pstm = cnx.prepareStatement(sql);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public ResultSet executeSelect(){
            rs = null;
            try {
                rs = pstm.executeQuery();
            }catch (Exception e){
                e.printStackTrace();
            }
            return rs;
        }

        public int executeMaj(){
            try {
                ok = pstm.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
            return ok;
        }

        public void closeConnection(){
            try {
                if (cnx != null)
                    cnx.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public PreparedStatement getPstm() {
            return pstm;
        }
}

