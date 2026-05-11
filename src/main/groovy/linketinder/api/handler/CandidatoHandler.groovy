package linketinder.api.handler
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import groovy.json.JsonSlurper
import linketinder.api.RespostaHttp
import linketinder.controller.CandidatoController
import linketinder.model.Candidato

class CandidatoHandler implements HttpHandler {

    private final CandidatoController candidatoController

    CandidatoHandler(CandidatoController candidatoController) {
        this.candidatoController = candidatoController
    }

    @Override
    void handle(HttpExchange exchange) {
        if (exchange.requestMethod != "POST") {
            RespostaHttp.erro(exchange, 405, "Método não permitido.")
            return
        }

        try {
            String corpo = exchange.requestBody.getText("UTF-8")
            Map dados = new JsonSlurper().parseText(corpo) as Map
            Candidato candidato = candidatoController.cadastrar(dados)
            RespostaHttp.enviar(exchange, 201, [id: candidato.id, email: candidato.email])
        } catch (IllegalArgumentException e) {
            RespostaHttp.erro(exchange, 400, e.message)
        } catch (Exception e) {
            RespostaHttp.erro(exchange, 500, "Erro interno: " + e.message)
        }
    }
}