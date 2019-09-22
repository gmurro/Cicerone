<?php

require_once '../Globetrotter.php';

$response = array();


if($_POST['nome'] && $_POST['cognome'] && $_POST['dataNascita'] && $_POST['cellulare'] && $_POST['email'] && $_POST['idGlobetrotter'] ){

    $id = $_POST['idGlobetrotter'];
    $nome = $_POST['nome'];
    $cognome = $_POST['cognome'];
    $dataNascita = $_POST['dataNascita'];
    $cellulare= $_POST['cellulare'];
    $email = $_POST['email'];

    $globetrotter = new Globetrotter();
    $response = $globetrotter-> modificaProfiloGlobetrotter($nome, $cognome, $dataNascita, $cellulare, $email, $id );


} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";

}

header('Content-Type: application/json');
echo json_encode($response);


?>