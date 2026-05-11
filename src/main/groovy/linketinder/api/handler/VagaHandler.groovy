package linketinder.api.handler

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper
import linketinder.api.RespostaHttp
import linketinder.controller.VagaController
import linketinder.model.Competencia

import java.util.regex.Matcher
import java.util.regex.Pattern

class VagaHandler implements HttpHandler {

    private static final Pattern PADRAO_URL = ~/^\/empresas\/(\d+)\/vagas\/?$/

    private final VagaController vagaController

    VagaHandler(VagaController vagaController) {
        this.vagaController = vagaController
    }

    @Override
    void handle(HttpExchange exchange) {
        Matcher matcher = PADRAO_URL.matcher(exchange.requestURI.path)
        if (!matcher.matches()) {
            RespostaHttp.erro(exchange, 404, "Recurso não encontrado.")
            return
        }
        if (exchange.requestMethod != "POST") {
            RespostaHttp.erro(exchange, 405, "Método não permitido.")
            return
        }

        try {
            int idEmpresa = matcher.group(1).toInteger()
            String corpo = exchange.requestBody.getText("UTF-8")
            Map dados = new JsonSlurper().parseText(corpo) as Map

            List<Competencia> competencias = (dados.competencias as List ?: [])
                    .collect { new Competencia(it as String) }

            vagaController.criar(idEmpresa, dados, competencias)
            RespostaHttp.enviar(exchange, 201, [mensagem: "Vaga criada.", idEmpresa: idEmpresa])
        } catch (IllegalArgumentException e) {
            RespostaHttp.erro(exchange, 400, e.message)
        } catch (Exception e) {
            RespostaHttp.erro(exchange, 500, "Erro interno: " + e.message)
        }
    }
}