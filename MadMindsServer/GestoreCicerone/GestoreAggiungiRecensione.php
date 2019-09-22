<?php

require_once '../Recensione.php';

$response = array();



if($_POST['idCicerone'] && $_POST['idGlobetrotter'] && $_POST['voto'] && $_POST['descrizione']){

    $idCicerone = $_POST['idCicerone'];
    $idGlobetrotter = $_POST['idGlobetrotter'];
    $voto = $_POST['voto'];
    $descrizione= $_POST['descrizione'];
    $cicerone = new Recensione();
    $response = $cicerone-> aggiungiRecensione($idCicerone,$idGlobetrotter,$voto,$descrizione);

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}


echo json_encode($response);

?>