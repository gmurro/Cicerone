<?php

require_once '../Globetrotter.php';
$response = array();


if($_POST['idGlobetrotter'] && $_POST['img'] ){

    $id = $_POST['idGlobetrotter'];
    $img = $_POST['img'];
    $pathFile = "img/".$id."_profilo.jpg";

    $globetrotter = new Globetrotter();
    if( salvaImmagine( $pathFile, $img ) ) {
        $response = $globetrotter-> modificaFotoGlobetrotter($pathFile, $id);
    } else {
        $response['error'] = true;
        $response['message'] = "Errore nel caricamento della foto";
    }



} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";

}

echo json_encode($response);


function salvaImmagine( $path, $img ) {

    $img = str_replace('data:image/png;base64,', '', $img);
    $img = str_replace('data:image/jpeg;base64,', '', $img);
    $img = str_replace('data:image/jpg;base64,', '', $img);
    $img = str_replace(' ', '+', $img);
    $data = base64_decode($img);
    $success = file_put_contents($path, $data);

    if($success) {
        return true;
    } else {
        return false;
    }
}