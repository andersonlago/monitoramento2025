<?php
// Dados de conexão
$servername = "sql212.infinityfree.com";
$username = "if0_39143010";
$password = "WfiZ6ZbhKDzx";
$dbname = "if0_39143010_gps";
$port = 3306;

// Cria conexão
$conn = new mysqli($servername, $username, $password, $dbname, $port);

// Verifica conexão
if ($conn->connect_error) {
    die("Falha na conexão: " . $conn->connect_error);
}

// Recupera dados do POST
$codigo_unico = $_GET['codigo_unico'] ?? '';
$latitude = $_GET['latitude'] ?? 0;
$longitude = $_GET['longitude'] ?? 0;
$velocidade = $_GET['velocidade'] ?? 0;
$direcao = $_GET['direcao'] ?? 0;
$bateria = $_GET['bateria'] ?? 0;
$endereco = $_GET['endereco'] ?? 0;
$provedor = $_GET['provedor'] ?? 0;
$precisao = $_GET['precisao'] ?? 0;
$data_hora_br = $_GET['dt_hora'] ?? date('d/m/Y H:i:s'); // Exemplo: 02/06/2024 15:30:00

if  ($codigo_unico ) {
// Converte para formato MySQL
$data_hora_mysql = DateTime::createFromFormat('d/m/Y H:i:s', $data_hora_br);
if ($data_hora_mysql) {
    $dt_hora = $data_hora_mysql->format('Y-m-d H:i:s');
} else {
    $dt_hora = date('Y-m-d H:i:s'); // valor padrão se conversão falhar
}

// Prepara e executa a query
$stmt = $conn->prepare("INSERT INTO dados (codigo_unico, latitude, longitude, velocidade, dt_hora, direcao, bateria, endereco, provedor, precisao) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?)");
$stmt->bind_param("sdddsdissd", $codigo_unico, $latitude, $longitude, $velocidade, $dt_hora, $direcao, $bateria, $endereco, $provedor, $precisao);

if ($stmt->execute()) {
    echo "Dados inseridos";

} else {
    echo "Erro ao inserir:" . $stmt->error;
}
}else {
    echo "Dados não recebidos";
}

$stmt->close();
$conn->close();
?>

<!DOCTYPE html>
<html>
<head>
    <title>Mapa com OpenStreetMap</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
   <style>
        html, body {
            height: 100%;
            margin: 0;
        }

        #map {
            height: 100%;
            width: 100%;
            position: fixed; /* Fixa o mapa para ocupar a tela toda */
            top: 0;
            left: 0;
            z-index: 1; /* Garante que o mapa fique atrás do formulário */
        }

        form {
            position: absolute; /* Posiciona o formulário sobre o mapa */
            top: 5px; /* Ajuste a distância do topo */
            left: 40px; /* Ajuste a distância da esquerda */
            background-color: rgba(255, 255, 255, 0.5); /* Fundo branco semi-transparente */
            padding: 1px;
            border-radius: 1px;
            z-index: 2; /* Garante que o formulário fique na frente do mapa */
        }
    </style>
</head>
<body>
   <div id="map"></div>

    <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script>
        var map = L.map('map').setView([<?php echo $latitude; ?>, <?php echo $longitude; ?>], 18); // Recôncavo Baiano

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);

                 L.marker([<?php echo $latitude; ?>, <?php echo $longitude; ?>])
                .addTo(map)
                .bindPopup("<b>Usuário:</b> <?php echo $codigo_unico; ?><br><b>Data/Hora:</b> <?php echo $data_hora_br; ?>");

    </script>
</body>
</html>