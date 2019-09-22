<?php
require_once '../Attivita.php';
require_once '../Prenotazione.php';
require_once '../Amministrazione.php';
require_once '../Globetrotter.php';

$response = array();
if($_POST['codAttivita'] && $_POST['nPartecipanti']){
    $codAttivita = $_POST['codAttivita'];
    $nPartecipanti = $_POST['nPartecipanti'];

    $attivita = new Attivita();
    $response = $attivita->visualizzaAttivita($codAttivita);
    $prezzoUnitario = $attivita->getFasciaPrezzo($codAttivita, $nPartecipanti);

    $creatore = new Globetrotter();
    $datiCreatore = $creatore->visualizzaProfilo($response['creatore']);
    $response['creatore'] = $datiCreatore['nome']." ".$datiCreatore['cognome'];

    $amministrazione = new Amministrazione();
    $surplus = $amministrazione->visualizzaSurplus($amministrazione->getEmail());
    $response['surplus'] = $surplus;

    $prezzoParziale = $prezzoUnitario*$nPartecipanti;
    $prezzoAggiuntivo = ($prezzoUnitario*$nPartecipanti)*$surplus/100;
    $prezzoTotale = $prezzoParziale + $prezzoAggiuntivo;

    $response['prezzoParziale'] = $prezzoParziale;
    $response['servizio'] = $prezzoAggiuntivo;
    $response['prezzoTotale'] = $prezzoTotale;
    $response['error'] = false;

} else {
    $response['error'] = true;
    $response['message'] = "Parametri insufficenti";
}
echo json_encode($response);
?>