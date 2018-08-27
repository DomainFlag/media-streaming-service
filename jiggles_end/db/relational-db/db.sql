#------------------------------------------------------------
# Database: Jiggles | If on localhost
#------------------------------------------------------------

DROP DATABASE IF EXISTS Jiggles;
CREATE DATABASE Jiggles;
USE Jiggles;

#------------------------------------------------------------
# Dropping existing tables
#------------------------------------------------------------

SET foreign_key_checks = 0;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS playlist;
DROP TABLE IF EXISTS keeper;
DROP TABLE IF EXISTS track;
DROP TABLE IF EXISTS album;
DROP TABLE IF EXISTS artist;
DROP TABLE IF EXISTS record;
DROP TABLE IF EXISTS chat;
DROP TABLE IF EXISTS social;
DROP TABLE IF EXISTS thread;
DROP TABLE IF EXISTS interaction;

#------------------------------------------------------------
# Table: user
#------------------------------------------------------------

CREATE TABLE user(
        id        int (11) AUTO_INCREMENT NOT NULL ,
        hash      Varchar (255) NOT NULL ,
        salt      Varchar (255) NOT NULL ,
        path      Varchar (255) NOT NULL ,
        created_at Date NOT NULL DEFAULT CURRENT_TIMESTAMP ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: friend
#------------------------------------------------------------

CREATE TABLE friend(
        idU       int NOT NULL ,
        idF       int NOT NULL ,
        created_at Date NOT NULL DEFAULT CURRENT_TIMESTAMP ,
        PRIMARY KEY (idU, idF)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: preferences
#------------------------------------------------------------

CREATE TABLE preferences(
        id        int NOT NULL ,
        color     Varchar (255) NOT NULL ,
        location  Varchar (255) NOT NULL ,
        # ...
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: playlist
#------------------------------------------------------------

CREATE TABLE playlist(
        id    int (11) AUTO_INCREMENT  NOT NULL ,
        name  Varchar (255) NOT NULL ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: keeper
#------------------------------------------------------------

CREATE TABLE keeper(
        idP   int NOT NULL ,
        idT   int NOT NULL ,
        PRIMARY KEY (idP, idT)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: track
#------------------------------------------------------------

CREATE TABLE track(
        id          int (11) AUTO_INCREMENT  NOT NULL ,
        idA         int NOT NULL ,
        position    int NOT NULL DEFAULT 0,
        name        Varchar (255) NOT NULL ,
        path        Varchar (255) NOT NULL ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: album
#------------------------------------------------------------

CREATE TABLE album(
        id         int (11) AUTO_INCREMENT  NOT NULL ,
        idA        int NOT NULL ,
        name       Varchar (255) NOT NULL ,
        created_at DATE NOT NULL ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: artist
#------------------------------------------------------------

CREATE TABLE artist(
        id      int (11) Auto_increment  NOT NULL ,
        name    Varchar (255) NOT NULL ,
        profile Varchar (255) NOT NULL,
        label   Varchar (255) NOT NULL ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: record
#------------------------------------------------------------

CREATE TABLE record(
        id         int (11) AUTO_INCREMENT  NOT NULL ,
        idU        int NOT NULL ,
        idG        int NOT NULL ,
        message    Varchar (255) NOT NULL ,
        package    Varchar (255) NOT NULL ,
        created_at DATE NOT NULL DEFAULT CURRENT_TIMESTAMP ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: group
#------------------------------------------------------------

CREATE TABLE group(
        idG     int (11) AUTO_INCREMENT  NOT NULL ,
        idU     int NOT NULL ,
        idC     int NOT NULL ,
        PRIMARY KEY (idU, idC)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: chat
#------------------------------------------------------------

CREATE TABLE chat(
        id      int (11) AUTO_INCREMENT  NOT NULL ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: social
#------------------------------------------------------------

CREATE TABLE social(
        id          int (11) AUTO_INCREMENT  NOT NULL ,
        idU         int NOT NULL ,
        name        Varchar (255) NOT NULL ,
        created_at  DATE NOT NULL DEFAULT CURRENT_TIMESTAMP ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: thread
#------------------------------------------------------------

CREATE TABLE thread(
        id          int (11) AUTO_INCREMENT  NOT NULL ,
        idS         int NOT NULL ,
        idU         int NOT NULL ,
        title       Varchar (255) NOT NULL ,
        content     Varchar (255) NOT NULL ,
        created_at  DATE NOT NULL DEFAULT CURRENT_TIMESTAMP ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: interaction
#------------------------------------------------------------

CREATE TABLE interaction(
        id          int (11) AUTO_INCREMENT  NOT NULL ,
        idP         int NOT NULL ,
        idT         int NOT NULL ,
        idU         int NOT NULL ,
        depth       int NOT NULL ,
        message     Varchar (255) NOT NULL ,
        content     Varchar (255) NOT NULL ,
        created_at  DATE NOT NULL DEFAULT CURRENT_TIMESTAMP ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: collection
#------------------------------------------------------------

CREATE TABLE collection(
        id          int (11) AUTO_INCREMENT  NOT NULL ,
        idU         int NOT NULL ,
        path        Varchar (255) UNIQUE ,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: devices
#------------------------------------------------------------

CREATE TABLE device(
        idU         int NOT NULL ,
        name        Varchar (255) NOT NULL ,
        device      Varchar (255) UNIQUE NOT NULL ,
        favourite   Boolean NOT NULL DEFAULT FALSE ,
        path        Varchar (255) NOT NULL ,
        PRIMARY KEY (idU, device)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: queue
#------------------------------------------------------------

CREATE TABLE queue(
        id          int (11) AUTO_INCREMENT  NOT NULL ,
        name        Varchar (255) NOT NULL ,
        position    int NOT NULL DEFAULT 0,
        created_at  DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# Table: piece
#------------------------------------------------------------

CREATE TABLE piece(
        id          int (11) AUTO_INCREMENT  NOT NULL ,
        idQ         int NOT NULL ,
        idT         int NOT NULL ,
        idU         int NOT NULL ,
        position    int NOT NULL DEFAULT 0,
        created_at  DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (id)
)ENGINE=InnoDB;


#------------------------------------------------------------
# CONSTRAINTS: ALL tables
#------------------------------------------------------------

ALTER TABLE friend ADD CONSTRAINT FK_friend_idU FOREIGN KEY (idU) REFERENCES user(id);
ALTER TABLE friend ADD CONSTRAINT FK_friend_idF FOREIGN KEY (idF) REFERENCES user(id);

ALTER TABLE preferences ADD CONSTRAINT FK_preferences_id FOREIGN KEY (id) REFERENCES user(id);

ALTER TABLE keeper ADD CONSTRAINT FK_keeper_idP FOREIGN KEY (idP) REFERENCES playlist(id);
ALTER TABLE keeper ADD CONSTRAINT FK_keeper_idT FOREIGN KEY (idT) REFERENCES track(id);

ALTER TABLE track ADD CONSTRAINT FK_track_idA FOREIGN KEY (idA) REFERENCES album(id);

ALTER TABLE album ADD CONSTRAINT FK_album_idA FOREIGN KEY (idA) REFERENCES artist(id);

ALTER TABLE record ADD CONSTRAINT FK_record_idU FOREIGN KEY (idU) REFERENCES user(id);
ALTER TABLE record ADD CONSTRAINT FK_record_idG FOREIGN KEY (idG) REFERENCES group(id);

ALTER TABLE group ADD CONSTRAINT FK_group_idU FOREIGN KEY (idU) REFERENCES user(id);
ALTER TABLE group ADD CONSTRAINT FK_group_idC FOREIGN KEY (idC) REFERENCES chat(id);

ALTER TABLE social ADD CONSTRAINT FK_social_idU FOREIGN KEY (idU) REFERENCES user(id);

ALTER TABLE thread ADD CONSTRAINT FK_thread_idS FOREIGN KEY (idS) REFERENCES social(id);
ALTER TABLE thread ADD CONSTRAINT FK_thread_idU FOREIGN KEY (idU) REFERENCES user(id);

ALTER TABLE interaction ADD CONSTRAINT FK_interaction_idS FOREIGN KEY (idP) REFERENCES interaction(id);
ALTER TABLE interaction ADD CONSTRAINT FK_interaction_idU FOREIGN KEY (idT) REFERENCES thread(id);
ALTER TABLE interaction ADD CONSTRAINT FK_interaction_idS FOREIGN KEY (idU) REFERENCES user(id);

ALTER TABLE collection ADD CONSTRAINT FK_collection_idU FOREIGN KEY (idU) REFERENCES user(id);

ALTER TABLE device ADD CONSTRAINT FK_device_idU FOREIGN KEY (idU) REFERENCES user(id);

ALTER TABLE piece ADD CONSTRAINT FK_piece_idQ FOREIGN KEY (idQ) REFERENCES queue(id);
ALTER TABLE piece ADD CONSTRAINT FK_piece_idT FOREIGN KEY (idT) REFERENCES track(id);
ALTER TABLE piece ADD CONSTRAINT FK_piece_idU FOREIGN KEY (idU) REFERENCES user(id);
