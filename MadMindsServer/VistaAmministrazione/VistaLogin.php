<!DOCTYPE html>
<html lang="en">
<head>
    <title>CiceroneAdmin</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="https://www.madminds.tk/VistaAmministrazione/logoSenzaSfondo.png.png" />
    <meta name="theme-color" content="#298282">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:400,600" />
    <link rel="stylesheet" href="css/all.min.css" />
    <link rel="stylesheet" href="css/bootstrap.min.css" />
    <link rel="stylesheet" href="css/templatemo-style.css" />
    <!--===============================================================================================-->
    <link rel="icon" type="image/png" href="images/icons/favicon.ico"/>
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="vendor/bootstrap/css/bootstrap.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.7.0/css/font-awesome.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="fonts/Linearicons-Free-v1.0.0/icon-font.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="vendor/animate/animate.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="vendor/css-hamburgers/hamburgers.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="vendor/animsition/css/animsition.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="vendor/select2/select2.min.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="vendor/daterangepicker/daterangepicker.css">
    <!--===============================================================================================-->
    <link rel="stylesheet" type="text/css" href="css/util.css">
    <link rel="stylesheet" type="text/css" href="css/main.css">
    <!--===============================================================================================-->
</head>
<body style="background-color: #298282;">

<div class="parallax-window" data-parallax="scroll" style="background-color: : #298282">
    <div class="container-fluid" style="background-color: : #298282">


        <!-- Features -->
        <div class="row" id="tmFeatures">

            <div class="tm-bg-white-transparent tm-feature-box" style="max-width: 650px">
                <img src="images/logoSenzaSfondo.png" alt="Cicerone" style="width: 250px; padding-bottom:0px; margin-left: auto; margin-right: auto;">
                <div style=" color:black; font-size: 14px;">
                    In un mondo sempre più globalizzato e con compagnie di volo low cost, decine e
                    decine di siti di prenotazione online di hotel, ristoranti, biglietti aerei e ferroviari, cresce
                    sempre più nella gente il desiderio di viaggiare, esplorando nuove città ricche di vicoli
                    caratteristici, skyline mozzafiato, di vedere nuove culture e ascoltare nuove lingue.<br/><br/>
                    Questa peculiarità porta alla realizzazione di “Cicerone” una piattaforma in grado di
                    permettere ai vari Globetrotter, ovvero coloro che viaggiano per il mondo, di cercare
                    varie attività pubblicate dai Ciceroni. In questa figura possono identificarsi, oltre alle
                    guide turistiche vere e proprie, delle persone comuni che offrono il loro tempo, la loro
                    conoscenza della città e delle varie attività presenti in essa, per metterli a
                    disposizione di tutti i Globetrotter in cambio di denaro.<br/><br/>
                    Le attività che i Ciceroni possono pubblicare sono del più disparato tipo, dal classico
                    giro turistico della città passando per tutti i punti di maggiore interesse. Dalle piazze
                    più famose, piuttosto che un giro dentro il centro storico alla ricerca dei vicoli più
                    suggestivi o ancora una singola visita a un luogo memoriale e caratterizzante della
                    città stessa.

                    <div class="container-login100-form-btn" style="width: 50%; margin-top: 40px; margin-left: auto; margin-right: auto">
                        <button class="login100-form-btn" name="downloadApk" onclick="window.location.href = 'https://madminds.tk/VistaAmministrazione/cicerone.apk'">
                            Download APK
                        </button>
                    </div>
                </div>
            </div>



            <div class="tm-bg-white-transparent tm-feature-box" style="max-width: 650px">
                <form  name="modulo" action="../GestoreAmministrazione/GestoreLoginAmministrazione.php" method="post" style="margin-top: 50px">
                    <span class="login100-form-title p-b-43">
                         <h3 class="tm-feature-name">Login Amministrazione</h3>
                    </span>


                    <div class="wrap-input100 validate-input" data-validate = "Email non valida: ex@abc.xyz" style="background-color: white ">
                        <input class="input100" type="text" name="email" >
                        <span class="focus-input100"></span>
                        <span class="label-input100">Email</span>
                    </div>


                    <div class="wrap-input100 validate-input" data-validate="Password obbligatoria"  style="background-color: white ">
                        <input class="input100" type="password" name="password" >
                        <span class="focus-input100"></span>
                        <span class="label-input100">Password</span>
                    </div>


                    <div class="container-login100-form-btn">
                        <button class="login100-form-btn" type="submit" name="btlogin">
                            Login
                        </button>
                    </div>
                </form>
                <?php
                    $messaggio=$_GET['messaggio'];
                    echo $messaggio;
                ?>
            </div>


        </div>

    </div>
    <!-- .container-fluid -->
</div>








<!--===============================================================================================-->
<script src="vendor/jquery/jquery-3.2.1.min.js"></script>
<!--===============================================================================================-->
<script src="vendor/animsition/js/animsition.min.js"></script>
<!--===============================================================================================-->
<script src="vendor/bootstrap/js/popper.js"></script>
<script src="vendor/bootstrap/js/bootstrap.min.js"></script>
<!--===============================================================================================-->
<script src="vendor/select2/select2.min.js"></script>
<!--===============================================================================================-->
<script src="vendor/daterangepicker/moment.min.js"></script>
<script src="vendor/daterangepicker/daterangepicker.js"></script>
<!--===============================================================================================-->
<script src="vendor/countdowntime/countdowntime.js"></script>
<!--===============================================================================================-->
<script src="js/main.js"></script>
<script src="js/jquery.min.js"></script>
<script src="js/parallax.min.js"></script>
<script src="js/bootstrap.min.js"></script>

</body>
</html>