package sen.dev.sponsorshipapp1.ripositories.utilisateur;

import sen.dev.sponsorshipapp1.entities.Utilisateur;

public interface IUtilisateur {
    public Utilisateur seConnecter(String login, String password);
}
