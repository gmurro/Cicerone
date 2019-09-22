<?php

require_once '../Cicerone.php';

$response = array();


if($_POST['nome'] && $_POST['cognome'] && $_POST['dataNascita'] && $_POST['cellulare'] && $_POST['email'] && $_POST['idCicerone']
    && $_POST['citta'] && $_POST['descrizione']){

    $citta = $_POST['citta'];
    $descrizione = $_POST['descrizione'];
    $id = $_POST['idCicerone'];
    $nome = $_POST['nome'];
    $cognome = $_POST['cognome'];
    $dataNascita = $_POST['dataNascita'];
    $cellulare= $_POST['cellulare'];
    $email = $_POST['email'];

    $cicerone = new Cicerone();
    $response = $cicerone-> modificaProfiloCicerone($nome, $cognome, $dataNascita, $cellulare, $email, $id, $citta, $descrizione);


} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";

}

echo json_encode($response);


?>