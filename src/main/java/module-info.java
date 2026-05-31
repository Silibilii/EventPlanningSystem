module ru.kafpin.krswrk {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires org.slf4j;
    requires java.prefs;


    opens ru.kafpin.krswrk to javafx.fxml;
    exports ru.kafpin.krswrk;
    exports ru.kafpin.krswrk.dao;
    opens ru.kafpin.krswrk.dao to javafx.fxml;
    exports ru.kafpin.krswrk.controller;
    opens ru.kafpin.krswrk.controller to javafx.fxml;
    exports ru.kafpin.krswrk.model;
    opens ru.kafpin.krswrk.model to javafx.fxml;
    exports ru.kafpin.krswrk.dao.impl;
    opens ru.kafpin.krswrk.dao.impl to javafx.fxml;
    exports ru.kafpin.krswrk.util;
    opens ru.kafpin.krswrk.util to javafx.fxml;
}