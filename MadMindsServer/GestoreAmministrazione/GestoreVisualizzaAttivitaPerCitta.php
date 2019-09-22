<?php

require_once '../Amministrazione.php';
$amministratore = new Amministrazione();
$array = $amministratore->visualizzaAttivitaPerCitta();

echo json_encode($array['nAttivita']);
echo json_encode($array['citta']);

?>
