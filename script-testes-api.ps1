# ==============================================================================
# Script de Testes Automatizados da API (Mercado Express + Banco Tranquilo + RabbitMQ)
# Checkpoint 5 - Parte 2 (FIAP - Java Advanced)
# ==============================================================================

$baseUrl = "http://localhost:8082"

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host " 1. Verificando Status da API do Banco Tranquilo e Filas RabbitMQ" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
$status = Invoke-RestMethod -Uri "$baseUrl/api/banco-tranquilo/status" -Method Get
$status | ConvertTo-Json -Depth 4 | Write-Host -ForegroundColor Green

Write-Host "`n=================================================================" -ForegroundColor Cyan
Write-Host " 2. Enviando Requisição de Compra JSON (Aprovada via PIX)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
$compraAprovadaJson = @{
    nomeCliente = "Leandro Guarido de Oliveira"
    emailCliente = "leandro.guarido@fiap.com.br"
    cpfCliente = "123.456.789-00"
    metodoPagamento = "PIX"
    numeroCartao = ""
    itens = @(
        @{ produtoId = 1; quantidade = 2 },
        @{ produtoId = 3; quantidade = 1 }
    )
} | ConvertTo-Json -Depth 4

$respostaAprovada = Invoke-RestMethod -Uri "$baseUrl/api/compras" -Method Post -ContentType "application/json" -Body $compraAprovadaJson
$respostaAprovada | ConvertTo-Json -Depth 5 | Write-Host -ForegroundColor Green

Write-Host "`n=================================================================" -ForegroundColor Cyan
Write-Host " 3. Enviando Requisição de Compra JSON (Recusada - Cartão 0000)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
$compraRecusadaJson = @{
    nomeCliente = "Cliente Cartão Bloqueado"
    emailCliente = "bloqueado@fiap.com.br"
    cpfCliente = "999.888.777-66"
    metodoPagamento = "CARTAO_CREDITO"
    numeroCartao = "5432 1098 7654 0000"
    itens = @(
        @{ produtoId = 6; quantidade = 1 }
    )
} | ConvertTo-Json -Depth 4

$respostaRecusada = Invoke-RestMethod -Uri "$baseUrl/api/compras" -Method Post -ContentType "application/json" -Body $compraRecusadaJson
$respostaRecusada | ConvertTo-Json -Depth 5 | Write-Host -ForegroundColor Yellow

Write-Host "`n=================================================================" -ForegroundColor Cyan
Write-Host " 4. Consultando E-mails Armazenados no BD (TDS_TB_Emails_Enviados)" -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan
$emails = Invoke-RestMethod -Uri "$baseUrl/api/emails" -Method Get
$emails | Select-Object id, codigoTransacao, destinatario, assunto, statusEnvio, dataEnvio | Format-Table -AutoSize
