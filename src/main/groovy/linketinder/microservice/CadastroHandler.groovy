package linketinder.microservice

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import linketinder.model.Candidato
import linketinder.model.Empresa
import linketinder.service.CandidatoService
import linketinder.service.EmpresaService

class CadastroHandler implements HttpHandler {

    private final CandidatoService candidatoService
    private final EmpresaService   empresaService

    CadastroHandler(CandidatoService candidatoService, EmpresaService empresaService) {
        this.candidatoService = candidatoService
        this.empresaService   = empresaService
    }

    @Override
    void handle(HttpExchange exchange) throws IOException {
        adicionarHeadersCors(exchange)

        if (exchange.requestMethod == "OPTIONS") {
            responder(exchange, 204, "")
            return
        }

        if (exchange.requestMethod != "POST") {
            responder(exchange, 405, JsonOutput.toJson([erro: "Método não permitido"]))
            return
        }

        String path = exchange.requestURI.path
        String body = exchange.requestBody.text

        try {
            switch (path) {
                case "/cadastro/candidato": cadastrarCandidato(exchange, body); break
                case "/cadastro/empresa":   cadastrarEmpresa(exchange, body);   break
                default: responder(exchange, 404, JsonOutput.toJson([erro: "Rota não encontrada"]))
            }
        } catch (IllegalArgumentException e) {
            responder(exchange, 400, JsonOutput.toJson([erro: e.message]))
        } catch (Exception e) {
            responder(exchange, 500, JsonOutput.toJson([erro: "Erro interno: ${e.message}"]))
        }
    }

    private void cadastrarCandidato(HttpExchange exchange, String body) {
        def dados = new JsonSlurper().parseText(body)

        Candidato candidato = new Candidato.Builder()
                .nome(dados.nome as String)
                .email(dados.email as String)
                .cpf(dados.cpf as String)
                .idade(dados.idade as int)
                .estado(dados.estado as String)
                .cep(dados.cep as String)
                .descricao(dados.descricao as String)
                .senha(dados.senha as String)
                .competencias([])
                .build()

        candidatoService.cadastrar(candidato)
        responder(exchange, 201, JsonOutput.toJson([mensagem: "Candidato cadastrado com sucesso", id: candidato.id]))
    }

    private void cadastrarEmpresa(HttpExchange exchange, String body) {
        def dados = new JsonSlurper().parseText(body)

        Empresa empresa = new Empresa.Builder()
                .nome(dados.nome as String)
                .email(dados.email as String)
                .cnpj(dados.cnpj as String)
                .pais(dados.pais as String)
                .estado(dados.estado as String)
                .cep(dados.cep as String)
                .descricao(dados.descricao as String)
                .senha(dados.senha as String)
                .build()

        empresaService.cadastrar(empresa)
        responder(exchange, 201, JsonOutput.toJson([mensagem: "Empresa cadastrada com sucesso", id: empresa.id]))
    }

    private static void adicionarHeadersCors(HttpExchange exchange) {
        exchange.responseHeaders.add("Access-Control-Allow-Origin", "*")
        exchange.responseHeaders.add("Access-Control-Allow-Methods", "POST, OPTIONS")
        exchange.responseHeaders.add("Access-Control-Allow-Headers", "Content-Type")
        exchange.responseHeaders.add("Content-Type", "application/json; charset=UTF-8")
    }

    private static void responder(HttpExchange exchange, int status, String corpo) {
        byte[] bytes = corpo.getBytes("UTF-8")
        exchange.sendResponseHeaders(status, bytes.length)
        exchange.responseBody.write(bytes)
        exchange.responseBody.close()
    }
}