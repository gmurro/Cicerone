<?php
require_once '../Attivita.php';

$response = array();
if($_POST['citta'] && $_POST['nPartecipanti'] && $_POST['dataMin'] && $_POST['dataMax']){

    $citta = $_POST['citta'];
    $nPartecipanti = $_POST['nPartecipanti'];
    $dataMin = $_POST['dataMin'];
    $dataMax = $_POST['dataMax'];

    $attivita = new Attivita();
    $response = $attivita->ricercaAttivita($citta,$nPartecipanti,$dataMin,$dataMax);

} else {
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>