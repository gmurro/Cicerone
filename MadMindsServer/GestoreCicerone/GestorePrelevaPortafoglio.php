<?php
require_once '../Cicerone.php';

$response = array();
if($_POST['codCicerone'] && $_POST['importo']){
    $codCicerone = $_POST['codCicerone'];
    $importo = $_POST['importo'];

    $cicerone = new Cicerone();

    $preleva = $cicerone->prelevaDaPortafoglio($codCicerone, $importo);

    if($preleva == true) {
        $response['error'] = false;
        $response['message'] = "Trasferimento di ".$importo." € andato a buon fine.";
    } else {
        $response['error'] = true;
        $response['message'] = "Errore durante la transazione";
    }


} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>
