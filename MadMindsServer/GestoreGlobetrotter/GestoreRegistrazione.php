<?php

require_once '../Globetrotter.php';

$response = array();
if($_POST['email'] && $_POST['password'] && $_POST['nome'] && $_POST['cognome'] && $_POST['dataNascita']){
    $email = $_POST['email'];
    $password = $_POST['password'];
    $nome = $_POST['nome'];
    $cognome = $_POST['cognome'];
    $dataNascita = $_POST['dataNascita'];

    $globetrotter = new Globetrotter();
    $response = $globetrotter->registraNuovoGlobetrotter($email, $password, $nome, $cognome, $dataNascita );

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>