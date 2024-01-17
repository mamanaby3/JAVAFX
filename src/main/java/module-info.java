module sen.dev.sponsorshipapp1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires TrayNotification;

    opens sen.dev.sponsorshipapp1 to javafx.fxml;
    exports sen.dev.sponsorshipapp1;
    exports sen.dev.sponsorshipapp1.contollers;
    opens sen.dev.sponsorshipapp1.contollers to javafx.fxml;
    exports sen.dev.sponsorshipapp1.entities;
    opens sen.dev.sponsorshipapp1.entities to javafx.fxml;
    exports sen.dev.sponsorshipapp1.ripositories.utilisateur;
    opens sen.dev.sponsorshipapp1.ripositories.utilisateur to javafx.fxml;
    exports sen.dev.sponsorshipapp1.ripositories.role;
    opens sen.dev.sponsorshipapp1.ripositories.role to javafx.fxml;
}